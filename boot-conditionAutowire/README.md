## 说明

`@conditional` 注解是在 Spring4 中新增的用于条件判断注入。

这里是使用 `@conditional` 注解，按照一定的条件进行判断，给容器注入 bean。

### 常用的条件注解

| 条件注解                           |     对应的 Condition 处理类     | 处理逻辑                                                                                                                                                                                         |
|:-------------------------------|:-------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| @ConditionalOnBean             |      OnBeanCondition      | Spring容器中是否存在对应的实例。可以通过实例的类型、类名、注解、昵称去容器中查找(可以配置从当前容器中查找或者父容器中查找或者两者一起查找)                                                                                                                    |
| @ConditionalOnClass            |     OnClassCondition      | 类加载器中是否存在对应的类。可以通过Class指定(value属性)或者Class的全名指定(name属性)如果是多个类或者多个类名的话，关系是”与”关系，也就是说这些类或者类名都必须同时在类加载器中存在                                                                                       |
| @ConditionalOnExpression       |   OnExpressionCondition   | 判断SpEL 表达式是否成立                                                                                                                                                                               |
| @ConditionalOnMissingBean      |      OnBeanCondition      | Spring容器中是否缺少对应的实例。可以通过实例的类型、类名、注解、昵称去容器中查找(可以配置从当前容器中查找或者父容器中查找或者两者一起查找)                                                                                                                    |
| @ConditionalOnMissingClass	    |     OnClassCondition      | 跟ConditionalOnClass的处理逻辑一样，只是条件相反，在类加载器中不存在对应的类                                                                                                                                              |
| @ConditionalOnProperty	        |    OnPropertyCondition    | 应用环境中的属性是否存在。提供prefix、name、havingValue 以及 matchIfMissing 属性。prefix表示属性名的前缀，name是属性名，havingValue是具体的属性值，matchIfMissing是个boolean值，如果属性不存在，这个matchIfMissing为true的话，会继续验证下去，否则属性不存在的话直接就相当于匹配不成功 |
| @ConditionalOnResource         |    OnResourceCondition    | 是否存在指定的资源文件。只有一个属性resources，是个String数组。会从类加载器中去查询对应的资源文件是否存在                                                                                                                                 |
| @ConditionalOnSingleCandidate	 |      OnBeanCondition      | Spring容器中是否存在且只存在一个对应的实例。只有3个属性value、type、search。跟ConditionalOnBean中的这3种属性值意义一样                                                                                                              |
| @ConditionalOnWebApplication	  | OnWebApplicationCondition | 应用程序是否是Web程序，没有提供属性，只是一个标识。会从判断Web程序特有的类是否存在，环境是否是Servlet环境，容器是否是Web容器等                                                                                                                      |

### 举例
| 例子                                                                                                        | 说明                                                                                     |
|:----------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------|
| @ConditionalOnBean(javax.sql.DataSource.class)	                                                           | Spring容器或者所有父容器中需要存在至少一个javax.sql.DataSource类的实例                                       |
| @ConditionalOnClass({ Configuration.class,FreeMarkerConfigurationFactory.class })	                        | 类加载器中必须存在Configuration和FreeMarkerConfigurationFactory这两个类                              |
| @ConditionalOnExpression(“’${server.host}’==’localhost’”)	                                                | server.host配置项的值需要是localhost                                                           |
| ConditionalOnJava(JavaVersion.EIGHT)	                                                                     | Java版本至少是8                                                                             |
| @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)	                | Spring当前容器中不存在ErrorController类型的bean                                                   |
| @ConditionalOnMissingClass(“GenericObjectPool”)	                                                          | 类加载器中不能存在GenericObjectPool这个类                                                          |
| @ConditionalOnNotWebApplication	                                                                          | 必须在非Web应用下才会生效                                                                         |
| @ConditionalOnProperty(prefix = “spring.aop”, name = “auto”, havingValue = “true”, matchIfMissing = true) | 应用程序的环境中必须有spring.aop.auto这项配置，且它的值是true或者环境中不存在spring.aop.auto配置(matchIfMissing为true) |
| @ConditionalOnResource(resources=”mybatis.xml”)                                                           | 类加载路径中必须存在mybatis.xml文件                                                                |
| @ConditionalOnSingleCandidate(PlatformTransactionManager.class)                                           | Spring当前或父容器中必须存在PlatformTransactionManager这个类型的实例，且只有一个实例                             |
| @ConditionalOnWebApplication	                                                                             | 必须在Web应用下才会生效                                                                          |


### 相关知识点

**@Conditional 的定义**

`@Conditional` 可以标注在类和方法上

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME) 
@Documented
public @interface Conditional {
    Class<? extends Condition>[] value();   // 这里是一个 Condition 数组
}
```

**Condition 接口**

`Condition` 接口，对应的实现类，需要实现 matches 方法

```java
public interface Condition {
    boolean matches(ConditionContext var1, AnnotatedTypeMetadata var2);
}
```


### 自定义注解

使用条件注入需要两部分操作
- 创建一个实现 Condition 接口的实现类 XxxCondition
- 自定义一个 Condition 注解，在自定义的注解上面标注 `@Conditional(XxxCondition.class)`


#### 第一步：新增自定义注解


```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(MyOnPropertyCondition.class)   // 导入具体的判断类
public @interface MyConditionalOnProperty{
    // 这里定义几个注解的信息
    String value();

    String name();
}
```

#### 第二步：Condition 实现类

创建 MyOnPropertyCondition 类
```java
public class MyOnPropertyCondition implements Condition {

    /**
     * 匹配出对应的 bean
     * @param context 可以获取 spring 中一些基本的信息
     * @param metadata 可以获取到 注解上面对应的信息
     * @return boolean
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        /** 这块这里没有用到，但是可以通过 context 获取到相关的额外信息，这里补充记录下 **/
        // 获取到 ioc 容器中的 beanFactory
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        //获取类加载器
        ClassLoader classLoader = context.getClassLoader();
        //获取当前环境信息
        Environment environment = context.getEnvironment();
        //获取bean定义的注册类
        BeanDefinitionRegistry registry = context.getRegistry();
        /****/

        // 通过 metadata 来获取值
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(MyConditionalOnProperty.class.getName());
        String propertyName = (String) annotationAttributes.get("name");
        String value = (String) annotationAttributes.get("value");

        if ("test".equals(propertyName) && "123".equals(value)) {
            return true;
        }

        return false;
    }
}
```

#### 第三步：启动类

```java
public class ConditionAutowireApplication {

    @Bean
    @MyConditionalOnProperty(value = "123", name = "test")
    public String conditionHello(){
        return "condition 装配成功。";
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext content = new SpringApplicationBuilder(ConditionAutowireApplication.class)
                .web(WebApplicationType.NONE).run(args);

        String hellWorld  =content.getBean("conditionHello", String.class);
        System.out.println("hello world" + hellWorld);
        content.close();
    }
}
```

#### 程序启动测试

程序启动之后，就可以看到控制台有对应的输出了

```java
hello worldcondition 装配成功。
```

#### 额外说明

上面是在启动类这里是增加了这块

```java
@Bean
@MyConditionalOnProperty(value = "123", name = "test")
public String conditionHello(){
    return "condition 装配成功。";
}
```

如果是单独新增一类，类中的方法使用上述的源码，就需要指定包扫描的路径，我们再看看如何修改

首先，我们在启动类中移除上面的代码

```java
@ComponentScan("com.demo")
public class ConditionAutowireApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext content = new SpringApplicationBuilder(ConditionAutowireApplication.class)
                .web(WebApplicationType.NONE).run(args);

        String hellWorld  =content.getBean("conditionHello", String.class);
        System.out.println("hello world" + hellWorld);
        content.close();
    }
}
```

其次，我们增加一个 bean 

```java
@Component
public class ConditionTypeBean {

    @Bean
    @MyConditionalOnProperty(value = "123", name = "test")
    public String conditionHello(){
        return "condition 装配成功。";
    }
}
```

然后重新启动就好，源码里面是已经修改完了的。