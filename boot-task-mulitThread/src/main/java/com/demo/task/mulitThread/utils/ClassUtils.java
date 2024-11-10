package com.demo.task.mulitThread.utils;

import com.alibaba.fastjson2.JSONObject;

import java.lang.reflect.Field;
import java.util.Set;

public class ClassUtils {

    public static <T> String generate(T pojo){
        StringBuilder stringBuilder = new StringBuilder();
        for(Class<?> clazz = pojo.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                try {
                    field.setAccessible(true);
                    stringBuilder.append(field.get(pojo));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return MD5Utils.MD5(stringBuilder.toString());
    }

    public static <T> void setValue(T pojo, JSONObject jsonObject){
        Set<String> strings = jsonObject.keySet();
        for(Class<?> clazz = pojo.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                if (strings.contains(field.getName())) {
                    try {
                        field.setAccessible(true);
                        field.set(pojo, jsonObject.get(field.getName()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}
