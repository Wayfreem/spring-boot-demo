package com.demo.sse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author wuq
 * @Time 2022-12-19 15:02
 * @Description
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
public class TestSSEController {

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
        return "sse";
    }

    @RequestMapping(value = "push")
    public void push(HttpServletResponse response) throws IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("utf-8");

        for (int i = 0; i < 5; i++) {
            // 指定事件标识  event: 这个为固定格式
            response.getWriter().write("event:me\n");
            // 格式：data: + 数据 + 2个回车
            response.getWriter().write("data:" + i + "\n\n");
            response.getWriter().flush();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
