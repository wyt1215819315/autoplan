# AutoPlan_Helper
这是一个自动化的托管系统，目前支持网易云签到刷歌，bilibili，米游社原神签到

目前项目属于测试阶段，可能会有些莫名其妙的bug，敬请谅解

如果觉得好用，点个**star**吧

### 开源地址
[wyt1215819315 / autoplan](https://github.com/wyt1215819315/autoplan)

### 目前已经实现
1. b站每日自动经验任务
1. b站赛事预测赚硬币任务
2. 网易云自动签到刷歌任务
3. 米游社原神签到领奖励任务以及米游币任务

### 测试服务器地址
<a href="https://auto.oldwu.top/" target="_blank">点击打开</a>

本人不会利用任何cookie，但是为了安全考虑，建议还是自己搭建运行环境

### 项目结构
采用Springboot + Thymeleaf + layui制作

数据库：mysql

登录安全验证及权限管理：spring-security

定时任务核心：quartz（从pearadmin中抠过来的）

### 使用说明
#### bilibili
支持b站签到任务以及赛事预测任务
已实现扫码登录

cookie登录请参考<a href="https://github.com/JunzhouLiu/BILIBILI-HELPER-PRE">BILIBILI-HELPER-PRE</a>以获取cookie值
#### 网易云
都是字面意思

#### 米游社
只支持原神签到任务和米游币任务

[更多使用说明请查看](https://blog.oldwu.top/index.php/archives/84/#toc_5)

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
4. 使用maven打包，并使用`java -jar xxx.jar`运行
5. 注册账号，并将其定为管理员账户，步骤：
   1. 查看`sys_user`表中你的账号对应的`id`
    2. 进入`sys_role_user`表中找到对应的`user_id`
    3. 将对应行的`sys_role_id`值改为1
6. 一些定时任务的配置请登录管理员账号在`自动任务管理`中查看


### 一些问题
1. 代码不是一般的乱，（非常非常乱....而且很多地方不符合规范），本人萌新一枚，请大佬多多指教
3. 由于BILIBILI-HELPER-PRE项目大多采用static变量，因此无法多线程运行，也无法手动执行

### 未来
2. 统一推送，即不用填写多个，仅需在个人中心绑定一个即可
4. 手动执行b站任务

### 更新日志
* 21.8.29 更新了b站二维码登录以及任务删除功能
* 21.8.30 增加了网易云推送，改变了日志表结构
* 21.8.31 增加了米游社原神签到，修复了网易云刷歌不计数的问题（摔、垃圾网易云json数组外边还要加引号）
* 21.9.2 增加了编辑参数编辑功能
* 21.9.3 增加了网易云任务和米游社任务的手动执行开关
* 21.9.4 增加了米游社cookie字段，使其能够执行米游币任务
* 21.9.5 修复了非管理员无法使用单次执行任务的问题，修复了米游社任务无权限访问的问题，修复了bilibili直播送礼物报错的问题
* 21.9.6 增加了bilibili赛事预测,修复了网易云uid超出int范围导致任务中断的问题

### 鸣谢
1. <a href="https://github.com/JunzhouLiu/BILIBILI-HELPER-PRE">BILIBILI-HELPER-PRE</a>
1. <a href="https://github.com/JunzhouLiu/bilibili-match-prediction">bilibili-match-prediction</a>
2. <a href="https://github.com/secriy/CloudMusic-LevelUp">CloudMusic-LevelUp</a>
3. <a href="https://github.com/PonKing66/genshi-helper">genshi-helper</a>
4. <a href="https://github.com/y1ndan/genshinhelper">genshinhelper</a>

