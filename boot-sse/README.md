## 关于 SSE

sse 全程 Server Send Event，是 HTTP 协议中的一种，`Content-Type` 为 `text/event-stream`，是服务端主动向前端推送数据。类似于 WebSocket。

sse 优势我们可以划分为两个：
- 长链接
- 服务端能主动向客户端推送数据


### sse 规范

在 html5 的定义中，服务端 sse，通常须要遵循如下要求

**请求头**

开启长链接 + 流方式传递

```
Content-Type: text/event-stream;charset=UTF-8
Cache-Control: no-cache
Connection: keep-alive
```

**数据格式**

服务端发送的消息，由 message 组成，其格式以下:

```
field:value\n\n
```

其中 field 有五种可能

- 空： 即以 : 开头，表示注释，能够理解为服务端向客户端发送的心跳，确保链接不中断
- data：数据
- event： 事件，默认值
- id： 数据标识符用 id 字段表示，至关于每一条数据的编号
- retry： 重连时间


## 在 Spring 框架中实现

这里需要允许跨域

```java
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
public class SSEController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
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
```

前端测试

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<script type="text/javascript">
    // 初始化，参数为url
    // 依赖H5
    var sse = new EventSource("SSE")

    // 监听消息并打印
    sse.onmessage = function (evt) {
        console.log("message", evt.data, evt)
    }

    // 如果指定了事件标识需要用这种方式来进行监听事件流
    sse.addEventListener("me", function (evt) {
        console.log("me event", evt.data)
        // 事件流如果不关闭会自动刷新请求，所以我们需要根据条件手动关闭
        if (evt.data == 3) {
            sse.close()
        }
    })
</script>
</body>
</html>
```

### 完整的工具类使用

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
public class SseEmitterUtil {

    /**
     * 当前连接数
     */
    private static AtomicInteger count = new AtomicInteger(0);

    /**
     * 使用 map 对象，便于根据 userId 来获取对应的 SseEmitter，或者放 redis 里面
     */
    private static Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    /**
     * 创建用户连接并返回 SseEmitter
     *
     * @param userId 用户ID
     * @return SseEmitter
     */
    public static SseEmitter connect(String userId) {
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter();
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(userId));
        sseEmitter.onError(errorCallBack(userId));
        sseEmitter.onTimeout(timeoutCallBack(userId));

        // 缓存
        sseEmitterMap.put(userId, sseEmitter);

        // 数量+1
        count.getAndIncrement();
        log.info("创建新的sse连接，当前用户：{}", userId);
        return sseEmitter;
    }

    /**
     * 给指定用户发送信息
     */
    public static void sendMessage(String userId, String message) {
        if (!sseEmitterMap.containsKey(userId)) return;

        try {
            // sseEmitterMap.get(userId).send(message, MediaType.APPLICATION_JSON);
            sseEmitterMap.get(userId).send(message);
        } catch (IOException e) {
            log.error("用户[{}]推送异常:{}", userId, e.getMessage());
            removeUser(userId);
        }
    }

    /**
     * 群发消息
     */
    public static void batchSendMessage(String wsInfo, List<String> ids) {
        ids.forEach(userId -> sendMessage(wsInfo, userId));
    }

    /**
     * 群发所有人
     */
    public static void batchSendMessage(String wsInfo) {
        sseEmitterMap.forEach((k, v) -> {
            try {
                v.send(wsInfo, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                log.error("用户[{}]推送异常:{}", k, e.getMessage());
                removeUser(k);
            }
        });
    }

    /**
     * 移除用户连接
     */
    public static void removeUser(String userId) {
        sseEmitterMap.remove(userId);
        // 数量-1
        count.getAndDecrement();
        log.info("移除用户：{}", userId);
    }

    /**
     * 获取当前连接信息
     */
    public static List<String> getIds() {
        return new ArrayList<>(sseEmitterMap.keySet());
    }

    /**
     * 获取当前连接数量
     */
    public static int getUserCount() {
        return count.intValue();
    }

    private static Runnable completionCallBack(String userId) {
        return () -> {
            log.info("结束连接：{}", userId);
            removeUser(userId);
        };
    }

    private static Runnable timeoutCallBack(String userId) {
        return () -> {
            log.info("连接超时：{}", userId);
            removeUser(userId);
        };
    }

    private static Consumer<Throwable> errorCallBack(String userId) {
        return throwable -> {
            log.info("连接异常：{}", userId);
            removeUser(userId);
        };
    }
}
```


```java
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
```
前端测试

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>消息推送</title>
</head>
<body>
    <div>
        <button onclick="closeSse()">关闭连接</button>
        <div id="message"></div>
    </div>
</body>

<script>
    let source = null;

    // 用时间戳模拟登录用户
    const userId = new Date().getTime();

    if (window.EventSource) {

        // 建立连接
        source = new EventSource('http://localhost:8080/sse/connect/' + userId);

        /**
         * 连接一旦建立，就会触发open事件
         * 另一种写法：source.onopen = function (event) {}
         */
        source.addEventListener('open', function (e) {
            setMessageInnerHTML("建立连接。。。");
        }, false);

        /**
         * 客户端收到服务器发来的数据
         * 另一种写法：source.onmessage = function (event) {}
         */
        source.addEventListener('message', function (e) {
            setMessageInnerHTML(e.data);
        });


        /**
         * 如果发生通信错误（比如连接中断），就会触发error事件
         * 或者：
         * 另一种写法：source.onerror = function (event) {}
         */
        source.addEventListener('error', function (e) {
            if (e.readyState === EventSource.CLOSED) {
                setMessageInnerHTML("连接关闭");
            } else {
                console.log(e);
            }
        }, false);

    } else {
        setMessageInnerHTML("你的浏览器不支持SSE");
    }

    // 监听窗口关闭事件，主动去关闭sse连接，如果服务端设置永不过期，浏览器关闭后手动清理服务端数据
    window.onbeforeunload = function () {
        closeSse();
    };

    // 关闭Sse连接
    function closeSse() {
        source.close();
        const httpRequest = new XMLHttpRequest();
        httpRequest.open('GET', 'http://localhost:8080/sse/close/' + userId, true);
        httpRequest.send();
        console.log("close");
    }

    // 将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }
</script>
</html>
```