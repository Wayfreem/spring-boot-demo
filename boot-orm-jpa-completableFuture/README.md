## 说明
在 JPA 中使用 CompletableFuture 提升处理效率。

由于使用 CompletableFuture 是使用异步线程，会出现事务不一致的情况，下面我们会一点点去的测试，使用 CompletableFuture 达到我们预想的效果。

## 项目搭建

### 第一步：引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!--  jpa 依赖     -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
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

<dependency>
    <groupId>com.integralblue</groupId>
    <artifactId>log4jdbc-spring-boot-starter</artifactId>
    <version>1.0.2</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>2.0.14</version>
</dependency>
```

### 第二步：增加配置文件内容

在 mysql 的链接中，增加参数 `logger=Slf4JLogger&profileSQL=true` 可以看到事务日志信息

```properties
server.port=8080

# jpa config
## add config `logger=Slf4JLogger&profileSQL=true`  to  logger the log of transaction
## eg: spring.datasource.url=jdbc:mysql://localhost:3306/test?logger=Slf4JLogger&profileSQL=true
spring.datasource.url=jdbc:mysql://192.168.152.129:3306/study?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8
spring.datasource.username=admin
spring.datasource.password=123456

# auto create table
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

logging.level.com.demo=debug
logging.level.jdbc.sqlonly=WARN
logging.level.jdbc.sqltiming=INFO
logging.level.jdbc.resultsettable=WARN
logging.level.jdbc.resultset=WARN
logging.level.jdbc.connection=WARN
logging.level.jdbc.audit=WARN


## open under logger lever to watch the open and close of transaction
#logging.level.org.springframework.orm.jpa=DEBUG
#logging.level.org.springframework.transaction=DEBUG
#logging.level.org.springframework.orm.jpa.JpaTransactionManager=trace
#logging.level.org.hibernate.engine.transaction.internal.TransactionImpl=DEBUG
```

### 第三步：增加模型与 Repository

**model 类**

```java
@Entity
@Data
@Table(name = "T_order")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String orderNo;
    private Long customerId;
    private int status;

    @Version
    private Integer version;
}
```

**Repository**

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
}
```


### 第四步: 增加 controller 

由于我们会使用到异步操作，对于异步操作最好是自己新建一个连接池，我这里偷懒，直接使用了 Spring 默认的线程池

```java

```