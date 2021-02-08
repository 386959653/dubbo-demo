package com.dubbo.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dubbo.common.domain.DubboRequest;
import com.dubbo.service.HelloService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @Reference(version = "1.0")
    private HelloService helloService;

    @RequestMapping(value = "/hello")
    public String hello() {
        Map<String,Object> map = new HashMap<>();
        map = helloService.sayHello("world");
        System.out.println(map);
        return map.toString();
    }

    /**
     * 接口调用统一入口（泛化调用）
     * @param dubboRequest
     * @return
     */
    @RequestMapping("/api")
    public Object api(@RequestBody DubboRequest dubboRequest) {
        return DubboProxy.invoke(dubboRequest);
    }

}
