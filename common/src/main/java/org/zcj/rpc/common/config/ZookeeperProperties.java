package org.zcj.rpc.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 17 07
 * Description:
 */
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {

    // 默认连接地址
    private String url = "127.0.0.1:2181";

    // 默认超时时间
    private Integer sessionTimeOut = 30000;

    // 命名空间
    private String namespace = "test";

    // 所有数据的根节点
    public static final String root = "rpcroot";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(Integer sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public static String getRoot() {
        return root;
    }
}
