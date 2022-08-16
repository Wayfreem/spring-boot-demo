## 简介

使用 spring boot 与 redis 集成的例子，具体集成相关操作如下：
- 实现集成的redis 操作
- 集成有 redis 分布式锁的操作
- 使用 redis 作为消息消息队列操作

**相关文档**
- [Redis 发布订阅操作](https://blog.csdn.net/qq_18948359/article/details/119797418?spm=1001.2014.3001.5501)
- [SpringBoot 2.x 整合Redis](https://blog.csdn.net/qq_18948359/article/details/119780556?spm=1001.2014.3001.5501)


## 具体实现

### 第一步：引入依赖

pom 文件中核心依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 第二步：修改配置文件
```properties
# 服务端口
server.port=8080
 
# redis 配置
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=
```

### 第三步：自定义 redisTemplate

由于发现 enableDefaultTyping 方法过期，这里需要替换写法
```java

om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

替换为

om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
```

```java
@Configuration
public class RedisConfig {

    /**
     * 自己定义一个 redisTemplate，并设置相关参数
     * @param redisConnectionFactory RedisConnectionFactory
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        // 为了我们自己开发方便，一般直接用 <String, Object>
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Json 序列化配置
        // 使用 Jackson2JsonRedisSerializer 替换默认的 JdkSerializationRedisSerializer 来序列化和反序列化 redis的value值
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Objects.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 这个方法过期了        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

        jackson2JsonRedisSerializer.setObjectMapper(om);

        // String 的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);       // hash的key也采用String的序列化方式

        // template.setValueSerializer(jackson2JsonRedisSerializer);   // value序列化方式采用jackson
        template.setValueSerializer(stringRedisSerializer);         // value 采用 String 的方式
        template.setHashValueSerializer(jackson2JsonRedisSerializer);    // hash的value序列化方式采用jackson
        template.afterPropertiesSet();

        return template;
    }
}
```

### 第四步：RedisOperator

创建一个用于操作 redis 的类

```java
@Service
public class RedisOperator {

    @Autowired
    private RedisTemplate redisTemplate;

    public String getKey(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void setKey(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

}
```

### 第五步：创建 controller 访问测试
```java
@RestController
public class RedisController {

    @Autowired
    RedisOperator redisOperator;

    @RequestMapping("setKey")
    public void setKey(){
        redisOperator.setKey("tempKey", "value");
    }

    @RequestMapping("getKey")
    public String getKey(){
        return redisOperator.getKey("tempKey");
    }
}
```

## 使用 redis 作为分布式锁操作

对于 redis 分布式锁操作对应于 redis 的命令为：
- setex: set with expire  设置一个值的过期时间
- setnx: set if not exist 不存在的时候设置值

```java
@Component
public class DistributedLock {

    // 由于是直接操作 String, 这里就直接使用 StringRedisTemplate
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 如果成功加上锁就返回为 true
     * @param lockId 锁的编码
     * @return Boolean 加锁是否成功
     */
    public boolean lock(String lockId, long millisecond) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockId, "lock", millisecond, TimeUnit.MILLISECONDS);
        return success != null && success;
    }

    public boolean lock(String lockId) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockId, "lock");
        return success != null && success;
    }

    /**
     * 释放锁
     */
    public void unlock(String lockId) {
        redisTemplate.delete(lockId);
    }
}
```

## 使用 redis 队列做发布订阅
[博客地址](https://blog.csdn.net/qq_18948359/article/details/119806041?spm=1001.2014.3001.5501)

为了方便于程序开发，使用**注解的方式**实现发布订阅，大致的逻辑是：

用注解标注出需要加入到 redis 容器中的方法，然后通过 redis 进行发布消息，加入了注解的方法可以接受到消息

### 第一步：添加自定义注解

```java
import java.lang.annotation.*;
 
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisMessageListener {
 
    String topic(); // 事件主题
}
```

### 第二步：增加 redis `RedisMessageListenerContainer` 容器 bean
```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
```

### 第三步：增加注解方法注册中心

这里的注册服务是将程序中所有的对象在启动的时候全部扫描一边，如果是有注解在，就添加到容器中。

```java
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
```

### 第四步：增加监听类，以及发送消息的方法

消息的监听类，用于接受处理 redis 发送过来的消息
```java
@Service
public class OrderListener {

    @RedisMessageListener(topic = "order::getState")
    public void getState(String msg){
        System.out.println("OrderListener --->  getState ---->" + msg);
    }

    @RedisMessageListener(topic = "order::getState")
    public void onMessage(String msg){
        System.out.println("OrderListener --->  onMessage ---->" + msg);
    }
}
```

消息发送类，通过这个方法发送消息到 redis 中
```java
public void sendMsg(String msg){
    redisTemplate.convertAndSend("order::getState", msg);
}
```

### 第五步：测试
```java
@RequestMapping("sendMsg")
public void sendMsg(String msg){
    redisOperator.sendMsg(msg);
}
```