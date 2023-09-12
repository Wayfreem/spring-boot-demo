## 说明

集成 `Spring Security` 项目。这里需要使用到数据，所以需要先初始化SQL，SQL 的内容具体看下 `sql/init.sql` 中的内容。

数据库搭建的方式就看下这个：[docker 中安装 MySQL 以及使用](https://blog.csdn.net/qq_18948359/article/details/125486934?spm=1001.2014.3001.5501)


## 重点内容扫盲
对于接下来需要做的深入学习之前，我们先对两块的知识点扫盲一下

### PasswordEncoder 接口

关于 `PasswordEncoder` 接口，`PasswordEncoder` 主要负责的就是密码和 主题信息业务类返回的密码进行比对的时候，所要使用的加密方式。
```java
// 表示把参数按照特定的解析规则进行解析
String encode(CharSequence rawPassword);

// 表示验证从存储中获取的编码密码与编码后提交的原始密码是否匹配。如果密码匹配，则返回 true；如果不匹配，则返回 false。第一个参数表示需要被解析的密码。第二个参数表示存储的密码。
boolean matches(CharSequence rawPassword, String encodedPassword);

// 表示如果解析的密码能够再次进行解析且达到更安全的结果则返回 true，否则返回false。默认返回 false。
default boolean upgradeEncoding(String encodedPassword){
	return false; 
}
```
接着，我们看下对应的接口实现类
![PasswordEncoder 实现类](https://img-blog.csdnimg.cn/ae5a2e12c48b4d7ab770710b3562457f.png)
我们这里就主要介绍下 `BCryptPasswordEncoder` 密码解析器，这个也是官方推荐使用的，

`BCryptPasswordEncoder` 是 Spring Security 框架提供的一种密码加密方式。它使用 bcrypt 算法对密码进行加密，该算法是一种非常安全可靠的密码加密算法。

使用 BCryptPasswordEncoder 加密用户的密码时，首先会生成一个随机“盐”（salt），并将盐值和原始密码一同进行加密。因为每个用户的盐值都是随机生成的，`即使两个用户的密码相同，加密后的结果也是不同的`，这样大大增加了密码破解的难度。

> 举一个实际的例子：密码是 `123456` 加密后成了 a 存到了数据库，这时候登录前端传的还是 `123456` 密码，然后进行加密，加密后的密文会发现根本不是a，是b，但是a和b两个密文通过加密算法提供的对比方法，在对比的时候是相等的。

**具体实例**

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // 对密码进行加密
        String pwd = bCryptPasswordEncoder.encode("123456");
        // 输出加密之后的字符串
        System.out.println("加密之后数据：\t"+pwd);

        // 使用 bCryptPasswordEncoder 的匹对方法
        boolean result = bCryptPasswordEncoder.matches("123456", pwd);
        // 打印比较结果
        System.out.println("比较结果：\t"+result);
    }
}
```
控制台输出
```console
加密之后数据：	$2a$10$n.U/yTVF8c9mjMsPUv0fruekmbvfxAhZhcq0ymOWa/qMwr3P7LxQa
比较结果：	true
```

### UserDetailsService 接口
在上面介绍密码是如何生成的时候，有讲到 `UserDetailsServiceAutoConfiguration` 类，在上面的注解上面就有出现过他的身影。通过这里的 `@ConditionalOnMissingBean` 可以看出来，当我们没有自己的登录逻辑时（就像上面的入门示例一样），就会默认的走到这个地方来。
![UserDetailsServiceAutoConfiguration](https://img-blog.csdnimg.cn/223a77e8b383424da4eb17a301cb50eb.png)
对于这个接口呢，我感觉是需要重点需要了解的，这个是涉及到我们登录的时候校验用的。`也就是说 Spring Security 就是通过这个来校验登录用户信息的。` 我们具体应该怎么写代码呢？这由于他是一个接口，我们实现这个接口，然后写入我们自己的逻辑就好，对应的源码如下：
```java
package org.springframework.security.core.userdetails;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```
对于这个`loadUserByUsername` 具体的操作有点儿奇怪，我们这个先看整个验证流程，==这里需要特别说明下：==
>第一步：我们根据 username 查询数据库对应的用户是否存在。
第二步：将数据库中查询的用户信息（账号+密码）封装到 UserDetail 对象中，作为方法的返回值。
第三步，将第二步中数据库中的密码与前端出入的明文密码加密之后对比，验证身份。

`UserDetailsService 中`的 `loadUserByUsername` 就做的事情是第二步。我们通常的情况下，是直接判断用户名和密码，看看是否能登录成功，这里和我们自己写的登录逻辑并不一样。

用户示例
```java
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws
            UsernameNotFoundException {
        // 1.根据username查询数据库，判断用户名是否存在
       
        // 2.将数据库当中查出来的username和pwd封装到user对象当中返回 第三个参数表示权限
        return new User(username, pwd,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin,"));
    }
}
```

**UserDetail**
在上面出现了  `UserDetail` 以及一个 `User`类这里就再说明下他们是啥，`UserDetail` 是一个接口，具体的源码如下：
```java
public interface UserDetails extends Serializable {
	// 表示获取登录用户所有权限
    Collection<? extends GrantedAuthority> getAuthorities();

	// 表示获取密码
    String getPassword();

	// 表示获取用户名
    String getUsername();

	// 表示判断账户是否过期
    boolean isAccountNonExpired();

	// 表示判断账户是否被锁定
    boolean isAccountNonLocked();

	// 表示凭证{密码}是否过期
    boolean isCredentialsNonExpired();

	// 表示当前用户是否可用
    boolean isEnabled();
}
```

在Spring Security 中有一个 `User` (不是上面文章中 SecurityProperties 的内部类 User ) 作为 `UserDetails` 实现，在项目上面是新建一个类实现这个接口或者直接使用这个 `User` 都是可以的：
![User类](https://img-blog.csdnimg.cn/8fe12bb66a9c4d6f89e34d49515cc97a.png)

具体的验证调用验证的逻辑如下，这里是先将一部分源码贴出来给大伙看下，知道是怎么调用的，后面会通过示例讲到
![调用验证](https://img-blog.csdnimg.cn/3c94027e0bce41ba85cc726e5b34c85d.png)

