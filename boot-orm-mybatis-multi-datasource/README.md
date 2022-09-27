
## 简介
使用 spring boot 集成 mybatis 实现多数据源对数据库进行操作。

既然是需要连接数据库，就需要安装 MySQL(采用docker 安装) [安装参考链接](https://blog.csdn.net/qq_18948359/article/details/125486934?spm=1001.2014.3001.5502)

## 集成的步骤

### 第一步：引入依赖

**pom 文件**

需要加入 mybatis 的的依赖，以及对应的驱动

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
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

### 第二步：修改配置文件

application.properties

```properties
# server port config
server.port=8080

# mysql config
spring.jpa.database=MYSQL
spring.datasource.url=jdbc:mysql://192.168.152.129:3306/study?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=admin
spring.datasource.password=123456

#mybatis xml Mapping path
mybatis.mapper-locations=classpath:mapping/*Mapper.xml
mybatis.type-aliases-package=com.demo.orm.mybatis.*

# standout logging
logging.level.com.demo.orm.mybatis=debug
```

### 第三步：创建 实体类 以及 mapper

**实体类 user**

```java
import lombok.Data;

@Data
public class User {

    private String id;
    private String name;
    private String sex;
    private String email;
    private String lastname;
}
```

**Mapper 类**

mybatis 提供了两种查询的方案，一种就是原始的基于 xml 的查询方式，另外一种是基于 `@Select` 注解的实现方式

```java
@Repository
@Mapper
public interface UserMapper {

    User selectById(String id);

    List<User> selectByName(String name);

    @Select("select * from user where lastname = #{lastname}")
    List<User> getUserByLastname(@Param("lastname") String lastname);
}
```

Mapper 类是关联了一个对应的 xml 文件，这里需要新增一个 xml 文件,由于在 properties 文件中已经指定了路劲，所有文件地址在 resources/mapping/ 创建

**UserMapper.xml**
```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.demo.orm.mybatis.mapper.UserMapper">

    <resultMap id="BaseResultMap" type="com.demo.orm.mybatis.entity.User">
        <result column="ID" jdbcType="VARCHAR" property="id"/>
        <result column="NAME" jdbcType="VARCHAR" property="name"/>
        <result column="PASSWORD" jdbcType="VARCHAR" property="password"/>
    </resultMap>

    <select id="selectById" resultType="com.demo.orm.mybatis.entity.User">
        select * from user where id = #{id}
    </select>

    <select id="selectByName" resultType="com.demo.orm.mybatis.entity.User">
        select * from user where name = #{name}
    </select>
</mapper>
```

### 第四步：创建 controller 以及 service

**UserController**

```java
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("getUser/{id}")
    public String GetUser(@PathVariable String id){
        return userService.selectById(id).toString();
    }

    /**
     * 访问地址
     * http://localhost:8080/getUserByName?name=管理员
     * @param name name
     * @return List<User>
     */
    @RequestMapping("getUserByName")
    public List<User> getUserByName(String name) {
        return userService.selectByName(name);
    }

    /**
     * 访问地址
     * http://localhost:8080/getUserByLastname?lastname=Q
     * @param lastname
     * @return List<User>
     */
    @RequestMapping("getUserByLastname")
    public List<User> getUserByLastname(String lastname){
        return userService.getUserByLastname(lastname);
    }
}
```

**UserService**
```java
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User selectById(String id) {
        return userMapper.selectById(id);
    }

    public List<User> selectByName(String name){
        return userMapper.selectByName(name);
    }

    public List<User> getUserByLastname(String lastName){
        return userMapper.getUserByLastname(lastName);
    }
}
```