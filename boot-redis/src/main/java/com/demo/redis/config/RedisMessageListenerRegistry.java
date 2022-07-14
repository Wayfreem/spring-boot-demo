package com.demo.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wuq
 * @Time 2022-7-14 13:47
 * @Description
 */
@Component
public class RedisMessageListenerRegistry implements ApplicationRunner {

    // AtomicLong 可以理解为加了 synchronized 的 long 类型
    private AtomicLong counter = new AtomicLong(0);

    @Autowired
    private ApplicationContext context;

    @Override
    public void run(ApplicationArguments args) {
        // 获取Redis的消息监听容器
        RedisMessageListenerContainer container = context.getBean(RedisMessageListenerContainer.class);

        // 扫描注册所有的 @RedisMessageListener 的方法，添加到容器中
        for (String beanName : context.getBeanNamesForType(Object.class)) {
            ReflectionUtils.doWithMethods(Objects.requireNonNull(context.getType(beanName)),
                    method -> {
                        ReflectionUtils.makeAccessible(method);
                        Object target = context.getBean(beanName);
                        RedisMessageListener annotation = AnnotationUtils.findAnnotation(method, RedisMessageListener.class);
                        MessageListenerAdapter adapter = registerBean((GenericApplicationContext) context, target, method);
                        container.addMessageListener(adapter, new PatternTopic(annotation.topic()));
                    },
                    method -> !method.isSynthetic() && method.getParameterTypes().length == 1
                            && AnnotationUtils.findAnnotation(method, RedisMessageListener.class) != null);
        }

    }

    private MessageListenerAdapter registerBean(GenericApplicationContext context, Object target, Method method) {
        String containerBeanName = String.format("%s_%s", MessageListenerAdapter.class.getName(), counter.incrementAndGet());
        context.registerBean(containerBeanName, MessageListenerAdapter.class, () -> new MessageListenerAdapter(target, method.getName()));
        return context.getBean(containerBeanName, MessageListenerAdapter.class);
    }
}
