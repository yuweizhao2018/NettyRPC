package org.zcj.rpc.client.core;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.zcj.rpc.common.model.Protocol;
import org.zcj.rpc.common.model.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RpcRequestPool {

    private static volatile RpcRequestPool rpcRequestPool = null;

    private RpcRequestPool() {

    }

    public static RpcRequestPool getInstance() {
        if (rpcRequestPool == null) {
            synchronized (RpcRequestPool.class) {
                if (rpcRequestPool == null) {
                    rpcRequestPool = new RpcRequestPool();
                }
            }
        }
        return rpcRequestPool;
    }

    private static final Map<String, Promise<Protocol>> requestPool = new ConcurrentHashMap<>();

    public void addRequest(String requestId, EventExecutor executor){
        requestPool.put(requestId, new DefaultPromise<Protocol>(executor));
    }

    public Protocol getResponse(String requestId) throws Exception {
        //获取远程调用结果 10s超时
        Promise<Protocol> promise = requestPool.get(requestId);
        //no service provided
        if (promise == null){
            return null;
        }
        Protocol protocol = promise.get(10, TimeUnit.SECONDS);
        requestPool.remove(requestId);
        return protocol;
    }

    public void notifyRequest(String requestId, Protocol protocol){
        Promise<Protocol> promise = requestPool.get(requestId);
        if (promise != null){
            promise.setSuccess(protocol);
        }
    }
}
