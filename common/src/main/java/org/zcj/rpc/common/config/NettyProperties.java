package org.zcj.rpc.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 17 03
 * Description:
 */
@ConfigurationProperties(prefix = "rpc.netty")
public class NettyProperties {

    private String clientIp = "127.0.0.1";


    private Integer serverPort = 20880;

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }


    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }
}
