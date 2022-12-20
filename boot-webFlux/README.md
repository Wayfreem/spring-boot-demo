## 说明

这里是集成 webFlux 基础的使用。

### 依赖包

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 对应的 Controller 部分

这里会涉及到 `text/event-stream` 可以看下 sse 项目

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author wuq
 * @Time 2022-12-19 14:03
 * @Description
 */
@RestController
@Slf4j
public class TestController {

    @GetMapping("/1")
    private String get1() {
        log.info("get1 start");
        String result = createStr();
        log.info("get1 end");
        return result;
    }

    private String createStr() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "some thing";
    }

    @GetMapping("/2")
    private Mono<String> get2() {
        log.info("get2 start");
        // 如果直接使用just方法那么它的线程耗时和get1结果一样，等到方法执行结束后才结束
        // Mono<String> result = Mono.just(createStr());
        // 注意需要使用流编程模式，惰性求值，实现异步
        Mono<String> result = Mono.fromSupplier(this::createStr);
        log.info("get2 end");
        return result;
    }

    /**
     * Flux 返回 1-N 个元素
     * produces = "text/event-stream" 设置后像流一样返回数据，不设置就会一次全部返回
     * @return
     */
//    @GetMapping(value = "/3", produces = "text/event-stream")
    @GetMapping(value = "3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> get3(){
        log.info("get3 start");

        Flux<String> result = Flux.fromStream(IntStream.range(1, 5).mapToObj(
                i -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "flux data --" + i;
                }
        ));

        log.info("get3 end");
        return result;
    }

}
```

### 测试类

通过测试类来初步理解

```java
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

public class ReactorDemo {

    @Test
    public void test(){
        // reactor = jdk8 stream + jdk9 reactor stream
        String[] strs = {"1", "2", "3"};

        // 这里需要使用到的包在 org.reactivestreams 下面
        Subscriber<Integer> subscriber = new Subscriber<>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("接收到数据：" + item);

                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                subscription.cancel();
            }

            @Override
            public void onComplete() {
                subscription.cancel();
            }
        };

        // 这里是 JDK8 中的 stream
        Flux.fromArray(strs).map(Integer::valueOf)
                // 这里是 JDK9 中的 stream
                .subscribe(subscriber);
    }
}
```