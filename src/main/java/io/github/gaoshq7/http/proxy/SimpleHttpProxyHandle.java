package io.github.gaoshq7.http.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.SimpleHttpProxyHandle
 *
 * @author : gsq
 * @date : 2025-06-04 16:47
 * @note : It's not technology, it's art !
 **/
public class SimpleHttpProxyHandle implements ProxyHandle {

    private final String hostname;

    private final Integer httpPort;

    private final static Logger log = LoggerFactory.getLogger(SimpleHttpProxyHandle.class);

    public SimpleHttpProxyHandle(String hostname, Integer httpPort){
        this.hostname = hostname;
        this.httpPort = httpPort;
    }

    @Override
    public void handle(Channel proxyRequestChannel, HttpRequest request) {

        Channel clientChannel = ProxyRequestClientChannelMap.getClientChannel(proxyRequestChannel);
        if (null != clientChannel) {
            clientChannel.writeAndFlush(request);
            return;
        }

        Bootstrap bootstrap;
        boolean https = request.method() == HttpMethod.CONNECT;
        if (https) {
            bootstrap = SimpleHttpProxyBootstrapFactory.httpsBootstrap();
        } else {
            bootstrap = SimpleHttpProxyBootstrapFactory.httpBootstrap();
        }
        String uri = request.uri();
        if (https) {
            uri = "https://" + uri;
        }
        //HostInfo hostInfo = HostParseUtils.parseHost(uri);
        HostInfo hostInfo = new HostInfo();
        hostInfo.setHost(this.hostname);
        hostInfo.setPort(httpPort);

        try {
            ChannelFuture f = bootstrap.connect(hostInfo.getHost(), hostInfo.getPort()).sync();
            ProxyRequestClientChannelMap.bind(proxyRequestChannel, f.channel());

            // https
            if (https) {
                FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
                proxyRequestChannel.writeAndFlush(response).addListener(future -> {
                    proxyRequestChannel.pipeline().remove("request");
                    proxyRequestChannel.pipeline().remove("response");
                    proxyRequestChannel.pipeline().remove("handle");
                    proxyRequestChannel.pipeline()
                            .addLast(new ByteArrayEncoder())
                            .addLast(new ByteArrayDecoder())
                            .addLast(new HttpsProxyChannelInboundHandle());
                });
            } else {
                // http
                f.channel().writeAndFlush(request);
            }

        } catch (InterruptedException e) {
            log.error("尝试连接目标服务器时异常", e);
        }
    }

}
