package org.zcj.rpc.client.netty;

import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 13 02
 * Description:
 */
public class ChannelPool {

    // 缓存所有的channel key为ip-port
    public static volatile Map<String, Channel> channels = new ConcurrentHashMap<>();


    public static volatile Set<String> service = new HashSet<>();

    public static volatile Map<String, Set<String>> serviceIps = new ConcurrentHashMap<>();


    public static Channel getChannel(String key) {
        return channels.get(key);
    }

    public synchronized static void addChannel(String key, Channel channel) {
        channels.put(key, channel);
    }

    public synchronized static void removeChannel(String key) {
        if (channels.containsKey(key)) {
            channels.remove(key);
        }
    }

    public synchronized static void addService(String interfaceName) {
        service.add(interfaceName);
    }


    public synchronized static void addServiceIp(String service, String ipPort) {
        if (serviceIps.containsKey(service)) {
            serviceIps.get(service).add(ipPort);
        } else {
            Set<String> set = new HashSet<>();
            set.add(ipPort);
            serviceIps.put(service, set);
        }

    }
}
