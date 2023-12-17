# 基本配置
docker run --name autoplan \
-p 需要映射的端口:80 \
-e APP_DB_TYPE=数据库类型  \
-e APP_DB_URL=数据库地址  \
-e APP_DB_USER=数据库用户  \
-e APP_DB_PWD=数据库密码  \
-v 数据库挂载位置:/app/db \
-d wyt1215819315/auto_plan

# sqlite
docker run --name autoplan \
-p 需要映射的端口:80 \
-v 数据库挂载位置:/app/db \
-d wyt1215819315/auto_plan

# mysql
docker run --name autoplan \
-p 需要映射的端口:80 \
-e APP_DB_TYPE=mysql  \
-e APP_DB_URL=127.0.0.1:3306/auto_plan  \
-e APP_DB_USER=root  \
-e APP_DB_PWD=123456  \
-d wyt1215819315/auto_plan