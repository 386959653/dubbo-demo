package com.dubbo.consumer.controller;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.dubbo.spring.boot.DubboProperties;
import com.dubbo.common.domain.DubboRequest;
import com.dubbo.common.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Wewon on 2021-02-08.
 */
@Component
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

                DubboProperties dubboProperties = SpringUtils.getBean(DubboProperties.class);

                // 弱类型接口名
                reference.setInterface(dubboRequest.getInterfaceName());
                reference.setVersion(dubboRequest.getVersion());
                // 声明为泛化接口
                reference.setGeneric(true);
                reference.setTimeout(10000);//超时
                reference.setRetries(0);//重试次数
                reference.setRegistry(dubboProperties.getRegistry());
                reference.setApplication(dubboProperties.getApplication());
                CACHE.put(key, reference);
            }
        }
        return ReferenceConfigCache.getCache().get(reference);
    }
}

