package org.zcj.rpc.api;


import org.zcj.rpc.annotation.annotation.RpcProxy;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 16 29
 * Description:
 */
@RpcProxy(version = "1.0", serializer = (byte)1)
public interface HelloService {

    String hello(String hi);

}
