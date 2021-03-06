## 工程说明

这里是为了用来记录 Spring Boot 学习的相关源码项目，每个子模块都是一个独立的项目。

由于之前学习的特别零散，这里就做一个聚合，后期慢慢的会将其他 demo 集成起来，并且都会给出博客的地址。

每个 demo 都有独立的 `README`, 在搭建的时候可以去看看。

## 开发环境

- JDK 17
- Maven 3.5 
- Intellij IDEA
- MySQL(采用docker 安装) [安装参考链接](https://blog.csdn.net/qq_18948359/article/details/125486934?spm=1001.2014.3001.5502)

## 具体模块说明

这里先大致列出来之前有零碎学过的，后面慢慢的补上，对应的模块集成会同步发送博客到 CSDN 上

| 模块                    | 说明                                      |
|:----------------------|:----------------------------------------|
| boot-taskSchedule     | 在 Spring boot 中使用 taskSchedule 来实现定时任务  |
| boot-customizeEvent   | 在 Spring Boot 使用自定义事件，对程序操作做异步解耦        |
| boot-redis            | spring boot 与 redis 的集成                 |
| boot-mongoDB          | spring boot 与 MongoDB 集成                |
| boot-orm-jpa          | spring boot 与 jpa 集成                    |
| boot-orm-mybatis      | spring boot 与 Mybatis 集成                |
| boot-orm-mybatis Plus | spring boot 与 Mybatis Plus 集成           |
| boot-email            | spring boot 集成 邮件，实现发送邮件                |
| boot-webSocket        | spring boot 集成 webSocket，实现双工通信         |
| boot-es               | spring boot 集成 elasticsearch，对搜索引擎的相关操作 |
| boot-ureport2         | spring boot 集成 ureport2，实现打印报表          |

