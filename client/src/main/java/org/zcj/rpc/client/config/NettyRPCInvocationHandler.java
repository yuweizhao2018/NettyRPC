package org.zcj.rpc.client.config;

import com.alibaba.fastjson.util.TypeUtils;
import org.springframework.stereotype.Component;
import org.zcj.rpc.annotation.annotation.RpcProxy;
import org.zcj.rpc.client.core.RpcRequestPool;
import org.zcj.rpc.client.netty.NettyClient;
import org.zcj.rpc.common.model.Head;
import org.zcj.rpc.common.model.Protocol;
import org.zcj.rpc.common.model.RpcRequest;
import org.zcj.rpc.common.model.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;


@Component
public class NettyRPCInvocationHandler implements InvocationHandler {

    public NettyRPCInvocationHandler() {
    }

    private Class<?> type;

    public NettyRPCInvocationHandler(Class<?> type){
        this.type = type;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Protocol protocol = new Protocol();
        Head head = new Head();
        RpcRequest rpcRequest = new RpcRequest();
        String version = type.getAnnotation(RpcProxy.class).version();
        byte serializer = type.getAnnotation(RpcProxy.class).serializer();
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setVersion(version);
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameters(args);
        String requestId = UUID.randomUUID().toString().replaceAll("-", "");
        head.setRequestId(requestId);
        head.setSerializer(serializer);
        protocol.setHead(head);
        protocol.setObject(rpcRequest);
        new NettyClient().send(protocol);

        Protocol resProtocol = RpcRequestPool.getInstance().getResponse(requestId);
        if (resProtocol == null){
            return null;
        }
        RpcResponse response = (RpcResponse) resProtocol.getObject();
        Object result = response.getResult();
        if (result == null){
            throw response.getException();
        }
        // json会将对象内部的Object对象反序列化为Map形式，这里需要手动cast result类型
        if (result instanceof Map){
            result = TypeUtils.cast(result, method.getReturnType(), null);
        }
        return result;
    }
}
