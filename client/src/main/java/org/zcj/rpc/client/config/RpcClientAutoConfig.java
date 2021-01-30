package org.zcj.rpc.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zcj.rpc.common.config.NettyProperties;
import org.zcj.rpc.common.config.ZookeeperProperties;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 16 29
 * Description:
 */
@Configuration
@EnableConfigurationProperties({NettyProperties.class, ZookeeperProperties.class})
public class RpcClientAutoConfig {


}
