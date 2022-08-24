## 说明

在 yml 配置中，有一个新的属性 `include`, 通过 `include`，可以导入一些公共的配置文件进来

```yml
spring:
  profiles:
    active: dev
    include:
      - log
```

当在程序启动的时候，会发现，控台中会出现这么一行日志信息

```logger
2022-08-24 22:42:36.379  INFO 18552 --- [           main] c.demo.autoConfig.AutoConfigApplication  : The following 2 profiles are active: "log", "dev"
```