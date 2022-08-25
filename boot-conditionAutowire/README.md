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
