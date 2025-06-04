package io.github.gaoshq7.http.proxy;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.ProxyRequestClientChannelMap
 *
 * @author : gsq
 * @date : 2025-06-04 16:53
 * @note : It's not technology, it's art !
 **/
public class ProxyRequestClientChannelMap {

    private final static Logger log = LoggerFactory.getLogger(ProxyRequestClientChannelMap.class);

    private final static Map<Channel, Channel> clientPool = Maps.newConcurrentMap();

    private final static Map<Channel, Channel> requestPool = Maps.newConcurrentMap();

    public static void bind(Channel channel, Channel clientChannel) {
        clientPool.put(channel, clientChannel);
        requestPool.put(clientChannel, channel);
    }

    public static Channel getClientChannel(Channel channel) {
        return clientPool.get(channel);
    }

    public static Channel getRequestChannel(Channel channel) {
        return requestPool.get(channel);
    }

    public static void closeByProxyClient(Channel channel) {
        Channel proxyRequestChannel = requestPool.get(channel);
//        log.info("远程服务关闭连接,关闭用户的连接");
        requestPool.remove(channel);
        if (proxyRequestChannel != null) {
            proxyRequestChannel.close();
            clientPool.remove(proxyRequestChannel);
        }
    }

    public static void closeByProxyRequest(Channel channel) {
        Channel proxyClientChannel = clientPool.get(channel);
//        log.info("用户连接关闭,关闭远程服务连接");
        clientPool.remove(channel);
        if (proxyClientChannel != null) {
            requestPool.remove(proxyClientChannel);
            proxyClientChannel.close();
        }
    }

}
