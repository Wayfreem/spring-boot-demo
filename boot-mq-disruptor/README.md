## 参考链接
[Disruptor详细介绍](https://blog.csdn.net/qq_44073614/article/details/127428053)

[Disruptor - 介绍（1）](https://www.jianshu.com/p/78160f213862)

[Spring Boot 使用 Disruptor 做内部高性能消息队列](https://mp.weixin.qq.com/s/sHSFhE2R85h1wEFy6TRg_w)

[并发框架Disruptor(核心概念 入门 高性能原理-伪共享 CAS 环形数据 生产和消费模式 高级使用 )](https://blog.csdn.net/m0_46690280/article/details/120095823)

最新参考
[Spring Boot + Disruptor 实战：深度讲解构建百万级并发无锁队列](https://mp.weixin.qq.com/s/nCPHyHWHzf1bPDH0UazXkg)

## 项目说明


二、Disruptor核心机制解析
2.1、环形队列的时空折叠
// RingBuffer内存结构
public final class RingBuffer<E> {
private final Object[] entries;          // 预分配对象数组
private final int indexMask;             // 位运算替代取模
private final Sequence sequencer;        // 序列号管理器
}
设计精髓：

预分配内存：启动时初始化所有Event对象

位运算优化：indexMask = bufferSize - 1（bufferSize为2^n）

无锁并发：通过Sequence实现原子操作

2.2、消除伪共享的终极方案
// Sequence内存布局优化
public class Sequence extends RhsPadding {
static final class LhsPadding {
long p1, p2, p3, p4, p5, p6, p7; // 左填充
}
private volatile long value;
static final class RhsPadding {
long p9, p10, p11, p12, p13, p14, p15; // 右填充
}
}
缓存行填充：通过左右各56字节填充，确保每个Sequence独占缓存行
三、Spring Boot 集成 Disruptor 的步骤
3.1、添加依赖
首先，在 Spring Boot 项目的 pom.xml 中添加 Disruptor 的依赖：

<dependency>
    <groupId>com.lmax</groupId>
    <artifactId>disruptor</artifactId>
    <version>3.4.4</version>
</dependency>
3.2、定义事件类
创建一个简单的事件类，用于在队列中传递数据：

// 定义事件类
public class MyEvent {
private String message;
public String getMessage() {
return message;
}
public void setMessage(String message) {
this.message = message;
}
}
3.3、定义事件工厂
事件工厂用于创建事件对象：

import com.lmax.disruptor.EventFactory;
// 定义事件工厂
public class MyEventFactory implements EventFactory<MyEvent> {
@Override
public MyEvent newInstance() {
return new MyEvent();
}
}
3.4、定义事件处理器
事件处理器负责处理从队列中取出的事件：

import com.lmax.disruptor.EventHandler;
// 定义事件处理器
public class MyEventHandler implements EventHandler<MyEvent> {
@Override
public void onEvent(MyEvent event, long sequence, boolean endOfBatch) throws Exception {
System.out.println("Received event: " + event.getMessage());
}
}
3.5、配置 Disruptor
在 Spring Boot 中，可以通过配置类来创建和配置 Disruptor：

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Configuration
public class DisruptorConfig {
private static final int BUFFER_SIZE = 1024;
@Bean
public Disruptor<MyEvent> disruptor() {
// 创建线程池
ExecutorService executor = Executors.newSingleThreadExecutor();
// 创建事件工厂
MyEventFactory factory = new MyEventFactory();
// 创建 Disruptor
Disruptor<MyEvent> disruptor = new Disruptor<>(factory, BUFFER_SIZE, executor, ProducerType.SINGLE, new BlockingWaitStrategy());
// 设置事件处理器
disruptor.handleEventsWith(new MyEventHandler());
// 启动 Disruptor
disruptor.start();
return disruptor;
}
}
3.6、创建生产者服务
创建一个生产者服务，用于向 Disruptor 中发布事件：

import com.lmax.disruptor.RingBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class MyEventProducer {
private final RingBuffer<MyEvent> ringBuffer;
@Autowired
public MyEventProducer(Disruptor<MyEvent> disruptor) {
this.ringBuffer = disruptor.getRingBuffer();
}
public void publishEvent(String message) {
long sequence = ringBuffer.next();
try {
MyEvent event = ringBuffer.get(sequence);
event.setMessage(message);
} finally {
ringBuffer.publish(sequence);
}
}
}
3.7、创建控制器进行测试
创建一个简单的控制器，用于测试生产者服务：

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TestController {
@Autowired
private MyEventProducer producer;
@GetMapping("/publish/{message}")
public String publishMessage(@PathVariable String message) {
producer.publishEvent(message);
return "Message published: " + message;
}
}
四、Disruptor 高性能原理剖析
4.1、无锁设计
Disruptor 采用了无锁算法，避免了传统队列中锁竞争带来的性能开销。通过使用 CAS（Compare-And-Swap）操作和序列（Sequence）来保证并发操作的原子性和顺序性。


4.2、环形缓冲区
Ring Buffer 是一个固定大小的环形数组，生产者和消费者通过指针在数组中循环操作。这种设计避免了传统队列的内存分配和垃圾回收问题，提高了内存使用效率。


4.3、缓存行填充
Disruptor 通过缓存行填充技术，避免了伪共享问题。伪共享是指多个线程同时访问同一缓存行中的不同变量，导致缓存行频繁失效，影响性能。通过在关键变量前后填充足够的字节，确保每个变量独占一个缓存行，提高了缓存命中率。


五、性能测试与对比
为了验证 Disruptor 在高并发场景下的性能优势，可以使用 JMH（Java Microbenchmark Harness）进行性能测试，并与传统的 BlockingQueue 进行对比。以下是一个简单的性能测试示例：

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class DisruptorBenchmark {
private static final int BUFFER_SIZE = 1024;
private static final int ITERATIONS = 1000000;
private Disruptor<MyEvent> disruptor;
private BlockingQueue<MyEvent> blockingQueue;
@Setup
public void setup() {
// 初始化 Disruptor
ExecutorService executor = Executors.newSingleThreadExecutor();
MyEventFactory factory = new MyEventFactory();
disruptor = new Disruptor<>(factory, BUFFER_SIZE, executor, ProducerType.SINGLE, new BlockingWaitStrategy());
disruptor.handleEventsWith(new MyEventHandler());
disruptor.start();
// 初始化 BlockingQueue
blockingQueue = new LinkedBlockingQueue<>(BUFFER_SIZE);
}
@Benchmark
public void testDisruptor() {
for (int i = 0; i < ITERATIONS; i++) {
long sequence = disruptor.getRingBuffer().next();
try {
MyEvent event = disruptor.getRingBuffer().get(sequence);
event.setMessage("Test message " + i);
} finally {
disruptor.getRingBuffer().publish(sequence);
}
}
}
@Benchmark
public void testBlockingQueue() throws InterruptedException {
for (int i = 0; i < ITERATIONS; i++) {
MyEvent event = new MyEvent();
event.setMessage("Test message " + i);
blockingQueue.put(event);
}
}
public static void main(String[] args) throws RunnerException {
Options opt = new OptionsBuilder()
.include(DisruptorBenchmark.class.getSimpleName())
.forks(1)
.build();
new Runner(opt).run();
}
}
通过运行上述性能测试代码，可以看到 Disruptor 在高并发场景下的吞吐量明显高于传统的 BlockingQueue。

六、性能调优五大策略
6.1、等待策略选择
策略类型
适用场景
吞吐量
延迟
BlockingWaitStrategy
低延迟要求
中
低
SleepingWaitStrategy
平衡型
高
中
YieldingWaitStrategy
高吞吐场景
极高
高
BusySpinWaitStrategy
极端低延迟
极高
极低
// 根据场景动态切换策略
public WaitStrategy getOptimalStrategy() {
if (isLowLatencyEnv()) {
return new BusySpinWaitStrategy();
} else {
return new YieldingWaitStrategy();
}
}
6.2、批量事件提交
// 批量发布优化
public class OrderEventPublisher {

    private final RingBuffer<OrderEvent> ringBuffer;

    public void publishBatch(List<Order> orders) {
        long hi = ringBuffer.next(orders.size());  // 批量申请序号
        long lo = hi - (orders.size() - 1);
        for (long seq = lo; seq <= hi; seq++) {
            OrderEvent event = ringBuffer.get(seq);
            Order order = orders.get((int) (seq - lo));
            event.setOrderId(order.getId());
            // 填充其他字段...
        }
        ringBuffer.publish(lo, hi);  // 批量发布
    }
}
6.3、内存屏障控制
// 内存可见性保障
public class OrderEvent {
private String orderId;
private volatile BigDecimal amount; // volatile保证可见性

    // 使用Unsafe实现更细粒度控制
    private static final Unsafe UNSAFE = getUnsafe();
    private static final long AMOUNT_OFFSET = UNSAFE.objectFieldOffset(
        OrderEvent.class.getDeclaredField("amount"));

    public void setAmount(BigDecimal amount) {
        UNSAFE.putOrderedObject(this, AMOUNT_OFFSET, amount);
    }
}

七、生产环境最佳实践
7.1、监控指标埋点
// 吞吐量监控
@Bean
public MetricsContext disruptorMetrics(RingBuffer<?> ringBuffer) {
return new MetricsContext() {
@Scheduled(fixedRate = 5000)
public void logMetrics() {
long remain = ringBuffer.remainingCapacity();
long publishSeq = ringBuffer.getCursor();
log.info("队列使用率：{}%", (publishSeq - remain) * 100 / ringBuffer.getBufferSize());
}
};
}
7.2、异常处理机制
// 异常处理器
public class OrderExceptionHandler implements ExceptionHandler<OrderEvent> {

    @Override
    public void handleEventException(Throwable ex, long sequence, OrderEvent event) {
        log.error("处理事件[{}]异常: {}", event.getOrderId(), ex.getMessage());
        deadLetterQueue.save(event); // 进入死信队列
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        log.error("Disruptor启动失败", ex);
        System.exit(1);
    }
}
八、适用场景与方案选型
8.1、推荐使用场景
金融交易系统：订单匹配、风控计算

实时日志处理：APM监控数据聚合

游戏服务器：战斗结算、道具交易

物联网网关：海量设备数据采集


8.2、技术选型对比
消息中间件
吞吐量
延迟
适用场景
Disruptor
1000万TPS
微秒级
JVM内部高性能队列
Kafka
100万TPS
毫秒级
分布式持久化消息
RabbitMQ
5万TPS
毫秒级
复杂路由的企业级消息

总结
本文详细介绍了如何在 Spring Boot 项目中集成 Disruptor 实现高性能队列，通过源码示例展示了 Disruptor 的使用方法和配置过程。同时，深入剖析了 Disruptor 的高性能原理，包括无锁设计、环形缓冲区和缓存行填充等技术。通过性能测试对比，验证了 Disruptor 在高并发场景下的性能优势。希望本文能帮助你更好地理解和应用 Spring Boot 和 Disruptor，构建出高性能的消息队列系统。