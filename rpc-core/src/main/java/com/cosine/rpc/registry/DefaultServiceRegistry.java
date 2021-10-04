package com.cosine.rpc.registry;

import com.cosine.rpc.enumeration.RpcError;
import com.cosine.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 默认的服务注册表
 * @Author: cosine
 * @Date: 2021/10/3 10:35 下午
 * @Version: 1.0.0
 */
public class DefaultServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    /** 服务注册表，key为接口的完整名，value为具体服务对象。一个接口只能有一个对象提供服务 */
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    /** 线程安全的ConcurrentHashSet */
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void register(T service) {
        // 服务名为完整的对象名，如com.cosine.test.HelloServiceImpl
        String serviceName = service.getClass().getCanonicalName();
        System.out.println("服务名为："+serviceName);
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        // service实现了哪些接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        // 一个接口只能有一个对象提供服务
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("为接口: {} 注册服务: {}", interfaces, serviceName);
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
