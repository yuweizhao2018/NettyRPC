package org.zcj.rpc.common.utils;


import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ZookeeperCache {

    private static Logger logger = LoggerFactory.getLogger(ZookeeperCache.class);

    /** 存储提供服务列表 key->服务名 value->提供服务的ip:port,用set保存，能帮我们去重，不过IPPojo记得重写equals和hasCode */
    private static final Map<String, Set<String>> serviceList = new ConcurrentHashMap<>(10);

    /**  服务监听列表，当新增服务则添加到这，如果服务关闭，则cloase掉再移除掉 */
    private static final Map<String, PathChildrenCache> listenerList = new ConcurrentHashMap<>(10);

    /**
     * 获取服务列表
     * @param
     * @return
     */
    public static Map<String, Set<String>> getServiceList(){
        return serviceList;
    }

    /**
     * 移除IP
     * @param ip
     */
    public static void removeIPPojo(String ip){
        String[] ipArr = ip.split(":");
        String ipPort = ipArr[0] + "-" + Integer.parseInt(ipArr[1]);
        serviceList.forEach((key,value)->{
            value.remove(ipPort);
        });
    }

    /**
     * 增加服务
     * @param servicePath
     * @param ipPort
     */
    public static  void addService(String servicePath,String ipPort){
        // 判断缓存的是否有serverPath服务
        if (serviceList.containsKey(servicePath)){
            Set<String> ips = serviceList.get(servicePath);
            ips.add(ipPort);
        }else{
            Set<String> ips = new HashSet<>(10);
            ips.add(ipPort);
            serviceList.put(servicePath,ips);
        }
        logger.info("服务列表：数量->"+serviceList.size()+" 列表->"+serviceList.toString());
    }

    /**
     * 移除服务
     * @param servicePath
     * @param ipPort
     */
    public static  void delService(String servicePath,String ipPort){
        if (serviceList.containsKey(servicePath)){
            Set<String> ips = serviceList.get(servicePath);
            if (ips.contains(ipPort)){
                ips.remove(ipPort);
            }
        }
        logger.info("服务列表：数量->"+serviceList.size()+" 列表->"+serviceList.toString());
    }

    /**
     * 增加监听
     * @param serverPath
     * @param cache
     */
    public static void addListener(String serverPath,PathChildrenCache cache){
        if (!listenerList.containsKey(serverPath)){
            listenerList.put(serverPath,cache);
        }
        logger.info("监听列表：数量->"+listenerList.size()+" 列表->"+listenerList.toString());
    }

    /**
     * 移除监听
     * @param serverPath
     */
    public static void removeListener(String serverPath){
        if (listenerList.containsKey(serverPath)){
            PathChildrenCache cache = listenerList.get(serverPath);
            try {
                cache.close();
                listenerList.remove(serverPath);
                logger.info("监听列表：数量->"+listenerList.size()+" 列表->"+listenerList.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
