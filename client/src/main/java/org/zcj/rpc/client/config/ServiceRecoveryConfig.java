package org.zcj.rpc.client.config;

import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.zcj.rpc.client.netty.ChannelPool;
import org.zcj.rpc.client.netty.NettyClient;
import org.zcj.rpc.common.config.ZookeeperProperties;
import org.zcj.rpc.common.utils.ZKUtils;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 11 00
 * Description:
 */
@Component
public class ServiceRecoveryConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        discoverService();
    }

    public void discoverService() {
        ZKUtils zkUtils = new ZKUtils(new ZookeeperProperties());
        zkUtils.start();
        for (String service : ChannelPool.service) {
            String servicePath = "/zookeeperProperties.root" + "/" + zookeeperProperties.getNamespace() + "/" + service;
            String ipPorts = new String(zkUtils.getData(servicePath));
            String[] split = ipPorts.split("-");
            Channel channel = NettyClient.createChannel(split[0], Integer.parseInt(split[1]));
            ChannelPool.addChannel(ipPorts, channel);
            ChannelPool.addServiceIp(service, ipPorts);
        }
    }
}
