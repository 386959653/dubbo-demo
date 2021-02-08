package com.dubbo.service;

import java.util.Map;

public interface HelloService {

    Map<String,Object> sayHello(String name);
    Map<String,Object> sayHello2();

}
