#FROM ubuntu
#
#MAINTAINER shinee<shinee930@aliyun.com>
#ENV WORKPATH /app/logs
#WORKDIR $WORKPATH
#
#ADD ./jdk-8u401-linux-x64.tar.gz /usr/local/jdk
#ENV JAVA_HOME /usr/local/jdk/jdk1.8.0_401
#ENV PATH=$JAVA_HOME/bin:$PATH
#ENV CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
#ENV LANG C.UTF-8
#ENV LC_ALL C.UTF-8
#
#EXPOSE 8091
##docker build -t zscenv:1.0 .
##docker run -d -v /home/admin/application/logs:/app/logs -v /home/admin/application:/app -p 8091:8091 --privileged=true -e TZ=Asia/Shanghai zscenv:1.0
#ENTRYPOINT ["java", "-jar", "-Xms4096m -Xmx4096m", "/app/erp.jar"]
#CMD ["--spring.profiles.active=prod"]

## AdoptOpenJDK 停止发布 OpenJDK 二进制，而 Eclipse Temurin 是它的延伸，提供更好的稳定性
## 感谢复旦核博士的建议！灰子哥，牛皮！
FROM eclipse-temurin:21-jre

## 创建目录，并使用它作为工作目录

RUN mkdir -p /app
WORKDIR /app
## 将后端项目的 Jar 文件，复制到镜像中
COPY ./target/app.jar app.jar

## 设置 TZ 时区
## 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms256m -Xmx512m"

## 暴露后端项目的 8080 端口
EXPOSE 8080

## 启动后端项目
CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar app.jar
