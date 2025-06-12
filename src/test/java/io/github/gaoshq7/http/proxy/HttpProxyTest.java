package io.github.gaoshq7.http.proxy;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import org.junit.Test;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

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

    private final String hostname = "172.22.1.211";

    private final int port = 17000;

    private final int exit = 9999;

    ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(5)
            .setMaxPoolSize(10)
            .setWorkQueue(new LinkedBlockingQueue<>(100))
            .build();

    @Test
    public void start_without_block() throws InterruptedException {
        this.bootstrap = new HttpProxyBootstrap();
        this.bootstrap.config(new HttpProxyServerConfig(this.hostname, this.port, this.exit, null, null, false));
        this.bootstrap.start();
        System.out.println("http代理服务启动成功，手动进行阻塞！");
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

    @Test
    public void start_with_block() {
        this.bootstrap = new HttpProxyBootstrap();
        this.bootstrap.config(new HttpProxyServerConfig(this.hostname, this.port, this.exit, null, null, true));
        this.bootstrap.start();
        System.out.println("此处不应输出！");
    }

    @Test
    public void start_without_ssl() {
        this.bootstrap = new HttpProxyBootstrap();
        this.bootstrap.config(
                new HttpProxyServerConfig(this.hostname, this.port, this.exit, null, null, true)
        ).start();
        System.out.println("此处不应输出！");
    }

    @Test
    public void start_with_ssl() {
        InputStream cert = HttpProxyTest.class.getClassLoader().getResourceAsStream("emr.org.pem");
        InputStream key = HttpProxyTest.class.getClassLoader().getResourceAsStream("emr.org-key.pem");
        this.bootstrap = new HttpProxyBootstrap();
        this.bootstrap.config(
                new HttpProxyServerConfig(this.hostname, this.port, this.exit, cert, key, true)
        ).start();
        System.out.println("此处不应输出！");
    }

    @Test
    public void stop_with_block() {
        InputStream cert = HttpProxyTest.class.getClassLoader().getResourceAsStream("emr.org.pem");
        InputStream key = HttpProxyTest.class.getClassLoader().getResourceAsStream("emr.org-key.pem");
        this.executor.submit(() -> {
            this.bootstrap = new HttpProxyBootstrap();
            System.out.println("http代理服务将要启动...");
            this.bootstrap.config(
                    new HttpProxyServerConfig(this.hostname, this.port, this.exit, cert, key, true)
            ).start();
            System.out.println("http代理服务已被关闭...");
        });
        ThreadUtil.safeSleep(5000);
        System.out.println("开启后服务的状态是：" + this.bootstrap.isActive());
        this.bootstrap.stop();
        ThreadUtil.safeSleep(3000);
        System.out.println("关闭后后服务的状态是：" + this.bootstrap.isActive());
    }

    @Test
    public void stop_without_block() {
        this.bootstrap = new HttpProxyBootstrap();
        this.bootstrap.config(
                new HttpProxyServerConfig(this.hostname, this.port, this.exit, null, null, false)
        ).start();
        ThreadUtil.safeSleep(5000);
        System.out.println("开启后服务的状态是：" + this.bootstrap.isActive());
        this.bootstrap.stop();
        ThreadUtil.safeSleep(3000);
        System.out.println("关闭后后服务的状态是：" + this.bootstrap.isActive());
    }

    @Test
    public void start_stop_start() {
        this.executor.submit(() -> {
            this.bootstrap = new HttpProxyBootstrap();
            System.out.println("http代理服务将要启动...");
            this.bootstrap.config(
                    new HttpProxyServerConfig(
                            this.hostname, this.port, this.exit,
                            HttpProxyTest.class.getClassLoader().getResourceAsStream("emr.org.pem"),
                            HttpProxyTest.class.getClassLoader().getResourceAsStream("emr.org-key.pem"),
                            true
                    )
            ).start();
            System.out.println("http代理服务已被关闭...");
        });
        ThreadUtil.safeSleep(5000);
        System.out.println("开启后服务的状态是：" + this.bootstrap.isActive());
        this.bootstrap.stop();
        ThreadUtil.safeSleep(3000);
        System.out.println("关闭后后服务的状态是：" + this.bootstrap.isActive());
        System.out.println("再次启动中...");
        this.bootstrap.config(
                new HttpProxyServerConfig(
                        this.hostname, this.port, this.exit,
                        HttpProxyTest.class.getClassLoader().getResourceAsStream("emr.org.pem"),
                        HttpProxyTest.class.getClassLoader().getResourceAsStream("emr.org-key.pem"),
                        true
                )
        );
        this.bootstrap.start();
        System.out.println("此处不应输出！");
    }

}
