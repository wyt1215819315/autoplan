# AutoPlan_Helper
这是一个自动化的托管系统，目前支持网易云签到刷歌，bilibili赚经验+自动赛事预测，米游社原神签到，部署至服务器和你的小伙伴一起赚经验吧

2.0版本已更新，带来全新的界面体验，感谢@MuXia-0326大佬的辛勤付出

1.x - 2.0版本升级需要升级配置文件，以及新增一个定时任务(不一定要执行sql，可以直接去管理界面加)：
```mysql
INSERT INTO `t_sys_quartz_job` (`id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`) VALUES ('592295794938351617', '米游社更新个人信息', 'DEFAULT', 'mihuyouTask.updateAvatar()', '0 15 0 ? * MON', '3', '1', 0);
```

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
原作者开源项目已经停止维护，可以看看他的博客声明https://blog.misec.top/archives/bye-helper

支持b站签到任务以及赛事预测任务
已实现扫码登录

cookie登录请参考<a href="https://blog.oldwu.top/index.php/archives/84/#toc_6">这里</a>以获取cookie值
#### 网易云
都是字面意思

#### 米游社
只支持原神签到任务和米游币任务

[更多使用说明请查看](https://blog.oldwu.top/index.php/archives/84/#toc_5)

### 项目部署
1. 首先准备好`application.yml`配置文件，模板文件可以在项目根目录找到或Releases中附，或者可以直接复制以下内容：
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
# actable自动建表
actable:
   table:
      auto: update
   model:
      #分号或者逗号隔开
      pack: com.oldwu.entity;com.oldwu.domain;com.netmusic.model;com.miyoushe.model
   database:
      type: mysql
   index:
      #自己定义的索引前缀#该配置项不设置默认使用actable_idx_
      prefix: INDEX_
   unique:
      #自己定义的唯一约束前缀#该配置项不设置默认使用actable_uni_
      prefix: INDEX_UNIQUE_
   # mybatis自有的配置信息，key也可能是：mybatis.mapperLocations
mybatis-plus:
   #mapper配置文件
   mapper-locations: classpath:mapper/*.xml,classpath:mapper/**/*.xml,classpath*:com/gitee/sunchenbin/mybatis/actable/mapping/*/*.xml
   type-aliases-package: com.oldwu.entity
   #开启驼峰命名
   configuration:
      map-underscore-to-camel-case: true
      #输出mybatis日志
#      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```
2. 在mysql中创建数据库并导入sql
3. 接下来你可以选择两种方式部署：
<details>
<summary>使用 <a href="https://github.com/wyt1215819315/autoplan/releases">Releases</a> 中打包好的jar运行</summary>

1. 将`application.yml`修改正确并放入jar包同级目录中
2. 使用`java -jar xxx.jar`运行

</details>

<details>
<summary>自行编译</summary>

1. 导入idea并下载依赖（请使用JDK1.8）
2. 在`resources`文件夹放入`application.yml`配置文件（可选，你可以选择外置配置文件）
3. 使用maven install打包成jar
4. 使用`java -jar xxx.jar`运行

</details>


4. 注册账号，并将其定为管理员账户，步骤：
   1. 查看`sys_user`表中你的账号对应的`id`
   2. 进入`sys_role_user`表中找到对应的`user_id`
   3. 将对应行的`sys_role_id`值改为1
5. 一些定时任务的配置请登录管理员账号在`自动任务管理`中查看

**Releases中的jar不会经常更新，我已经设置的github自动构建，如果需要最新测试版，请前往 https://github.com/wyt1215819315/autoplan/actions 自行下载**

**版本更新时，请务必备份数据库，以免未知的后果造成影响**

如果你不需要自动建表，请将配置文件中的actable有关的项全都注释掉即可

### 一些问题
1. 代码不是一般的乱，（非常非常乱....而且很多地方不符合规范），本人萌新一枚，请大佬多多指教
3. 由于BILIBILI-HELPER-PRE项目大多采用static变量，因此无法多线程运行，也无法手动执行

### 未来
1. 管理员功能：查看日志，删除任务等
2. go-cqhttp推送（需要加机器人为好友）
3. 手动执行b站任务（咕咕咕）
5. 主页说明支持markdown格式动态编辑（开发中..）
7. 修改密码功能

### 更新日志
* 21.8.29 更新了b站二维码登录以及任务删除功能
* 21.8.30 增加了网易云推送，改变了日志表结构
* 21.8.31 增加了米游社原神签到，修复了网易云刷歌不计数的问题（摔、垃圾网易云json数组外边还要加引号）
* 21.9.2 增加了编辑参数编辑功能
* 21.9.3 增加了网易云任务和米游社任务的手动执行开关
* 21.9.4 增加了米游社cookie字段，使其能够执行米游币任务
* 21.9.5 修复了非管理员无法使用单次执行任务的问题，修复了米游社任务无权限访问的问题，修复了bilibili直播送礼物报错的问题
* 21.9.6 增加了bilibili赛事预测,修复了网易云uid超出int范围导致任务中断的问题
* 21.9.15 尝试更新了BILIBILI-HELPER至1.1.5，可以改善部分任务中出现的null错误
* 21.10.1 layui已下线，目前将所有cdn服务换为本地文件
* 21.10.3 修复了部分bug，更改了部分数据表字段名称使其更统一，增加bilibili反向赛事预测
* 21.10.4 增加了全局webhook，更改webhook的传入方式为json字符串，增加生成器页面用来生成webhook.json
* 21.11.6 修复米游币任务分享帖子失效的问题
* 21.12.11 修复log4j2漏洞
* 21.12.29 修改米游社原神签到逻辑，适配米游社账号下多角色不同服的签到处理，并修改一些页面显示，使其更美观（感谢@MuXia-0326）
* 22.1.26 **（2.0版本重大更新）**
   1. 重构了所有页面，所有请求均改为前后端分离
   2. 增加登录注册验证码校验
   3. 修复网易云登录时可能出现的乱码问题
   4. 个人任务管理支持头像展示
   5. 增加actable自动建表，以后更改表结构时无需手动更改（第一次使用时还是需要导入sql）
   6. 将mybatis换为mybatis-plus，精简了大量xml文件
* 22.2.2 推送生成器支持自动填充json至推送框，首页公告改为从后台读取并且支持编辑功能
* 22.2.3 推送测试支持回显错误信息便于用户排查


### 鸣谢
1. <a href="https://github.com/JunzhouLiu/BILIBILI-HELPER-PRE">BILIBILI-HELPER-PRE（作者不干了）</a>
2. <a href="https://github.com/secriy/CloudMusic-LevelUp">CloudMusic-LevelUp</a>
3. <a href="https://github.com/PonKing66/genshi-helper">genshi-helper</a>
4. <a href="https://github.com/y1ndan/genshinhelper">genshinhelper</a>

感谢 JetBrains 对本项目的支持。

[![JetBrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg?_gl=1*y52vqx*_ga*NTE4NjY3NDA2LjE2MjY5NDU3MDk.*_ga_V0XZL7QHEB*MTYzMzE4NjE1Mi4yLjEuMTYzMzE4NjE4MS4w&_ga=2.80927447.171770786.1633179814-518667406.1626945709)](https://www.jetbrains.com/)

**免责声明：请勿将本项目用于付费代挂，或者是作为骗取cookie的黑产业链，任何造成的结果均与本项目无关！**
