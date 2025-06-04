package io.github.gaoshq7.http.proxy;

import lombok.Getter;
import lombok.Setter;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.HostInfo
 *
 * @author : gsq
 * @date : 2025-06-04 17:00
 * @note : It's not technology, it's art !
 **/
@Getter
@Setter
public class HostInfo {

    private String host;

    private Integer port;

    private String ip;

    @Override
    public String toString() {
        return "HostInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", ip='" + ip + '\'' +
                '}';
    }

}
