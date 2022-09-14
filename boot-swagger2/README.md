## 说明

这里是介绍如何通过 SpringBoot 去集成 Swagger2 实现自动生成 API 文档。

由于官方提供的 SwaggerUI 太 low，这里换了一个 knife4j。

## 具体实现

### 第一步：引入依赖

```xml
 <dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-ui</artifactId>
    <version>3.0.3</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>3.0.0</version>
</dependency>
```

另外说明下，如果是使用官方的UI，需要引入的依赖为：
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>3.0.0</version>
</dependency>
```


### 第二步：增加配置类

```java
@Configuration
@EnableSwagger2 // 增加启动 EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()) //  添加说明
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.demo.swagger"))    //需要扫描的基础包
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SpringBoot利用swagger构建api文档")
                .description("简单优雅的restfun风格，https://blog.csdn.net/qq_18948359")
                .termsOfServiceUrl("https://blog.csdn.net/qq_18948359")
                .version("1.0")
                .build();
    }
}
```

### 第三步：对应的 Controller

```java
@Api(tags = "SwaggerController")
@RestController
public class SwaggerController {

    @ApiOperation(value="保存用户", notes="根据传入的值，保存")
//    @ApiImplicitParams({@ApiImplicitParam(name = "User", value = "员工信息")})
//    @ApiImplicitParam(name = "User", value = "用户详细实体user", required = true, dataTypeClass = User.class)
    @RequestMapping("/save")
    public User save(User user) {
        return user;
    }

    @ApiOperation(value = "查询单个接口", notes = "根据url的name来获取用户详细信息", response = User.class)
//    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "员工名称")})
    @ApiImplicitParam(name = "name", value = "用户名称", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @RequestMapping("/findByName")
    public User findByName(String name) {
        System.out.println(name);
        return new User();
    }

    @ApiOperation(value="查询所有接口", notes="根据url的name来获取用户详细信息")
//    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "员工名称")})
    @ApiImplicitParam(name = "name", value = "用户名称", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @RequestMapping("/findAll")
    public List<User> findAll(String name) {
        return new ArrayList<>();
    }
}
```

### 第四步：增加 实体类
```java
@ApiModel
public class User {
    @ApiModelProperty(value = "用户id")
    private Integer id;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "用户地址")
    private String address;
    //getter/setter
}
```

### 测试启动

在启动的时候发现报错

```
2022-09-14 21:40:35.778  WARN 27192 --- [           main] ConfigServletWebServerApplicationContext : Exception encountered during context initialization - cancelling refresh attempt: org.springframework.context.ApplicationContextException: 
    Failed to start bean 'documentationPluginsBootstrapper'; nested exception is java.lang.NullPointerException: Cannot invoke "org.springframework.web.servlet.mvc.condition.PatternsRequestCondition.getPatterns()" because "this.condition" is null
```

原因是在 springboot2.6.0 中将SpringMVC 默认路径匹配策略从 `AntPathMatcher` 更改为 `PathPatternParser`，导致出错，解决办法是切换回原先的 `AntPathMatcher`

因此在配置文件中增加配置
```yaml
spring:
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
```

再次启动就可以了

### 访问

http://127.0.0.1:8080/doc.html#/home


### 整理报错相关

- for input "" 报错

- 访问页面报错
