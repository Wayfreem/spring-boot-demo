
## 简介
使用 spring boot 集成 mybatis 实现动态数据源操作。*项目未完成*

既然是需要连接数据库，就需要安装 MySQL(采用docker 安装) [安装参考链接](https://blog.csdn.net/qq_18948359/article/details/125486934?spm=1001.2014.3001.5502)

参考连接
- https://blog.csdn.net/Koikoi12/article/details/125514439
- https://blog.csdn.net/Koikoi12/article/details/125514439?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-1-125514439-blog-105329863.pc_relevant_3mothn_strategy_and_data_recovery&spm=1001.2101.3001.4242.2&utm_relevant_index=4
## 集成的步骤

我们需要配置多个数据源，然后使用 注解的方式实现动态切换
