
package org.zcj.rpc.server.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zcj.rpc.common.config.ZookeeperProperties;
import org.zcj.rpc.common.utils.ZKUtils;
import org.zcj.rpc.common.utils.ZookeeperCache;

import java.util.List;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 17 13
 * Description:
 */
public class ZookeeperListener implements Runnable{

    private Logger log = LoggerFactory.getLogger(ZookeeperListener.class);

    private ZookeeperProperties zookeeperProperties;

    private CuratorFramework zkClient;

    private ZKUtils zkUtils;

    public ZookeeperListener(ZookeeperProperties zookeeperProperties){
        this.zookeeperProperties = zookeeperProperties;
    }

    @Override
    public void run() {
        try {
            // 根节点下的所有应用列表
            StringBuilder root = new StringBuilder();
            root.append("/").append(ZookeeperProperties.root);
            this.zkUtils = new ZKUtils(this.zookeeperProperties);
            this.zkUtils.start();
            List<String> servers = zkUtils.getChildsByPath(root.toString());
            // 弄一个zkClient来进行监听操作
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,10);
            this.zkClient = CuratorFrameworkFactory.builder()
                    .connectString(zookeeperProperties.getUrl())
                    .sessionTimeoutMs(zookeeperProperties.getSessionTimeOut())
                    //.namespace(ZookeeperProperties.root+"/"+zookeeperProperties.getNamespace())
                    .retryPolicy(retryPolicy).build();

            this.zkClient.start();

            // 监听根节点，如果根节点下增加新应用或者删除应用
            addRootListener(root.toString(),this.zkClient,this.zkUtils);
        }catch (Exception e){
            e.printStackTrace();
            log.error("zookeeper监听失败:" + e.getMessage());
        }
    }

    private void addRootListener(String root,CuratorFramework zkClient,ZKUtils zkUtils) throws Exception{
        PathChildrenCache cache = new PathChildrenCache(zkClient,root,true);
        // 在初始化时就开始进行监听
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch(event.getType()){
                    case CHILD_ADDED:
                        String serverPath = event.getData().getPath();
                        log.info("/root->新增子节点："+serverPath);
                        // 获取此应用下面的服务列表
                        List<String> childPath = zkUtils.getChildsByPath(serverPath);
                        for (String s : childPath) {
                            // 查询数据
                            String ipPort = new String(zkUtils.getData(serverPath + "/" + s));
                            ZookeeperCache.addService(s,ipPort);
                        }
                        //监听Server
                        addServerListener(serverPath,client);
                        break;
                    case CHILD_UPDATED:
                        log.info("/root->子节点：" + event.getData().getPath() + ",数据修改为：" + new String(event.getData().getData()));
                        break;
                    case CHILD_REMOVED:
                        String serverPath2 = event.getData().getPath();
                        log.info("子节点："+serverPath2+"被删除");
                        // 从监听列表中移除
                        ZookeeperCache.removeListener(serverPath2);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void addServerListener(String path,CuratorFramework zkClient) throws Exception{
        PathChildrenCache cache = new PathChildrenCache(zkClient,path,true);
        // 增加到监听列表
        ZookeeperCache.addListener(path,cache);
        // 在初始化时就开始进行监听
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch(event.getType()){
                    case CHILD_ADDED:
                        String[] childPathArr = event.getData().getPath().split("/");
                        String childPath = childPathArr[childPathArr.length-1];
                        byte[] childData = event.getData().getData();
                        String ipPort = new String(childData);
                        log.info("新增子节点：" + event.getData().getPath() + ",数据为：" + ipPort);
                        ZookeeperCache.addService(childPath, ipPort);
                        break;
                    case CHILD_UPDATED:
                        log.info("子节点：" + event.getData().getPath() + ",数据修改为：" + new String(event.getData().getData()));
                        break;
                    case CHILD_REMOVED:
                        String[] childPathArr2 = event.getData().getPath().split("/");
                        String childPath2 = childPathArr2[childPathArr2.length-1];
                        byte[] childData2 = event.getData().getData();
                        String ipPort1 = new String(childData2);
                        log.info("子节点："+event.getData().getPath()+"被删除");
                        ZookeeperCache.delService(childPath2, ipPort1);
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
