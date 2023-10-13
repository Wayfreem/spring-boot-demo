package com.demo.kaptcha.controller;

import com.demo.kaptcha.config.KaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author wuq
 * @Time 2023-10-13 15:29
 * @Description
 */
@RestController
public class CaptchaController {

    @Autowired
    private KaptchaUtil kaptchaUtil;


    @GetMapping("getCaptchaImage")
    public Map getCode(HttpServletRequest request) {
        return kaptchaUtil.getCaptcha();
    }
}
