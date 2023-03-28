package com.parody.rpc.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Properties;

/**
 * 读取配置文件
 */
public class RpcClientConfig {

    private static Properties properties;

    //读取application.properties文件
    static {
        try (InputStream in = RpcClientConfig.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 读取配置文件中的设置到 RpcClientProperties 对象中
     */
    public static void fillProperties(RpcClientProperties clientProperties) throws IllegalAccessException {
        // 获取属性数组
        Field[] fields = clientProperties.getClass().getDeclaredFields();
        // 遍历属性数组并设置值
        for (Field field : fields) {
            String name = "client." + field.getName();
            String value = properties.getProperty(name);
            if (Objects.nonNull(value) && !name.equals("client.version")) {
                field.setAccessible(true);
                if (name.equals("client.timeout")) {
                    field.set(clientProperties, Integer.valueOf(value));
                } else {
                    field.set(clientProperties, value);
                }
            }
        }
    }
}
