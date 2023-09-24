FROM openjdk:8-jdk-alpine

# 设置阿里云镜像源
RUN echo "http://mirrors.aliyun.com/alpine/v3.14/main" > /etc/apk/repositories && \
    echo "http://mirrors.aliyun.com/alpine/v3.14/community" >> /etc/apk/repositories

# 安装字体配置工具和其他必要工具
RUN apk update && apk --no-cache add fontconfig ttf-dejavu

# 复制字体文件
COPY ./docker-run/times.ttf /usr/share/fonts/

VOLUME /tmp
ADD ./target/*.jar /auto_plan.jar
EXPOSE 26666

ENTRYPOINT ["java","-jar","auto_plan.jar","--spring.config.location=/tmp/yml/application.yml"]