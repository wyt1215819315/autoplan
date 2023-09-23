#!bin/bash

# 输入数据库的密码
MYSQL_PASSWORD = ""

docker network create autoplan_net

docker run --name autoplan-mysql \
-p 3306:3306 \
-e MYSQL_ROOT_PASSWORD=$MYSQL_PASSWORD \
-v ./auto_plan_docker.sql:/docker-entrypoint-initdb.d/auto_plan_docker.sql \
--network autoplan_net --network-alias mysql \
--restart=always \
-d mysql:8.0.19

# 等待 MySQL 容器启动完毕
echo "等待 MySQL 容器启动..."
while ! docker exec autoplan-mysql mysqladmin ping -u root --password=$MYSQL_PASSWORD --host=mysql --silent; do
    sleep 1
done

docker run --name autoplan \
-p 26666:26666 \
-e TZ="Asia/Shanghai" \
-v ./application.yml:/tmp/yml/application.yml \
--network=autoplan_net --network-alias=autoplan \
-d muxia0326/autoplan
