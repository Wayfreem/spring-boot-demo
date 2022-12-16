## 说明
在 JPA 中使用 CompletableFuture 提升处理效率。

由于使用 CompletableFuture 是使用异步线程，会出现事务不一致的情况，下面我们会一点点去的测试，使用 CompletableFuture 达到我们预想的效果。

## 具体实现


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



### 第二步：增加模型与 Repository

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

### 第三步

