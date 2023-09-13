## 说明

SpringBoot 整合SpringSecurity+JWT+Redis+Mybatis Plus。 这个项目是基于前面一个项目 `boot-rbac-security` 来的，这里就更加完善，使用 Redis 存放登录的 token，以及登录的时候刷新 token 的有效期。

通过这个项目，可以更加灵活的知道如何搭建整合和搭建 `SpringSecurity+JWT+Redis`。


## 验证流程

```
1、在LoginService的login方法中，构造一个 UsernamePasswordAuthenticationToken ，包含用户名和密码，这是开始认证的入口。

2、LoginService调用AuthenticationManager的authenticate方法启动认证流程。

3、AuthenticationManager会找到一个匹配的AuthenticationProvider来进行认证。

4、AuthenticationProvider会调用UserDetailsService的loadUserByUsername方法加载用户信息。这里我们通过UserDetailsServiceImpl来查询用户。

5、在UserDetailsServiceImpl中，根据用户名查询用户信息，然后调用PasswordService进行密码验证。

6、PasswordService通过AuthenticationContextHolder获取登录的用户名和密码。然后与数据库中存储的用户密码(经过编码)进行匹配，如果匹配上就验证成功。

7、PasswordService验证成功后，UserDetailsServiceImpl将根据用户信息构造一个UserDetails对象(这里是LoginUser)，包含了用户名，密码，权限信息等。

8、UserDetailsServiceImpl将UserDetails返回给AuthenticationProvider。

9、AuthenticationProvider收到UserDetails后，完成验证，并生成一个已认证的Authentication对象。

10、AuthenticationProvider将Authentication返回给AuthenticationManager。

11、AuthenticationManager设置该Authentication到SecurityContextHolder中，供后续访问控制使用。

12、LoginService拿到已认证的Authentication，从中取出UserDetails，生成JWTtoken并返回。

综上，结合项目的逻辑SpringSecurity的认证流程大体可以分为：获取用户信息->用户验证->构建UserDetails->生成Authentication。我们通过自定义UserDetailsService和PasswordService来实现了用户验证逻辑。

```

## 项目搭建

这里特别说明下，需要执行 `sql/init.sql`, 作为程序的初始化使用。源码搭建就多赘述了。


## 测试

在这个项目上面没有自定义页面来测试，我是通过 Post Man 测试的。下面说下测试相关内容。

### 异常的登录请求

**请求示例**

```http request
POST /login HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Content-Length: 40

{"username":"admin", "password":"admin"}
```

**返回结果**

```json
{
    "msg": "Bad credentials",
    "code": 500
}
```

### 正常的登录请求

**请求示例**

```http request
POST /login HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Content-Length: 41

{"username":"admin", "password":"123456"}
```

**返回结果**

```json
{
    "msg": "操作成功",
    "code": 200,
    "data": {
        "token": "eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6ImM5ZDlmZTgwLWE3NGUtNDcxNy05MDQ3LTAxZmRhYWYwNjgxMyJ9.4m54m7fvL5ZO2Hj3ZZrVBeAoT7wAZyttkc6-9UNTm01lvit9jPLVwKbnDMXvIFaBMHEKX4Z2YxpQW-AwY9OJsA",
        "username": "admin"
    }
}
```

### 请求访问接口

需要将返回值中的 token 放入到 head 中去

#### 错误的请求

**请求示例**
```http request
POST /user/all HTTP/1.1
Host: localhost:8080
Authorization: eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjU0OWEyMDE5LTZiMTQtNGNhYi1iOGMwLTBlODEzNzJjNmY4MyJ9.f2Ybhd7fisNjwjtW88MqpzGia5tkyoVu_OQULzvUV2qkmw7UCz29ttQZhzRTMnPKKunbXGaPncK0zCHaIDgbqw
Content-Type: application/json
Content-Length: 41

{"username":"admin", "password":"123456"}
```

**返回结果**

```json
{
    "msg": "Request method 'POST' not supported",
    "code": 500
}
```


#### 正确的请求

```http request
GET /user/all HTTP/1.1
Host: localhost:8080
Authorization: eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjU0OWEyMDE5LTZiMTQtNGNhYi1iOGMwLTBlODEzNzJjNmY4MyJ9.f2Ybhd7fisNjwjtW88MqpzGia5tkyoVu_OQULzvUV2qkmw7UCz29ttQZhzRTMnPKKunbXGaPncK0zCHaIDgbqw
Content-Type: application/json
Content-Length: 41

{"username":"admin", "password":"123456"}
```

**返回结果**

```json
{
    "msg": "操作成功",
    "code": 200,
    "data": [
        {
            "userId": 6,
            "userName": "admin",
            "nickName": "admin",
            "userType": "00",
            "password": "$2a$10$ZglYem2Zs8E4ETbLwaiA4OjXaTZX9w8wJ7x8LZdpGisdtI9VlIfvO",
            "delFlag": "0",
            "createBy": "",
            "createTime": null,
            "updateBy": "",
            "updateTime": null
        },
        {
            "userId": 7,
            "userName": "user",
            "nickName": "user",
            "userType": "00",
            "password": "$2a$10$ZglYem2Zs8E4ETbLwaiA4OjXaTZX9w8wJ7x8LZdpGisdtI9VlIfvO",
            "delFlag": "0",
            "createBy": "",
            "createTime": null,
            "updateBy": "",
            "updateTime": null
        }
    ]
}
```

剩下的可以测试下使用 user 用户访问 `/admin` 接口，这个是跨权限访问，是访问不了的。
