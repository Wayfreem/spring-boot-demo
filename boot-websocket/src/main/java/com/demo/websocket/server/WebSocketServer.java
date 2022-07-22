package com.demo.websocket.server;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    // concurrent包的线程安全Set，用来存放每个客户端对应的 MyWebSocket 对象。
    // 若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private String userId;

    /**
     * 连接建立的时候回调方法
     *
     * @param session websocket 连接回话
     * @param userId  用户编码
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        log.debug("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
            sendMessage(JSON.toJSONString(Map.of("topic", "ws.onOpen", "message", "连接成功")));
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    /**
     * 连接关闭时回调方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.debug("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 当前端向服务端发送消息的时候执行
     * @param message 消息体，约定为 json 格式
     */
    @OnMessage
    public void onMessage(String message) {
        log.debug("来自客户端的消息:" + message);

    }

    /**
     * 当连接报错时执行
     * @param session 当前的连接回话
     * @param error   报错信息
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 发送消息
     *
     * @param message json 格式的消息
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        // 向前端发送消息
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发消息
     * @param message 发送的消息体
     */
    public static void massMessage(String message) {
        log.info(message);
        for (WebSocketServer wss : webSocketSet) {
            try {
                wss.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static CopyOnWriteArraySet<WebSocketServer> getWebSocketSet() {
        return webSocketSet;
    }

    public String getUserId() {
        return userId;
    }

}
