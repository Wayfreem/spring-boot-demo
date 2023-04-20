## 简介

spring boot集成 websocket 项目。 spring boot 集成 WebSocket 有两种方式，[参考链接看这里](https://blog.csdn.net/qq_42151956/article/details/124745254?spm=1001.2101.3001.6650.8&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-8-124745254-blog-125384766.235%5Ev30%5Epc_relevant_default_base3&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-8-124745254-blog-125384766.235%5Ev30%5Epc_relevant_default_base3&utm_relevant_index=14)

## 集成的步骤

### 第一步：引入依赖

pom.xml

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### 第二步：创建 配置文件类
```java
@Configuration
public class WebSocketConfig {

    /**
     * 注入一个 ServerEndpointExporter,
     * 该 Bean会自动注册使用 @ServerEndpoint 注解声明的 websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
```

### 第三步：创建 websocket 服务
```java
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
```

### 第四步：新建一个 html 测试ws 连接
```html
<!DOCTYPE html>
<html>
<head>
    <title>WebSocket 测试</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width" />
</head>

<body>
<div>
    <h5>websocket 测试</h5>
</div>

<div>
    <input type="button" value="测试是否支持 websocket" onclick="check()"/>
    <div>
        <span id="checkText"></span>
    </div>
</div>

<br>

<div>
    <input type="button" id="btnConnection" value="连接" />
    <input type="button" id="btnClose" value="关闭" />
    <input type="button" id="btnSend" value="发送" />
</div>

<hr>

<div id="messages"></div>
</body>
<script type="text/javascript">
    var socket;

    document.querySelector("#btnConnection").addEventListener("click", function() {
        //实现化WebSocket对象，指定要连接的服务器地址与端口
        socket = new WebSocket("ws://127.0.0.1:8080/websocket/wayfreem");

        //打开事件
        socket.onopen = function() {
            console.log("Socket 已打开");
            socket.send("这是来自客户端的消息" + location.href + new Date());

            document.getElementById('messages').innerHTML = '与服务器端建立连接';
        };

        //获得消息事件
        socket.onmessage = function(msg) {
            console.log(msg.data);
            document.getElementById('messages').innerHTML += '<br />'+ event.data;
        };

        //关闭事件
        socket.onclose = function() {
            console.log("Socket已关闭");
        };

        //发生了错误事件
        socket.onerror = function() {
            console.log("发生了错误");
        }
    });

    //发送消息
    document.querySelector("#btnSend").addEventListener("click", function() {
        socket.send("这是来自客户端的消息" + location.href + new Date());
    });

    //关闭
    document.querySelector("#btnClose").addEventListener("click", function() {
        socket.close();
    });

    function check(){
        if (typeof (WebSocket) == "undefined") {
            document.getElementById("checkText").innerHTML = "您的浏览器不支持WebSocket";
        }else{
            document.getElementById("checkText").innerText = "可以使用 WebSocket";
        }
    }
</script>
</html>
```
