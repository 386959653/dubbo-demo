package com.dubbo.consumer.controller;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.dubbo.common.domain.DubboRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Wewon on 2021-02-08.
 */
public class DubboProxy {
    // ReferenceConfig实例很重，缓存
    private static ConcurrentMap<String, ReferenceConfig<GenericService>> CACHE = new ConcurrentHashMap<>();

    public static Object invoke(DubboRequest dubboRequest) {
        GenericService service = getService(dubboRequest);
        if (service == null) {
            // TODO 返回错误提示
            return null;
        }
        return service.$invoke(dubboRequest.getMethodName(), dubboRequest.getArgTypes(), dubboRequest.getParameters());
    }

    private static GenericService getService(DubboRequest dubboRequest){
        String key = dubboRequest.getInterfaceName() + dubboRequest.getVersion();
        ReferenceConfig<GenericService> reference = CACHE.get(key);
        if (reference != null) {
            return reference.get();
        }
        synchronized (CACHE) {
            if (CACHE.get(key) == null) { // recheck
                reference = new ReferenceConfig<GenericService>();
                // 普通编码配置方式
                ApplicationConfig application = new ApplicationConfig();
                application.setName("dubbo-consumer");

                // 连接注册中心配置
                RegistryConfig registry = new RegistryConfig();
                registry.setAddress("zookeeper://127.0.0.1:2181");
                // 弱类型接口名
                reference.setInterface(dubboRequest.getInterfaceName());
                reference.setVersion(dubboRequest.getVersion());
                // 声明为泛化接口
                reference.setGeneric(true);
                reference.setTimeout(10000);//超时
                reference.setRetries(0);//重试次数
                reference.setRegistry(registry);
                reference.setApplication(application);
                CACHE.put(key, reference);
            }
        }
        return ReferenceConfigCache.getCache().get(reference);
    }
}

