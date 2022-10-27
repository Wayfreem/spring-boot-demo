## 说明

这里介绍下关于 `@NamedQuery` 相关的知识点

`@NamedQuery` 与 `@NamedNativeQuery` 都是用来定义查询的一种形式。
- @NamedQuery： 使用的是 JPA语法的 SQL
- @NamedNativeQuery：使用的是原生SQL


### 示例

```java
// 定义单个 @NamedQuery 查询
@NamedQuery(name="findAllEmployee", query="SELECT c FROM Employee c")
// 或者  
@NamedQuery(name = "Employee.findAllEmployee", query = "select e from Employee e")

// 定义多个 @NamedQueries 查询
@NamedQueries({
@NamedQuery(name="findAllEmployee", query="SELECT c FROM Employee"),
@NamedQuery(name="findEmployeeWithId", query="SELECT c FROM Employee c WHERE c.id=?1")
@NamedQuery(name="findEmployeeWithName", query="SELECT c FROM Employee c WHERE c.name = :name")
})

// 定义单个 @NamedNativeQuery 查询
@NamedNativeQuery(name = "findAllEmployee", query = "select * from Employee c")

// 定义多个 @NamedNativeQueries 查询
@NamedNativeQueries({
@NamedNativeQuery(name = "findAllEmployee", query = "select * from Employee c")
})
```
- name：指定命名查询的名称
- query：指定命名查询的语句

## 使用方法

下面使用了 lombok 插件

### 第一种使用方法

**第一步**

在实体 bean 中声明出 `@NamedQuery` 与 `@NamedNativeQuery`。
```java
@Entity
@Table(name = "Employee", schema = "HR")
@NamedQueries({
        @NamedQuery(name = "Employee.findAllEmployee", query = "select e from Employee e")
})
@Data
public class Employee {

    @Id
    private String id;
    private String name;
    private String email;
    private String lastname;
}

```
对于 @NamedQuery 说明

    @NamedQuery(name = "Employee.findAllEmployee", query = "select e from Employee e")
    
    这里的 Name 使用到了 "Employee", 这里是表明是限定在 Employee 下面

**第二步**

需要在对应的 Repository 中增加 @NamedQuery 中 name 中定义的方法名

```java
public interface EmployeeRepository extends JpaRepository<Employee, String> { 
    List<Employee> findAllEmployee();
}
```

**测试调用**

这里省略掉了通过 controller 调用的部分
```java
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> findAllNamed(){
        return employeeRepository.findAllEmployee();
    }
}
```


### 第二种使用方法

**第一步**

在实体 bean 中声明出 `@NamedQuery` 与 `@NamedNativeQuery`。

```java
@Entity
@Table(name = "Employee", schema = "HR")
@NamedQueries({
@NamedQuery(name = "findAllEmployee", query = "select e from Employee e")
})
@Data
public class Employee {

    @Id
    private String id;
    private String name;
    private String email;
    private String lastname;
}
```

**第二步**

使用 EntityManager 中的 createNamedQuery 方法传入命名查询的名称创建查询就好

```java
@Service
public class EmployeeService {

    @Autowired
    private EntityManager entityManager;
    public List<Employee> findAllNamed(){
       return (List<Employee>) entityManager.createNamedQuery("findNamedEmployee").getResultList();
    }
}
```

第三步
测试调用
这里省略掉了通过 controller 调用的部分

