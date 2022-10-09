package com.demo.retry.controller;

import com.demo.retry.service.RetryService;
import com.demo.retry.service.RetryTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RetryController {

    @Autowired
    private RetryService retryService;

    @Autowired
    private RetryTemplateService retryTemplateService;

    /**
     * 使用注解的方式调用
     *
     * @param code 参数
     * @return code
     */
    @RequestMapping("test")
    public int test(int code) {
        return retryService.retry(code);
    }

    @RequestMapping("testTemplate")
    public int testTemplate(int code) {
        return retryTemplateService.retry(code);
    }
}
