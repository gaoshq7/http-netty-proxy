package io.github.gaoshq7.http.proxy;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.ProxyHandle
 *
 * @author : gsq
 * @date : 2025-06-04 16:42
 * @note : It's not technology, it's art !
 **/
public interface ProxyHandle {

    void handle(Channel proxyRequestChannel, HttpRequest request);

}
