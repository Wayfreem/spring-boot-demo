## 说明

- https://blog.csdn.net/weixin_41307800/article/details/124604598
- https://mp.weixin.qq.com/s/0d6cMyWqXmjCkNxdZ15NG
- https://blog.csdn.net/qq_44981526/article/details/125657331


Spring Retry 为 Spring 应用程序提供了声明性重试支持。

在日常开发工作中，总会遇到存在有调用一个接口或者服务方法报错，然后需要重新调用该接口或者服务方法的情况。

常用的方法就是使用 `try{}catch{}` 或者 `while` 循环之类的语法来进行相应的操作。但是这个不是一个好的处理方案，不能做到统一管理。这时，我们可以使用 `Spring Retry` 来实现。

## 集成说明

### 第一步：引入依赖

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
```