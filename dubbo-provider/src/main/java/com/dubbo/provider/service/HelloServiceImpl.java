package com.dubbo.provider.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.dubbo.service.HelloService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//dubbo注解，暴露服务
@Service(version = "1.0",timeout = 8000)
@Component
public class HelloServiceImpl implements HelloService {

    @Override
    public Map<String, Object> sayHello(String name) {
        Map<String,Object> map = new HashMap<>();
        MyDTO myDTO = new MyDTO();
        myDTO.setName(name);
        map.put("a",myDTO);
        return map;
    }

    @Override
    public Map<String, Object> sayHello2() {
        Map<String,Object> map = new HashMap<>();
        MyDTO myDTO = new MyDTO();
        myDTO.setName("123");
        map.put("a",myDTO);
        return map;
    }
}
