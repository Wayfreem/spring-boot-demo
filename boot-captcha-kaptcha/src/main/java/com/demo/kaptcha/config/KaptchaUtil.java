package com.demo.kaptcha.config;

import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于获取对应的验证码
 *
 * @author wuq
 * @Time 2023-10-13 16:16
 * @Description
 */
@Component
public class KaptchaUtil {

    @Value("${captcha.type}")
    private String captchaType;

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    /**
     * 获取验证码
     * @return Map {"code": "进行验证的值", "img": "base64 格式的图片"}
     */
    public Map getCaptcha() {

        // code 是我们需要验证的值，可以放到 缓存 中方便于验证
        String capStr = null, code = null;
        BufferedImage image = null;

        if ("math".equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            throw new RuntimeException("获取验证码报错：" + e.getMessage());
        }

        Map resultMap = new HashMap();
        resultMap.put("code", code);
        resultMap.put("img", Base64.encode(os.toByteArray()));
        return resultMap;
    }
}
