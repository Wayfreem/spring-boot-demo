## 简介

使用 JPA 链接MySQL 数据库。

## 具体实现

### 第一步：集成相关依赖

**pom.xml**

集成JPA、spring boot、mysql 相关的依赖包

```xml
<dependencies>
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
</dependencies>
```

### 第二步：增加配置文件内容

```properties
server.port=8080

# jpa config
spring.datasource.url=jdbc:mysql://192.168.152.129:3306/study?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8
spring.datasource.username=admin
spring.datasource.password=123456
spring.jpa.generate-ddl=true
spring.jpa.show-sql=true

logging.level.com.demo.orm.jpa=debug
logging.level.org.hibernate.type.descriptor.sql=TRACE
```

### 第三步：创建模型以及 repository

**模型类**
```java
@Data
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)  //  设置主键自增
    private String id;
    private String name;
    private String sex;
    private String email;
    private String lastname;

    @Version
    private Long version;

}

```

**repository 类**

```java
public interface UserRepository extends JpaRepository<User, String> {
}
```

## 启动程序

**关于JPA 使用的 dialect**

项目启动的时候可以看到控制台输出了具体的使用 方言 dialect 的类 为 `MySQL8Dialect`，以及创建表的SQL 语句。
```
2022-07-01 17:40:28.685  INFO 15048 --- [           main] org.hibernate.dialect.Dialect            : HHH000400: Using dialect: org.hibernate.dialect.MySQL8Dialect

Hibernate: create table hibernate_sequence (next_val bigint) engine=InnoDB
Hibernate: insert into hibernate_sequence values ( 1 )
Hibernate: create table user (id varchar(255) not null, email varchar(255), lastname varchar(255), name varchar(255), sex varchar(255), version bigint, primary key (id)) engine=InnoDB
```