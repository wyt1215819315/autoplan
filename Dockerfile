FROM amazoncorretto:17

# 配置时区
ENV TZ=Asia/Shanghai

# 设定工作目录
WORKDIR /app

# 假如采用sqlite数据库，需要将sqlite文件映射出去，否则容器销毁之后数据将全都丢失
VOLUME /app/db

# 添加app本体和配置文件
ADD ./target/auto_plan.jar /app/auto_plan.jar
ADD ./docs/application-example.yml /app/application-docker.yml

# 安装 Nginx
RUN yum install -y https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm
RUN yum install -y nginx

# 复制vue前端
COPY autoplan-front/dist /app/html

# 复制nginx配置文件覆盖默认nginx配置
COPY docs/nginx.conf /etc/nginx/nginx.conf

# 对外暴露80端口
EXPOSE 80

# 启动 Nginx 和 Spring Boot 项目
ENTRYPOINT nginx && java -jar auto_plan.jar --spring.profiles.active=docker