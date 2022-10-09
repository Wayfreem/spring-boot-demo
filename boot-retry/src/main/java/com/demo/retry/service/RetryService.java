package com.demo.retry.service;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class RetryService {

    /**
     * value：抛出指定异常才会重试
     * include：和 value 一样，默认为空，当 exclude 也为空时，默认所有异常
     * exclude：指定不处理的异常
     * maxAttempts：最大重试次数，默认3次
     * backoff：重试等待策略，
     * 默认使用 @Backoff，@Backoff 的 value 默认为 1000L，我们设置为 2000； 以毫秒为单位的延迟（默认 1000）
     * multiplier（指定延迟倍数）默认为 0，表示固定暂停 1秒后进行重试，如果把 multiplier 设置为 1.5，则第一次重试为 2秒，第二次为 3秒，第三次为4.5秒。
     *
     * @param code 调用参数
     * @return code
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public int retry(int code) {
        System.out.println("调用 retry() ，时间：" + LocalTime.now());
        if (code == 0) {
            throw new RuntimeException("调用失败！");
        }
        System.out.println("正常调用成功");

        return 200;
    }

    /**
     * Spring-Retry 还提供了 @Recover 注解，用于 @Retryable 重试失败后处理方法。
     * 如果不需要回调方法，可以直接不写回调方法，那么实现的效果是，重试次数完了后，如果还是没成功没符合业务判断，就抛出异常。
     * 可以看到传参里面写的是 RuntimeException e，这个是作为回调的接头暗号（重试次数用完了，还是失败，我们抛出这个 RuntimeException e通知触发这个回调方法）。
     * 注意事项：
     * 方法的返回值必须与 @Retryable 方法一致
     * 方法的第一个参数，必须是 Throwable 类型的，建议是与 @Retryable 配置的异常一致，其他的参数，需要哪个参数，写进去就可以了（ @Recover方 法中有的）
     * 该回调方法与重试方法写在同一个实现类里面
     * <p>
     * 由于是基于AOP实现，所以不支持类里自调用方法
     * 如果重试失败需要给 @Recover 注解的方法做后续处理，那这个重试的方法不能有返回值，只能是 void
     * 方法内不能使用try catch，只能往外抛异常
     * </p>
     *
     * @param e    Exception
     * @param code 调用参数
     * @return int
     * @Recover 注解来开启重试失败后调用的方法(注意, 需跟重处理方法在同一个类中)，此注解注释的方法参数一定要是 @Retryable 抛出的异常，否则无法识别，可以在该方法中进行日志处理。
     */
    @Recover
    public int recover(Exception e, int code) {
        System.out.println("回调方法执行！！！！");
        //记日志到数据库 或者调用其余的方法
        System.out.println("异常信息:" + e.getMessage());
        return 400;
    }
}
