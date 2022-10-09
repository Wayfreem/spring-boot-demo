package com.demo.retry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

/**
 * 使用 Retry
 *
 * @author wuq
 * @Time 2022-10-9 15:42
 * @Description
 */
@Service
public class RetryTemplateService {

    @Autowired
    private RetryTemplate retryTemplate;

    public int retry(final int code) {
       return retryTemplate.execute(
                retry -> {
                   return test(code);
                },
                recovery -> {
                   return recover(code);
                }
        );
    }


    private int test(int code){
        System.out.println("调用 retry() ，时间：" + LocalTime.now());
        if (code == 0) {
            throw new RuntimeException("调用失败！");
        }
        System.out.println("正常调用成功");
        return 200;
    }


    private int recover(int code){
        System.out.println("最后调用还是失败了，参数："+ code +"，时间：" + LocalTime.now());
        return 500;
    }
}
