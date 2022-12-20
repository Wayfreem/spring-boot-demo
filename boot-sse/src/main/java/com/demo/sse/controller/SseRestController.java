package com.demo.sse.controller;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author wuq
 * @Time 2022-12-19 16:21
 * @Description
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("sse")
public class SseRestController {

    /**
     * 用于创建连接
     */
    @GetMapping("/connect/{userId}")
    public SseEmitter connect(@PathVariable String userId) {
        return SseEmitterUtil.connect(userId);
    }

    /**
     * 推送给所有人
     *
     * @param message
     * @return
     */
    @GetMapping("/push/{message}")
    public String push(@PathVariable(name = "message") String message) {
        //获取连接人数
        int userCount = SseEmitterUtil.getUserCount();
        //如果无在线人数，返回
        if(userCount<1){
            return "无人在线！";
        }
        SseEmitterUtil.batchSendMessage(message);
        return "发送成功！";
    }

    /**
     * 发送给单个人
     *
     * @param message
     * @param userid
     * @return
     */
    @GetMapping("/push_one/{messsage}/{userid}")
    public String pushOne(@PathVariable(name = "message") String message, @PathVariable(name = "userid") String userid) {
        SseEmitterUtil.sendMessage(userid, message);
        return "推送消息给" + userid;
    }

    /**
     * 关闭连接
     */
    @GetMapping("/close/{userid}")
    public String close(@PathVariable("userid") String userid) {
        SseEmitterUtil.removeUser(userid);
        return "连接关闭";
    }
}
