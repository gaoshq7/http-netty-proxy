package io.github.gaoshq7.http.proxy;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.FullRequest
 *
 * @author : gsq
 * @date : 2025-06-04 16:56
 * @note : It's not technology, it's art !
 **/
@Getter
@Setter
public class FullRequest {

    private HttpRequest request;

    private List<HttpContent> contents;

}
