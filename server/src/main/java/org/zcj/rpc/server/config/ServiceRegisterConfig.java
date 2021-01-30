package org.zcj.rpc.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.zcj.rpc.annotation.annotation.RpcService;
import org.zcj.rpc.common.config.NettyProperties;
import org.zcj.rpc.common.config.ZookeeperProperties;
import org.zcj.rpc.common.utils.IpUtil;
import org.zcj.rpc.common.utils.ZKUtils;
import org.zcj.rpc.common.utils.ZookeeperCache;
import org.zcj.rpc.server.zookeeper.ZookeeperListener;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 17 13
 * Description:
 */
@Configuration
public class ServiceRegisterConfig implements ApplicationContextAware, ApplicationListener<ContextClosedEvent> {

    Logger logger = LoggerFactory.getLogger(ServiceRegisterConfig.class);

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    @Autowired
    private NettyProperties nettyProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ZKUtils zkUtils = new ZKUtils(this.zookeeperProperties);
        // 启动ZK客户端
        zkUtils.start();
        String ip = IpUtil.getIp();
        int port = nettyProperties.getServerPort();
        StringBuilder root = new StringBuilder();
        root.append("/").append(ZookeeperProperties.root);

        StringBuilder nameSpace = new StringBuilder();
        nameSpace.append("/").append(ZookeeperProperties.root).append("/")
                .append(zookeeperProperties.getNamespace());
        // 获取所有服务实现类
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (beansWithAnnotation != null && beansWithAnnotation.size() > 0) {
            beansWithAnnotation.forEach((key, serviceBean) -> {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                String version = serviceBean.getClass().getAnnotation(RpcService.class).version();
                StringBuilder path = new StringBuilder();
                path.append("/").append(interfaceName).append(version);
                zkUtils.createPersistentNodeByRecursion(nameSpace.toString() + path.toString(), (ip + "-" + String.valueOf(port)).getBytes());
            });
        }

        // 获取所有的服务
        List<String> servers = zkUtils.getChildsByPath(root.toString());
        for (String server : servers) {
            if (!zookeeperProperties.getNamespace().equalsIgnoreCase(server)) {
                // 每个服务下面所有的接口
                List<String> services = zkUtils.getChildsByPath(root.toString() + "/" + server);
                for (String service : services) {
                   if (!ZookeeperCache.getServiceList().containsKey(service)) {
                       ZookeeperCache.getServiceList().put(service, new HashSet<>());
                   }
                    Set<String> ipPorts = ZookeeperCache.getServiceList().get(service);
                    // ip - port
                    String ipPort = new String(zkUtils.getData(root.toString() + "/" + server + "/" + service));
                    ipPorts.add(ipPort);
                }
            }
        }
        zkUtils.close();

        Thread thread = new Thread(new ZookeeperListener(this.zookeeperProperties), "ZookeeperListener-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        logger.info("服务停止...");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/").append(ZookeeperProperties.root)
                .append("/").append(zookeeperProperties.getNamespace());
        ZKUtils zkUtils = new ZKUtils(this.zookeeperProperties);
        zkUtils.start();
        zkUtils.delNodeByRecursion(stringBuilder.toString());
    }
}
