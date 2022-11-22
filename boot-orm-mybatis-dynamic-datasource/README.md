
## 简介
使用 spring boot 集成 mybatis 实现动态数据源操作。

既然是需要连接数据库，就需要安装 MySQL(采用docker 安装) [安装参考链接](https://blog.csdn.net/qq_18948359/article/details/125486934?spm=1001.2014.3001.5502)


## 集成的步骤

我们需要配置多个数据源，然后使用 注解的方式实现动态切换，

### 第一步： 引入依赖

POM 文件
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- 添加AOP坐标 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<!--    mybatis 依赖    -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.29</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

### 第二步：增加对应的配置

**application.yml** 

这里配置多个数据源，以及设置日志输出，便于查看控制台消息

```yaml
# 基本配置
server:
  port: 8080

# 数据库
spring:
  datasource:
    primary:
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://192.168.31.215:3306/study_main?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8
      username: admin
      password: 123456
    second:
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://192.168.31.215:3306/study_dev?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8
      username: admin
      password: 123456

  jackson:
    serialization:
      indent-output: true

mybatis:
  mapper-locations: classpath:mapper/*.xml

## logging
logging:
  level:
    com.demo.orm.mybatis.dynamic.datasource : debug
```

### 第三步：对应的数据源配置

**DynamicDataSource.java** 

这里需要集成于 `AbstractRoutingDataSource`, 用于将数据源保存到其中，切换数据源的时候会用到。

```java
/**
 * 动态数据源类
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }

    public static void setDataSource(String dataSource) {
        contextHolder.set(dataSource);
    }

    public static String getDataSource() {
        return contextHolder.get();
    }

    public static void clearDataSource() {
        contextHolder.remove();
    }
}
```

**DynamicDataSourceConfig.java**

这里是增加对应的数据源配置类，获取到对应的数据源

```java
/**
 * 多数据源配置类
 */
@Configuration
public class DynamicDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource primaryDataSource, DataSource secondDataSource) {
        // 这里新建一个 Map 是将对应的数据源放入其中，然后给后面使用的时候来获取数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("primary-source",primaryDataSource);
        targetDataSources.put("second-source", secondDataSource);
        return new DynamicDataSource(primaryDataSource, targetDataSources);
    }
}
```


### 第四步：增加注解以及切换数据源的操作

定义注解 `@DataSource`

```java
/**
 * 备注：自定义数据源选择注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String name() default "";
}
```

**DataSourceAspect**

动态切换数据源的切面类，通过这里去获取对应的数据源

```java
@Aspect
@Component
public class DataSourceAspect {

    @Pointcut("@annotation(com.demo.orm.mybatis.dynamic.datasource.annotation.DataSource)")
    public void dataSourcePointCut() {
    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        DataSource dataSource = method.getAnnotation(DataSource.class);
        if(dataSource == null){
            DynamicDataSource.setDataSource("primary-source");  //  获取数据源
        }else {
            DynamicDataSource.setDataSource(dataSource.name());
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }
}
```


### 第五步：增加相关的模型类

**Student**
```java
@Data
public class Student implements Serializable{

    private Long id;
    private String name;
    private int sex;
    private String grade;
}
```

**Teacher**
```java
@Data
public class Teacher implements Serializable{

    private Long id;
    private String name;
    private int sex;
    private String office;
}
```

### 第六步：增加对应 mapper 以及 xml 文件

**StudentMapper**

```java
public interface StudentMapper {
    int save(@Param("studentDO") Student student);
    List<Student> queryAll();
}
```

**StudentMapper.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.demo.orm.mybatis.dynamic.datasource.mapper.StudentMapper">
    <resultMap id="BaseResultMap" type="com.demo.orm.mybatis.dynamic.datasource.model.Student">
        <id column="id" jdbcType="BIGINT" property="id" />
        <result column="name" jdbcType="VARCHAR" property="name" />
        <result column="sex" jdbcType="INTEGER" property="sex" />
        <result column="grade" jdbcType="VARCHAR" property="grade" />
    </resultMap>

    <insert id="save">
        INSERT INTO student (user_name, sex, grade) VALUES (#{studentDO.name}, #{studentDO.sex}, #{studentDO.grade});
    </insert>

    <select id="queryAll" resultType="com.demo.orm.mybatis.dynamic.datasource.model.Student">
        select * from student;
    </select>
</mapper>
```


**TeacherMapper**
```java
public interface TeacherMapper {
    int save(@Param("teacherDO") Teacher teacher);
    List<Student> queryAll();
}
```

**TeacherMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.demo.orm.mybatis.dynamic.datasource.mapper.TeacherMapper">
    <resultMap id="BaseResultMap" type="com.demo.orm.mybatis.dynamic.datasource.model.Teacher">
        <id column="id" jdbcType="BIGINT" property="id" />
        <result column="name" jdbcType="VARCHAR" property="name" />
        <result column="sex" jdbcType="INTEGER" property="sex" />
        <result column="office" jdbcType="VARCHAR" property="office" />
    </resultMap>

    <insert id="save">
        INSERT INTO teacher ( user_name, sex, office) VALUES (#{teacherDO.name}, #{teacherDO.sex}, #{teacherDO.office});
    </insert>

    <select id="queryAll" resultType="java.util.Map">
        select * from teacher;
    </select>
</mapper>
```


### 第七步：增加 controller

```java
@RestController
public class TestController {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @GetMapping("/{name}/list")
    public List<Student> list(@PathVariable("name") String name){
        System.out.println(name);
        return studentMapper.queryAll();
    }

    @DataSource(name="primary-source")
    @PostMapping(value="/primary")
    public Object findAll() {
        return studentMapper.queryAll();
    }

    @DataSource(name="second-source")
    @PostMapping(value="/second")
    public Object findAll2() {
        return teacherMapper.queryAll();
    }
}
```

### 第八步：测试

通过 HTTP 请求对应的 controller 接口就可以看到对应的效果了
