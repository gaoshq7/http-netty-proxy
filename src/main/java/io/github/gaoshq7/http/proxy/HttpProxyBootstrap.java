package io.github.gaoshq7.http.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.HttpProxyBootstrap
 *
 * @author : gsq
 * @date : 2025-06-04 16:09
 * @note : It's not technology, it's art !
 **/
public class HttpProxyBootstrap {

    private ServerBootstrap bootstrap;  // netty启动器

    private EventLoopGroup boss;   // 连接线程池

    private EventLoopGroup worker;  // 数据处理线程池

    private ChannelFuture future;   // 信道管理

    public void start() throws InterruptedException {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast("request", new HttpRequestDecoder())
                                .addLast("response", new HttpResponseEncoder())
                                .addLast("handle", new ProxyChannelInboundHandle("172.22.1.211", 17000));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        // 不阻塞
        future = bootstrap.bind(9090).sync();
        future.channel().closeFuture().sync();
    }

}
