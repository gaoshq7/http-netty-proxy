package io.github.gaoshq7.http.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.HttpsProxyChannelInboundHandle
 *
 * @author : gsq
 * @date : 2025-06-04 17:03
 * @note : It's not technology, it's art !
 **/
public class HttpsProxyChannelInboundHandle extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(HttpsProxyChannelInboundHandle.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof byte[]) {
                Channel clientChannel = ProxyRequestClientChannelMap.getClientChannel(ctx.channel());
//                clientChannel.writeAndFlush(msg).addListener(future -> log.info("转发[https]proxyRequest数据成功;proxyRequestChannelHash={},proxyClientChannelHash={}", ctx.channel().hashCode(), clientChannel.hashCode()));
                clientChannel.writeAndFlush(msg).addListener(future -> {});
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        log.info("用户关闭连接;host={}", new Gson().toJson(ctx.channel().remoteAddress()));
        ProxyRequestClientChannelMap.closeByProxyRequest(ctx.channel());
    }

}
