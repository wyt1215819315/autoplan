docker network create autoplan_net

docker run --name autoplan-mysql \
-p 3306:3306 \
-e MYSQL_ROOT_PASSWORD="密码" \
-v ./auto_plan_docker.sql:/docker-entrypoint-initdb.d/auto_plan_docker.sql \
--network autoplan_net --network-alias mysql \
--restart=always \
-d mysql:8.0.19

sleep 5

docker run --name autoplan \
-p 26666:26666 \
-e TZ="Asia/Shanghai" \
-v ./application.yml:/tmp/yml/application.yml \
--network=autoplan_net --network-alias=autoplan \
-d muxia0326/autoplan
