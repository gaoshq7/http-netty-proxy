package io.github.gaoshq7.http.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.InputStream;

/**
 * Project : http-netty-proxy
 * Class : io.github.gaoshq7.http.proxy.HttpProxyServerConfig
 *
 * @author : gsq
 * @date : 2025-06-05 11:51
 * @note : It's not technology, it's art !
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class HttpProxyServerConfig {

    private String ip;  // 被代理主机ip

    private int port;   // 被代理服务端口

    private int exit;   // 代理端口

    private InputStream cert;   // https协议公钥

    private InputStream key;   // https协议私钥

    private boolean block;  // 启动代理是否阻塞

    public boolean isSsl() {
        return this.cert != null && this.key != null;
    }

}
