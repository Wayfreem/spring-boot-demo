## 简介

使用 spring 框架上面的 event 来做事件的发布与订阅，这里是采用注解 `@EventListener`的方式实现监听，方便于程序开发。

对于那种需要自己实现 ApplicationListener 接口的实现方式，这里就不做说明

程序具体实现了两种方式:
- 发送事件，不接受返回值
- 发送事件，接受返回值

## 具体实现

**pom 文件**

只需要引入基础的依赖包就好

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**核心的 Event 定义**

这里我是先顶一个抽象的 Event 类，成员变量为 eventData 表示在事件传播过程中，需要传输的对象

```java
// 这里基础的事件定义
public abstract class BaseEvent<T> extends ApplicationEvent {

    // 需要传输的 事件参数
    protected T eventData;

    public BaseEvent(Object source) {
        super(source);
    }

    public BaseEvent(Object source, T eventData) {
        super(source);
        this.eventData = eventData;
    }

    public T getEventData(){
        return this.eventData;
    }

    public void setEventData(T eventData){
        this.eventData = eventData;
    }
}
```

接着，基于 BaseEvent 来封装出来个性化的 自定义事件。

这里面用于接受返回值的具体实现是，将当前的事件发送到对应的 Listener 中，然后再对应的 Listener 里面设置传入事件的 result 值。
```java
public class SimpleEvent<T> extends BaseEvent {

    private String topic;   // 需要传输的事件主题
    private List result;    // 用于接受事件的返回值

    public SimpleEvent(Object source) {
        super(source);
    }

    public SimpleEvent(Object source, T eventData){
        super(source, eventData);
    }

    public SimpleEvent(Object source, T eventData, String  topic){
        super(source, eventData);
        this.topic = topic;
    }

    public String getTopic(){
        return this.topic;
    }

    public List getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }
}
```

**核心的发布事件类**

使用 applicationContext 中的 publishEvent 方法用于发送事件。

这里重点说下接受返回值的逻辑，从下面的代码中 `publishAndReceive()` 可以看到将 `SimpleEvent` 发布出去之后，再通过 `SimpleEvent` 来获取返回值。

```java
@Component
public class SimpleEventPublisher<T> {

    @Autowired
    ApplicationContext applicationContext;

    public void publish(T msg) {
        applicationContext.publishEvent(new SimpleEvent(this, msg));
    }

    public void publish(T msg, String topic) {
        applicationContext.publishEvent(new SimpleEvent(this, msg, topic));
    }

    public List publishAndReceive(T msg, String topic) {
        SimpleEvent simpleEvent = new SimpleEvent(this, msg, topic);
        applicationContext.publishEvent(simpleEvent);
        return simpleEvent.getResult();
    }
}
```

**用于外部访问的接口**

这里添加对应的 controller 用于提供给外部访问测试

```java
@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    @RequestMapping("send")
    public void send() {
        eventService.send();
    }


    @RequestMapping("sendAndReceive")
    public void sendAndReceive() {
        eventService.sendAndReceive();
    }
}
```

**事件发送逻辑**

这里是测试线组装我们需要的数据，然后进行事件发送

```java
@Service
public class EventService {

    @Autowired
    SimpleEventPublisher simpleEventPublisher; 

    public void send() {
        Map map = Map.of("id", "1", "name", "测试 event");
        simpleEventPublisher.publish(map.toString(), "event#doMsg");    // 发送事件
    }

    public void sendAndReceive() {
        Map map = Map.of("id", "1", "name", "测试 event");
        List list = simpleEventPublisher.publishAndReceive(map.toString(), "event#doMsg");  // 发送事件并且接受返回值
        System.out.println(list.get(0));
    }
}
```

**事件的监听**

事件监听，这里使用 `@EventListener` 实现。

这里列出两种情况，第一种是没有返回值的方式，第二种是具有返回值的方式

```java
@EventListener
public void doMsg(SimpleEvent<String> simpleEvent){
    System.out.println("EventService 接收：" + simpleEvent.getEventData());
}

@EventListener
public void doMsgAndBackData(SimpleEvent<String> simpleEvent){
    if (simpleEvent.getTopic() != "event#doMsg") return;
    System.out.println("EventService 接收并返回：" + simpleEvent.getEventData());
    simpleEvent.setResult(Arrays.asList("返回参数"));       // 由于传入的事件与前面发布的事件内存地址指向的是同一个，这里可以设置值用于返回
}
```

对于事件监听有几个点需要说明下：
- `@EventListener` 标注的方法，可以是 private，这个是可以通过，虽然程序没有报错，但是是需要改为 public 的
- 对于事件的监听，可以不在同一个类中，可以分散到不同的类中，这里可以看下我项目中的 `EventListenerService` 就知道了

**事件监听的顺序**

在 `@EventListener` 标注的方法上面增加一个注解 `@Order` 注解就可以实现

```java
@Order(0)
@EventListener
public void doMsg(SimpleEvent<String> simpleEvent){
    System.out.println("EventService 接收："+simpleEvent.getEventData());
}

@Order(1)
@EventListener
public void doMsgAndBackData(SimpleEvent<String> simpleEvent){
    if(simpleEvent.getTopic()!="event#doMsg") return;       // 这里可以用于区分和判断是否是自己需要接受的 topic
    System.out.println("EventService 接收并返回："+simpleEvent.getEventData());
    simpleEvent.setResult(Arrays.asList("返回参数"));       // 由于传入的事件与前面发布的事件内存地址指向的是同一个，这里可以设置值用于返回
}
```