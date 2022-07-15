[Spring 官方地址](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongodb-getting-started)

## 简介

Spring boot 集成 MongoDB 的例子

## 集成的步骤

### 第一步：增加依赖
增加对应的 MongoDB 依赖包
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### 第二步：增加配置文件
```properties
spring.data.mongodb.username=admin
spring.data.mongodb.password=123456
spring.data.mongodb.host=192.168.152.129
spring.data.mongodb.port=27017
spring.data.mongodb.database=admin
```

### 第三步：增加对应的模型实体类以及 repository

这里我们新建一个模型实体类，使用JPA 的 repository 接口来进行 MongoDB 的访问

```java
@Data
@Document(collation = "test")   // 这里指定对应的集合名称
public class User implements Serializable {

    @Id
    private String id;

    private String userId;

    private String fileName;

    public User(String id, String userId, String fileName) {
        this.id = id;
        this.userId = userId;
        this.fileName = fileName;
    }
}
```
创建 repository 

```java
public interface UserRepository extends MongoRepository<User, String> {
}
```

### 第四步：创建 controller 以及 service 用于测试
TestController

```java
@RestController
public class TestController {

    @Autowired
    UserService userService;

    @RequestMapping("save")
    public User save() {
        return userService.save();
    }

    @RequestMapping("findById")
    public User findById(String id) {
        return userService.findById(id);
    }
}
```

UserService
```java
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User save(){
        return userRepository.save(new User("00001", "temp01", "java"));
    }

    public User findById(String id){
        return userRepository.findById(id).get();
    }
}
```