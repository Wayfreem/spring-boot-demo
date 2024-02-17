## 项目说明

1. 项目基于前面模块 `boot-email`，增加使用 `thymeleaf` 模板，并使用 `spring-boot-starter-thymeleaf` 依赖。
2. 项目增加 `application.properties` 配置文件，配置 `thymeleaf` 模板的路径。
3. 项目增加 `templates` 目录，用于存放 `thymeleaf` 模板文件。

## 项目集成

看下 `boot-email` 模块下的 `pom.xml` 文件， 在 `boot-email` 依赖的基础上面，需要引入 `spring-boot-starter-thymeleaf` 依赖

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

这里，我们重点看下下面代码

```java
Context context = new Context();
// 设置模板数据, 这里是一个map数据
context.setVariables(getMap());
// 获取thymeleaf的html模板， 这里的 student 就是模板文件名
String emailContent = templateEngine.process("student", context);

```