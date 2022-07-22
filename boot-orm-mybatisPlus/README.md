
[官网地址](https://baomidou.com/pages/24112f/)

## 简介

使用 spring boot 集成 Mybatis Plus 项目。

## 集成的步骤

### 第一步：引入依赖

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.2</version>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.29</version>
    <scope>runtime</scope>
</dependency>
```

### 第二步：创建于数据库对应的模型类

```java
@Data
@TableName("user")      // 表名映射
public class User {

    @TableId("id")     // 主键映射，如果指定名称为 user_id 则表示数据库中存在的列名未  user_id
    private Long id;

    @TableField("name")     // 一般的表名映射
    private String name;

    @TableField(exist = false)
    private Integer age;

    @TableField(select = true)  // 表示在查询语句中显示改列（投影操作），默认为true
    private String email;

    // 在数据库中不存在的字段，以下处理不会序列化到数据库中
    @TableField(exist = false)
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
```

### 第三步：创建 mapper

```java
public interface UserMapper extends BaseMapper<User> {

    // 使用自定义的查询时，不会自动加入 delete = 0 逻辑删除的语句
    @Select("select * from User ${ew.customSqlSegment}")
    List<User> selectCustomSql(@Param(Constants.WRAPPER) Wrapper<User> wrapper);


    List<User> selectCustomSqlByXML(@Param(Constants.WRAPPER) Wrapper<User> wrapper);

    IPage<User> selectCustomPage(Page<User> page, @Param(Constants.WRAPPER) Wrapper<User> wrapper);
}
```

### 第四步：在启动类中增加 `@MapperScan` 注解
```java
@SpringBootApplication
@MapperScan("com.demo.orm.mybatisPlus.mapper")
public class MybatisPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusApplication.class, args);
    }
}
```

### 第五步：创建对应的 xml
```xml
<!-- namespace 指向于对应实体的 Mapper 接口-->
<mapper namespace="com.demo.orm.mybatisPlus.mapper.UserMapper">

    <select id="selectCustomSqlByXML" resultType="com.demo.orm.mybatisPlus.model.User">
        select * from User ${ew.customSqlSegment}
    </select>

    <!-- 会报错 -->
    <select id="selectCustomPage" resultType="com.demo.orm.mybatisPlus.model.User">
        select * from aut_users ${ew.customSqlSegment}
    </select>
</mapper>
```

### 第六步：创建 controller

```java
@RestController
public class TestController {

    @Resource
    private UserMapper userMapper;


    @RequestMapping("selectUser")
    public List<User> selectUser(){
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
        return userList;
    }

    @RequestMapping("insert")
    public void insert(){
        User user = new User();
        user.setId(2l);
        user.setAge(20);
        user.setName("测试员10");
        int count = userMapper.insert(user);
        System.out.println( "插入记录数：" + count );
    }

    @RequestMapping("upate")
    public void testUpdate(){
        User user = new User();
        user.setId(10l);
        user.setUpdateTime(LocalDateTime.now());
        int count = userMapper.updateById(user);
        System.out.println( "更新记录数：" + count );

        selectUser();
    }

}
```

## 测试类

具体可以看下在 test 包下面的测试类 `SpringbootMybatisPlusApplicationTests`
