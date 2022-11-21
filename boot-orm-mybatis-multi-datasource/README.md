
## 简介
使用 spring boot 集成 mybatis 实现多数据源对数据库进行操作。

既然是需要连接数据库，就需要安装 MySQL(采用docker 安装) [安装参考链接](https://blog.csdn.net/qq_18948359/article/details/125486934?spm=1001.2014.3001.5502)

## 集成的步骤


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
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
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

```yaml
# 基本配置
server:
  port: 8080

# 数据库
spring:
  datasource:
    primary:
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://192.168.1.103:3306/study_main?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8
      username: admin
      password: 123456
    second:
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://192.168.1.103:3306/study_dev?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8
      username: admin
      password: 123456

  jackson:
    serialization:
      indent-output: true
```

### 第三步：数据源配置

因为配置数据库的 key 变化了，导致上述配置无法被 Spring Boot 自动加载，需要我们自己去加载。
增加 DataSourceConfig 数据源配置类，使用 Spring Boot 提供的类型安全的属性注入方式来加载上述配置，并创建对应的两个数据源 DataSource 实例，如下：

```java
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

### 第四步：Mybatis 配置

接下来是 MyBatis 的配置，新增 MyBatisConfigOne 和 MyBatisConfigTwo 两个配置类，
用上述两个数据源分别创建对应的 SqlSessionFactory 和 SqlSessionTemplate 实例（注意 Bean 的名称要不一样），分别如下：

```java
@Configuration
@MapperScan(basePackages = "com.demo.orm.mybatis.dynamic.datasource.mapper.primary",
        sqlSessionTemplateRef = "primarySqlSessionTemplate")
public class PrimaryConfig {

    // 此时 Spring 容器中有两个 DataSource 类型的 Bean ，所以这里需要按名称 byName 查找
    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;

    @Bean(name = "primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory primarySqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(primaryDataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/primary/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "primaryTransactionManager")
    @Primary
    public DataSourceTransactionManager primaryTransactionManager() {
        return new DataSourceTransactionManager(primaryDataSource);
    }

    @Bean(name = "primarySqlSessionTemplate")
    @Primary
    public SqlSessionTemplate primarySqlSessionTemplate(@Qualifier("primarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```

```java
@Configuration
@MapperScan(basePackages = "com.demo.orm.mybatis.multi.datasource.mapper.second",
        sqlSessionTemplateRef = "secondSqlSessionTemplate")
public class SecondConfig {

    @Autowired
    @Qualifier("secondDataSource")
    private DataSource secondDataSource;

    @Bean(name = "secondSqlSessionFactory")
    public SqlSessionFactory secondSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(secondDataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/second/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "secondTransactionManager")
    public DataSourceTransactionManager secondTransactionManager() {
        return new DataSourceTransactionManager(secondDataSource);
    }

    @Bean(name = "secondSqlSessionTemplate")
    public SqlSessionTemplate secondSqlSessionTemplate(@Qualifier("secondSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```

**关于 MyBatis 配置类的说明：**

- 配置 mapper 的位置：通过 basePackages 分别配置了扫描 mapper1 和 mapper2 路径， 之后在这两个路径下放 XxxMapper.java 和 XxxMapper.xml ，所有操作会自动对应着不同的数据源。
- 通过 sqlSessionFactoryRef 和 sqlSessionTemplateRef 分别指定不同 Bean 的引用名字。

### 第五步：创建对应的 Model 与 Mapper 

这部分就直接看源码，这里就不赘述了

### 测试

```java
@SpringBootTest
public class MybatisTest {

    @Autowired
    StudentMapper studentMapper;

    @Autowired
    TeacherMapper teacherMapper;

    @Test
    public void userSave() {
        Student studentDO = new Student();
        studentDO.setName("Mybatis");
        studentDO.setSex(1);
        studentDO.setGrade("一年级");
        studentMapper.save(studentDO);

        Teacher teacherDO = new Teacher();
        teacherDO.setName("Mybatis");
        teacherDO.setSex(2);
        teacherDO.setOffice("语文");
        teacherMapper.save(teacherDO);
    }
}
```
