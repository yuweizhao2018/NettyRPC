package org.zcj.prc.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.zcj.rpc.annotation.annotation.RpcService;
import org.zcj.rpc.api.HelloService;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 16 31
 * Description:
 */
@RpcService(value = HelloService.class, version = "1.0")
public class HelloServiceImpl implements HelloService {

    @Autowired
    private HelloDao helloDao;

    @Override
    public String hello(String hi) {
        return helloDao.hello(hi);
    }
}
