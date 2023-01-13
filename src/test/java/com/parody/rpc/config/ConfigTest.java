package com.parody.rpc.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ConfigTest {

    @Test
    public void configTest() {
        RpcClientProperties properties = RpcClientProperties.getProperties();
        Field[] fields = properties.getClass().getDeclaredFields();
        System.out.println(  Arrays.toString(fields));
        System.out.println(properties);
    }
}
