## 说明

由于采用 `application/json` 传输参数时，HttpServletRequest只能读取一次 body 中的内容。因为是读的字节流，读完就没了，因此需要需要做特殊处理。下面介绍下如何重复读取~~

## 具体实现步骤

### 第一步：移入基础依赖

pom.xml

```xml

```


### 第二步：增加过滤器，转换 request 为自定义的 request

```java

```


### 第三步：注册 Filter

```java

```
