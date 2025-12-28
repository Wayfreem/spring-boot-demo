package com.demo.mq.rocketmq.config.hook;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;
import org.slf4j.MDC;

/**
 * RocketMQ 发送消息钩子
 * 用于在发送消息前，将 MDC 中的 xid 放入消息的用户属性中
 */
@Slf4j
public class RqSendMessageHook implements SendMessageHook {

    @Override
    public String hookName() {
        return "RqSendMessageHook";
    }

    @Override
    public void sendMessageBefore(SendMessageContext context) {
        // 这里暂时先写死，后续再根据需求调整 xid
        String rid = MDC.get("xid");
        if(StringUtils.isNotBlank(rid)) {
            context.getMessage().putUserProperty("xid", rid);
        }
    }

    @Override
    public void sendMessageAfter(SendMessageContext context) {
    }
}
