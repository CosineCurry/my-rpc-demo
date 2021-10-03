package com.cosine.test;

import com.cosine.rpc.api.HelloObject;
import com.cosine.rpc.api.HelloService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: HelloService的实现类，客户端就是要调这个类的hello方法
 * @Author: cosine
 * @Date: 2021/9/30 8:49 下午
 * @Version: 1.0.0
 */
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);


    @Override
    public String hello(HelloObject object) {
        logger.info("接收到：{}",object.getMessage());
        return "HelloServiceImpl类的返回值，id=" + object.getId();
    }
}
