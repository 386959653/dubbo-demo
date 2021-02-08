package com.dubbo.provider.service;

import java.io.Serializable;

/**
 * Created by Wewon on 2020-08-06.
 */
public class MyDTO implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
