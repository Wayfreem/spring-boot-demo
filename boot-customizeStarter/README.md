## 工程说明

这里是自定义个 spring boot starter，更好的掌握 spring boot starter 的知识。了解 spring boot starter 的加载机制。

## 集成说明

在 resources 文件下创建 一个 META-INF 的文件夹，然后再 META-INF 文件夹里面创建 spring.factories 文件，这里是告诉程序自动装配的地址

```factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.demo.configuration.HelloWorldAutoConfiguration
```