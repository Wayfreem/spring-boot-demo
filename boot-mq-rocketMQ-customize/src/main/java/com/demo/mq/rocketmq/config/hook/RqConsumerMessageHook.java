package com.demo.mq.rocketmq.config.hook;

import com.demo.mq.rocketmq.utils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.MDC;

import java.util.List;


@Slf4j
public class RqConsumerMessageHook implements ConsumeMessageHook {
    @Override
    public String hookName() {
        return "RqConsumerMessageHook";
    }

    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        List<MessageExt> msgList = context.getMsgList();
        if (CollectionUtil.isEmpty(msgList)) {
            return;
        }
        for (MessageExt next : msgList) {
            String userProperty = next.getUserProperty("xid");
            if (StringUtils.isNotBlank(userProperty)) {
                MDC.put("xid", userProperty);
            }
        }
    }

    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
        List<MessageExt> msgList = context.getMsgList();
        if (CollectionUtil.isEmpty(msgList)) {
            return;
        }

        for (MessageExt next : msgList) {
            String userProperty = next.getUserProperty("xid");
            if (StringUtils.isNotBlank(userProperty)) {
                MDC.remove("xid");
            }
        }
    }
}
