[参考连接](https://www.cnblogs.com/shanheyongmu/p/15710953.html)

## 简介

这个项目是自定义 404 错误界面。

## 说明

在 resources/ 目录下面新建目录 static/error/

```
resources
└── static
    └──  error
        └── 404.html
```


#### 404.html
页面的具体内容
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404</title>
</head>
<body>
发生了404错误。
</body>
</html>
```

## 原理部分
当没有配置错误页面显示时，会出现一个默认的报错页面，这里的逻辑是在 `BasicErrorController` 类中
