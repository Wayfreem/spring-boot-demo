## 简介

使用 JPA 链接MySQL 数据库，并且实现多数据源对数据库做操作。

微服务推崇单服务单数据库；但是还是免不了存在一个微服务连接多个数据库的情况，今天介绍一下如何使用 JPA 的多数据源。

主要采用将不同数据库的 Repository 接口分别存放到不同的 package，Spring 去扫描不同的包，注入不同的数据源来实现多数据源。

## 具体实现

### 前提步骤

创建两个数据库 db01 和 db02 

**学生表 t_student**

```sql
CREATE TABLE `t_student` (
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

**教师表 t_teacher**
```sql
CREATE TABLE `t_teacher` (
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
      driver-class-name: com.mysql.jdbc.Driver
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

### 第三步：配置数据源

DataSourceConfig 配置

```java
/**
 * @Description: 数据源配置
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "primaryDataSource")
    @Qualifier("primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    @Primary
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "secondDataSource")
    @Qualifier("secondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create().build();
    }
}
```

PrimaryConfig数据源

```java
/**
 * @Description: 主数据源配置
 * @date
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryPrimary",
                        transactionManagerRef = "transactionManagerPrimary",
                        basePackages = {"com.olive.repository.primary"})
public class PrimaryConfig {

    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;

    @Autowired
    private HibernateProperties hibernateProperties;

    @Autowired
    private JpaProperties jpaProperties;

    @Primary
    @Bean(name = "entityManagerPrimary")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "entityManagerFactoryPrimary")    //primary实体工厂
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary (EntityManagerFactoryBuilder builder) {
        return builder.dataSource(primaryDataSource)
                .properties(getHibernateProperties())
                .packages("com.olive.entity.primary")     //换成你自己的实体类所在位置
                .persistenceUnit("primaryPersistenceUnit")
                .build();
    }

    @Primary
    @Bean(name = "transactionManagerPrimary")
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
    }

    private Map<String, Object> getHibernateProperties() {
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

}
```

SecondConfig 数据源源

```java
/**
 * @Description: 第二个数据源配置
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactorySecond",
                        transactionManagerRef = "transactionManagerSecond",
                        basePackages = {"com.olive.repository.second"})
public class SecondConfig {

    @Autowired
    @Qualifier("secondDataSource")
    private DataSource secondDataSource;

    @Resource
    private JpaProperties jpaProperties;

    @Resource
    private HibernateProperties hibernateProperties;

    @Bean(name = "entityManagerSecond")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySecond(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactorySecond")    //primary实体工厂
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySecond (EntityManagerFactoryBuilder builder) {

        return builder.dataSource(secondDataSource)
                .properties(getHibernateProperties())
                .packages("com.olive.entity.second")     //换成你自己的实体类所在位置
                .persistenceUnit("secondaryPersistenceUnit")
                .build();
    }

    @Bean(name = "transactionManagerSecond")
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySecond(builder).getObject());
    }

    private Map<String, Object> getHibernateProperties() {
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

}
```

### 第四步：创建模型以及 repository

**模型类**
```java
@Data
@Entity
@Table(name = "User")
public class User {

    @Id
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

### 第四步：创建 controller 以及 service

controller 层
```java
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("save")
    public User save(){
        return userService.save();
    }
    
}
```
service 层

```java
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User save(){
        User user = new User();
        user.setId("0001");
        user.setEmail("wuq@google.com");
        user.setName("wuq");
        user.setLastname("Q");
        return userRepository.saveAndFlush(user);
    }
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

## 关于日志输出

需要引入依赖包

pom.xml

```xml
 <dependency>
   <groupId>com.integralblue</groupId>
   <artifactId>log4jdbc-spring-boot-starter</artifactId>
   <version>1.0.2</version>
   <scope>runtime</scope>
</dependency>
```

application.properties

```properties
logging.level.com.demo=debug
logging.level.jdbc.sqlonly=WARN
logging.level.jdbc.sqltiming=INFO
logging.level.jdbc.resultsettable=WARN
logging.level.jdbc.resultset=WARN
logging.level.jdbc.connection=WARN
logging.level.jdbc.audit=WARN
```