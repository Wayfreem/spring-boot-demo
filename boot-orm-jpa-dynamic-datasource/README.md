## 简介

使用 JPA 链接MySQL 数据库，并且实现动态数据源对数据库做操作。

微服务推崇单服务单数据库；但是还是免不了存在一个微服务连接多个数据库的情况，今天介绍一下如何使用 JPA 的多数据源。

当我们在做数据迁移或者多库操作的时候，可以使用多数据源的操作。

## 具体实现

我们需要配置多个数据源，然后使用 注解的方式实现动态切换，

### 前提步骤

**创建数据库**

    创建两个数据库 db01 和 db02

**创建对应的表**

**学生表 student**  在数据库 db01 中创建

```sql
CREATE TABLE `student` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`user_name`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`sex`  int(1) NULL DEFAULT NULL ,
`grade`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC;
```

**教师表 t_teacher** 在数据库 db02 中创建
```sql
CREATE TABLE `teacher` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`user_name`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`sex`  int(1) NULL DEFAULT NULL ,
`office`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC;
```

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
  jpa:
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
    hibernate:
      ddl-auto: update
  datasource:
    primary:
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://127.0.0.1:3306/db01?characterEncoding=utf-8&allowMultiQueries=true&autoReconnect=true
      username: root
      password: root
    second:
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://127.0.0.1:3306/db02?characterEncoding=utf-8&allowMultiQueries=true&autoReconnect=true
      username: root
      password: root

  jackson:
    serialization:
      indent-output: true
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

**特殊说明**

由于我们是使用了 JPA，那么这里肯定是有一个问题在，就是我们在配置文件中配置了对应的自动创建表结构时，那么多数据源是怎么操作的呢？

因为我们在这里设置了一个默认的数据源，所以在程序启动的时候，会自动将对应的表初始化到默认的数据源中去。这里需要留意下。


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
@Entity
@Table(name = "Student")
public class Student implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int sex;
    private String grade;
}

```

**Teacher**
```java
@Data
@Entity
@Table(name = "Teacher")
public class Teacher implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name") // 若实体属性和表字段名称一致时，可以不用加@Column注解
    private String name;
    
    private int sex;
    private String office;
}

```

### 第六步：增加对应 repository

**StudentRepository**

```java
public interface StudentRepository extends JpaRepository<Student, Long> {
}
```

**TeacherRepository**
```java
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
```


### 第七步：增加 controller

```java
@RestController
public class TestController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;


    @GetMapping("/{name}/list")
    public List<Student> list(@PathVariable("name") String name){
        System.out.println(name);
        return studentRepository.findAll();
    }

    @DataSource(name="primary-source")
    @PostMapping(value="/primary")
    public Object findAll() {
        return studentRepository.findAll();
    }

    @DataSource(name="second-source")
    @PostMapping(value="/second")
    public Object findAll2() {
        return teacherRepository.findAll();
    }
}
```

### 第八步：测试

通过 HTTP 请求对应的 controller 接口就可以看到对应的效果了
