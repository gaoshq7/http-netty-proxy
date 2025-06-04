package io.github.gaoshq7.http.proxy;

import org.junit.Test;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.HttpProxyTest
 *
 * @author : gsq
 * @date : 2025-06-04 17:11
 * @note : It's not technology, it's art !
 **/
public class HttpProxyTest {

    private HttpProxyBootstrap bootstrap;

    @Test
    public void start() throws InterruptedException {
        this.bootstrap = new HttpProxyBootstrap();
        this.bootstrap.start();
    }

}
