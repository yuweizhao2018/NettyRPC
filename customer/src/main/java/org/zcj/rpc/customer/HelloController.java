package org.zcj.rpc.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zcj.rpc.api.HelloService;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 17 33
 * Description:
 */
@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    @RequestMapping("/hello")
    public String hello() {
        return helloService.hello("hello");
    }

}
