package com.demo.http.rereadHttpRequest.controller;

import com.demo.http.rereadHttpRequest.utils.RequestReadUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author wuq
 * @Time 2023-1-13 11:07
 * @Description
 */
@RestController
public class TestController {

    @RequestMapping("test")
    public String test(HttpServletRequest request) throws IOException {
        String body = IOUtils.toString(request.getInputStream(), Charset.defaultCharset());
        System.out.println(body);
        return "请求成功";
    }

    @RequestMapping("testRepeat")
    public String testRepeat(HttpServletRequest request) throws IOException {
        // 先读取一次 request 中的内容
        String body = RequestReadUtils.read(request);
        System.out.println(body);

        // 使用 IOUtils 包中的方法读取
        String data = IOUtils.toString(request.getInputStream(), Charset.defaultCharset());
        System.out.println(data);
        return "请求成功";
    }
}
