package org.demo.sms.aliyun.utils;

import com.alibaba.fastjson2.JSONObject;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SmsUtils {

    // 阿里云上面申请的 ACCESS_KEY_ID
    public static final String ACCESS_KEY_ID = "阿里云上面申请的 ACCESS_KEY_ID";

    // 阿里云上面申请的 ACCESS_KEY_SECRET
    public static final String ACCESS_KEY_SECRET = "阿里云上面申请的 ACCESS_KEY_SECRET";

    // 短信签名
    public static final String SIGN_NAME = "短信签名";

    /**
     * 发送短信使用的方法
     * 根据不同的客户替换对应的 key 以及 secret, 时间：2024-05-23
     *
     * @param code         参数值，用于替换短信模板中的参数
     * @param phone        手机号码
     * @param templateCode 模板编码
     */
    public static void sms(JSONObject code, String phone, String templateCode) {

        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(ACCESS_KEY_ID)
                .accessKeySecret(ACCESS_KEY_SECRET)
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou") // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(phone)
                .signName(SIGN_NAME)
                .templateCode(templateCode)
                .templateParam(code.toJSONString())
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        SendSmsResponse resp = null;
        try {
            resp = response.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println(new Gson().toJson(resp));

        client.close();
    }
}
