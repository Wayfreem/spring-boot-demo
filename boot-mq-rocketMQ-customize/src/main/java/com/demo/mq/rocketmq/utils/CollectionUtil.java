package com.demo.mq.rocketmq.utils;

import java.util.Collection;

public class CollectionUtil {

    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
