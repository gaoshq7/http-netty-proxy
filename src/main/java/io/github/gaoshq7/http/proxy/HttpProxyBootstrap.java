package io.github.gaoshq7.http.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.HttpProxyBootstrap
 *
 * @author : gsq
 * @date : 2025-06-04 16:09
 * @note : It's not technology, it's art !
 **/
public class HttpProxyBootstrap {

    private HttpProxyServerConfig serverConfig;

    private ServerBootstrap bootstrap;  // netty启动器

    private EventLoopGroup boss;   // 连接线程池

    private EventLoopGroup worker;  // 数据处理线程池

    private ChannelFuture future;   // 信道管理

    public HttpProxyBootstrap config(HttpProxyServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public void start() {
        try {
            if(isActive()) throw new RuntimeException("proxy already started");
            init();
            this.future = bindProxy(this.serverConfig.getIp(), this.serverConfig.getPort(), this.serverConfig.getExit());
            this.future.addListener(future -> {
                if (future.cause() != null) {
                    future.cause().printStackTrace();
                }
            });
            if (this.serverConfig.isBlock()) {
                this.future.channel().closeFuture().sync();
                stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    public void stop() {
        if (this.future != null && this.future.channel().isOpen()) {
            this.future.channel().close();
        }
        if (this.boss != null && !(this.boss.isShutdown() || this.boss.isShuttingDown())) {
            this.boss.shutdownGracefully();
        }
        if (this.worker != null && !(this.worker.isShutdown() || this.worker.isShuttingDown())) {
            this.worker.shutdownGracefully();
        }
    }

    public boolean isActive() {
        return this.future != null && future.channel().isOpen();
    }

    private void init() {
        if (serverConfig == null) {
            serverConfig = new HttpProxyServerConfig("127.0.0.1", 8080, 8080, null, null, true);
        }
        this.boss = new NioEventLoopGroup();
        this.worker = new NioEventLoopGroup();
        this.bootstrap = new ServerBootstrap();
    }

    private ChannelFuture bindProxy(String ip, int port, int exit) throws InterruptedException, SSLException {
        SslContext sslCtx = null;
        if (this.serverConfig.isSsl()) {
            sslCtx = SslContextBuilder.forServer(this.serverConfig.getCert(), this.serverConfig.getKey()).build();
        }
        final SslContext finalSslCtx = sslCtx;
        this.bootstrap.group(this.boss, this.worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        if (finalSslCtx != null) {
                            pipeline.addLast(finalSslCtx.newHandler(ch.alloc()));
                        }
                        pipeline.addLast("request", new HttpRequestDecoder());
                        pipeline.addLast("response", new HttpResponseEncoder());
                        pipeline.addLast("handle", new ProxyChannelInboundHandle(ip, port));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        return this.bootstrap.bind(exit).sync();
    }

}
