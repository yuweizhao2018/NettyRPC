package org.zcj.rpc.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.zcj.rpc.server.netty.NettyServer;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 16 48
 * Description:
 */
@Configuration
public class NettyServerInitConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private NettyServer nettyServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        nettyServer.openServer();
    }
}
