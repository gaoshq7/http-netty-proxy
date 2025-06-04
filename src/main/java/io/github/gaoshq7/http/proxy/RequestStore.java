package io.github.gaoshq7.http.proxy;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.RequestStore
 *
 * @author : gsq
 * @date : 2025-06-04 16:52
 * @note : It's not technology, it's art !
 **/
public class RequestStore {

    private final static Map<Channel, FullRequest> store = Maps.newConcurrentMap();

    public final static void save(Channel channel, HttpObject httpObject) {
        if (!store.containsKey(channel)) {
            FullRequest request = new FullRequest();
            store.put(channel, request);
        }
        if (httpObject instanceof HttpRequest) {
            store.get(channel).setRequest((HttpRequest) httpObject);
        }
        if (httpObject instanceof HttpContent) {
            FullRequest request = store.get(channel);
            if (CollUtil.isEmpty(request.getContents())) {
                request.setContents(new ArrayList<>());
            }
            request.getContents().add((HttpContent) httpObject);
        }
    }

    public final static DefaultFullHttpRequest getFull(Channel channel) {
        FullRequest request = store.get(channel);
        HttpRequest requestHeader = request.getRequest();
        ByteBuf byteBuf = Unpooled.buffer();
        if (CollUtil.isNotEmpty(request.getContents())) {
            request.getContents().forEach(i -> {
                if (i.content().readableBytes() > 0) {
                    byteBuf.writeBytes(i.content());
                }
                ReferenceCountUtil.release(i);
            });
        }
        DefaultFullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(requestHeader.protocolVersion(), requestHeader.method(), requestHeader.uri(), byteBuf, requestHeader.headers(), requestHeader.headers());
        store.remove(channel);
        //ReferenceCountUtil.release(msg);
        ReferenceCountUtil.release(request.getRequest());
        return fullHttpRequest;
    }

}
