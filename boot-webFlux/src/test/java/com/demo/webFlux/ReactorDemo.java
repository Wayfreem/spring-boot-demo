package com.demo.webFlux;

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
