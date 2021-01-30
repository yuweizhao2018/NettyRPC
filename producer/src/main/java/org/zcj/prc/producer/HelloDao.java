package org.zcj.prc.producer;

import org.springframework.stereotype.Repository;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/19 10 19
 * Description:
 */
@Repository
public class HelloDao {

    public String hello(String hello) {
        return "hello welcome !!!";
    }
}
