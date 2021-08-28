# AutoPlan_Helper
这是一个自动化的托管系统，目前支持网易云，bilibili

### 开源地址
[wyt1215819315 / autoplan](https://github.com/wyt1215819315/autoplan)

### 目前已经实现
1. b站每日自动经验任务
2. 网易云自动签到刷歌任务

### 测试服务器地址
<a href="https://auto.oldwu.top/" target="_blank">点击打开</a>

### 项目结构
采用Springboot + Thymeleaf + layui制作

数据库：mysql

登录安全验证及权限管理：spring-security

定时任务核心：quartz（从pearadmin中抠过来的）

### 使用说明
#### bilibili
请参考<a href="https://github.com/JunzhouLiu/BILIBILI-HELPER-PRE">BILIBILI-HELPER-PRE</a>以获取cookie值
#### 网易云
都是字面意思

### 项目部署
1. 导入idea并下载依赖
2. 在mysql中创建数据库并导入sql
3. 在`resources`中添加`application.yml`配置文件，内容如下
```yaml
server:
  #服务器端口
  port: 26666
spring:
  #数据库连接配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://数据库地址:3306/数据库名称?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: 数据库用户名
    password: 数据库密码
  main:
    allow-bean-definition-overriding: true
  mvc: #静态文件
    static-path-pattern: /static/**
mybatis:
  #mapper配置文件
  mapper-locations: classpath:mapper/*.xml,classpath:mapper/**/*.xml
  type-aliases-package: com.oldwu.entity
  #开启驼峰命名
  configuration:
    map-underscore-to-camel-case: true
#    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```
4. 使用maven打包
5. 注册账号，并将其定为管理员账户，步骤：
   1. 查看`sys_user`表中你的账号对应的`id`
    2. 进入`sys_role_user`表中找到对应的`user_id`
    3. 将对应行的`sys_role_id`值改为1
6. 一些定时任务的配置请登录管理员账号在`自动任务管理`中查看


### 一些问题
1. 网易云任务会出现莫名其妙的json解析失败导致一系列莫名其妙的错误，目前采用重试来解决，无法定位错误根源
2. 代码不是一般的乱，（非常非常乱....），本人萌新一枚，请大佬多多指教
3. 由于BILIBILI-HELPER-PRE项目大多采用static变量，因此无法多线程运行
4. 网易云项目暂不支持push推送，仅支持任务面板查看运行状态以及日志
5. b站项目不支持tele推送

### 未来
1. b站扫码登录

### 鸣谢
1. <a href="https://github.com/JunzhouLiu/BILIBILI-HELPER-PRE">BILIBILI-HELPER-PRE</a>
2. <a href="https://github.com/secriy/CloudMusic-LevelUp">CloudMusic-LevelUp</a>

