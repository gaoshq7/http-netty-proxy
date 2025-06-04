package io.github.gaoshq7.http.proxy;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.FullResponse
 *
 * @author : gsq
 * @date : 2025-06-04 17:06
 * @note : It's not technology, it's art !
 **/
@Getter
@Setter
public class FullResponse {

    private HttpResponse response;

    private List<HttpContent> contents;

}
