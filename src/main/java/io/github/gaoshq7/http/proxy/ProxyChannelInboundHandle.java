package io.github.gaoshq7.http.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.LastHttpContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.ProxyChannelInboundHandle
 *
 * @author : gsq
 * @date : 2025-06-04 16:16
 * @note : It's not technology, it's art !
 **/
public class ProxyChannelInboundHandle extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(ProxyChannelInboundHandle.class);

    private final ProxyHandle handle;

    public ProxyChannelInboundHandle(String hostname, Integer httpPort){
        handle = new SimpleHttpProxyHandle(hostname, httpPort);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof HttpObject) {
                RequestStore.save(ctx.channel(), (HttpObject) msg);
                if (msg instanceof LastHttpContent) {
                    FullHttpRequest request = RequestStore.getFull(ctx.channel());
                    handle.handle(ctx.channel(), request);
                }
            }
        } finally {
            //ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        log.info("用户关闭连接;host={}", new Gson().toJson(ctx.channel().remoteAddress()));
        ProxyRequestClientChannelMap.closeByProxyRequest(ctx.channel());
    }

}
