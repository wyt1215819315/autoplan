docker network create autoplan_net

docker run --name autoplan-mysql \
-p 3306:3306
-e MYSQL_ROOT_PASSWORD="密码" \
-v ./auto_plan.sql:/docker-entrypoint-initdb.d/ \
--network autoplan_net --network-alias mysql \
--restart=always \
-d mysql:8.0.19

docker run --name autoplan \
-p 26666:26666
-e TZ="Asia/Shanghai"
-v ./application.yml:/tmp/yml/
--network=autoplan_net --network-alias=autoplan
-d muxia0326/autoplan
