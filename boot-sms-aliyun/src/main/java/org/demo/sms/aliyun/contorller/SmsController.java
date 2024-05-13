package org.demo.sms.aliyun.contorller;

import com.alibaba.fastjson2.JSONObject;
import org.demo.sms.aliyun.utils.SmsUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {
    @RequestMapping("sms")
    public void send(){

        // 替换码
        JSONObject json = new JSONObject(2);
        json.put("money1", 2000);
        json.put("money2", 800);

        SmsUtils.sms(json, "手机号码", "短信模板");
    }
}
