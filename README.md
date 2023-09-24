<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Auto Plan Helper</h1>
<h4 align="center">自动化的托管系统</h4>
<p align="center">
	<a href="https://github.com/wyt1215819315/autoplan"><img src="https://img.shields.io/github/v/release/wyt1215819315/autoplan?color=green"></a>
	<img src="https://img.shields.io/github/stars/wyt1215819315/autoplan">
</p>

## 项目简介

本项目为自动化的托管系统，目前支持以下功能：

1. b站每日自动经验任务
2. b站赛事预测赚硬币任务
3. 网易云自动签到刷歌任务
4. 米游社原神签到领奖励任务以及米游币任务

**如果觉得好用，点个star吧**

> **2.5以上版本由于重构了bili-helper，原来的数据库结构不再兼容新版，请使用管理员用户登录并在bili任务中点击“转json”按钮完成配置转换，记得备份原来的数据库
**

> 1.x - 2.0版本升级需要升级配置文件，以及新增一个定时任务(不一定要执行sql，可以直接去管理界面加)：

```mysql
INSERT INTO `t_sys_quartz_job` (`id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`,
                                `concurrent`, `status`)
VALUES ('592295794938351617', '米游社更新个人信息', 'DEFAULT', 'mihuyouTask.updateAvatar()', '0 15 0 ? * MON', '3', '1',
        0);
```

***

> **2.11版本新增小米运动，需要新增数据库，请执行如下sql**

```mysql
# 任务记录表
CREATE TABLE `auto_xiaomi`
(
    `id`                INT(11)     NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `user_id`           INT(11)     NULL DEFAULT NULL COMMENT '外键约束user_id',
    `phone`             VARCHAR(50) NOT NULL COMMENT '小米账号' COLLATE 'utf8mb4_general_ci',
    `password`          VARCHAR(50) NOT NULL COMMENT '密码' COLLATE 'utf8mb4_general_ci',
    `steps`             VARCHAR(5)  NULL DEFAULT NULL COMMENT '步数' COLLATE 'utf8mb4_general_ci',
    `previous_occasion` VARCHAR(5)  NULL DEFAULT NULL COMMENT '上次提交的步数' COLLATE 'utf8mb4_general_ci',
    `name`              VARCHAR(50) NULL DEFAULT NULL COMMENT '任务名称' COLLATE 'utf8mb4_general_ci',
    `status`            VARCHAR(10) NULL DEFAULT NULL COMMENT '任务状态' COLLATE 'utf8mb4_general_ci',
    `random_or_not`     CHAR(1)     NULL DEFAULT NULL COMMENT '是否随机：0否，1是' COLLATE 'utf8mb4_general_ci',
    `enable`            VARCHAR(50) NULL DEFAULT NULL COMMENT '任务是否开启' COLLATE 'utf8mb4_general_ci',
    `enddate`           DATETIME    NULL DEFAULT NULL COMMENT '任务结束时间',
    `webhook`           TEXT        NULL DEFAULT NULL COMMENT '推送地址json' COLLATE 'utf8mb4_general_ci',
    `CREATED_TIME`      DATETIME    NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `userid` (`user_id`) USING BTREE,
    CONSTRAINT `auto_xiaomi_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
)
    COLLATE = 'utf8mb4_general_ci'
    ENGINE = InnoDB
;

# 定时任务执行sql
INSERT INTO `t_sys_quartz_job`
VALUES ('684022184875790336', '小米自动刷步数', 'DEFAULT', 'xiaomiTask.doAutoCheck()', '0 0 12 * * ? *', '3', '1', 0);
INSERT INTO `t_sys_quartz_job`
VALUES ('684022184905150464', '小米运动定时重置任务状态', 'DEFAULT', 'xiaomiTask.resetStatus()', '0 0 0 * * ? *', '3',
        '1', 0);

```

## 演示站地址

<a href="https://auto.oldwu.top/" target="_blank">点击打开Auto Plan</a>

**本人不会利用任何cookie，但是为了安全考虑，建议还是自己搭建运行环境**

## 项目架构

基于Springboot、SpringSecurity、layui、mysql开发

定时任务核心：quartz（从pearadmin中抠过来的）

## 使用说明

### bilibili

> 原作者开源项目已经停止维护，可以看看他的博客声明https://blog.misec.top/archives/bye-helper
>

* 支持b站签到任务以及赛事预测任务
* 支持扫码登录和cookie登录
* cookie登录请参考<a href="https://blog.oldwu.top/index.php/archives/84/#toc_6">这里</a>以获取cookie值

### 网易云

* 网易云每日签到和网易云每日刷300首歌

> **由于网易云的检测机制会封服务器ip，导致目前该功能的可用性为零**

### 米游社

* 原神签到任务
* 米游币任务

### 小米运动

- 每天中午12点定时提交任务，步数可以同步到微信、支付宝。绑定好就行了

[**更多详细使用说明请查看**](https://blog.oldwu.top/index.php/archives/84/#toc_5)

## 项目部署

### jar包部署

1. 首先准备好`application.yml`配置文件，模板文件可以点击链接下载：
   [application.yml](https://github.com/wyt1215819315/autoplan/blob/master/application-example.yml)
2. 在`mysql`中创建数据库并导入`auto_plan.sql`
3. 接下来你可以选择两种方式部署：
    * 第一种方法 **使用 <a href="https://github.com/wyt1215819315/autoplan/releases">Releases</a> 中打包好的jar运行**
        * 将`application.yml`修改正确并放入jar包同级目录中
        * 使用`java -jar xxx.jar`运行
    * 第二种方法 **自行编译jar包**
        * 导入idea并下载依赖（请使用JDK1.8）
        * 在`resources`文件夹放入`application.yml`配置文件（可选，你可以选择外置配置文件）
        * 使用maven install打包成jar
        * 使用`java -jar xxx.jar`运行


4. 注册账号，并将其定为管理员账户，步骤：
    * 查看`sys_user`表中你的账号对应的`id`
    * 进入`sys_role_user`表中找到对应的`user_id`
    * 将对应行的`sys_role_id`值改为1
5. 一些定时任务的配置请登录管理员账号在`自动任务管理`中查看

> **提示：[Releases](https://github.com/wyt1215819315/autoplan/releases)
中的jar包可能更新不及时，项目设置有自动构建，急需最新版jar包，可前往 [actions](https://github.com/wyt1215819315/autoplan/actions)
自行下载**

**版本更新时，请务必备份数据库，以免未知的后果造成影响**

如果你不需要自动建表，请将yml配置文件中的actable有关的项全都注释掉即可

### docker部署

1. 安装docker
    * 这一步请自行百度,如何安装
2. 克隆项目到本地

```bash
    git clone https://github.com/wyt1215819315/autoplan.git
```

3. 进入`autoplan/docker-run`目录

    1. 修改`application.yml`文件的密码为你像设置的密码
    ```yml
        # 在下面填上你的数据库密码(注意空格)
        password: 密码 
    ```
    2. 修改`run.sh`文件的密码为你像设置的密码
    ```bash
        # 输入数据库的密码
        MYSQL_PASSWORD=""
    ```
   3. 执行`run.sh`文件
    ```bash
       chmod +x run.sh
       ./run.sh
    ```

### docker-compose部署

1. 安装docker-compose
    * 这一步请自行百度,如何安装
2. 克隆项目到本地

```bash
    git clone https://github.com/wyt1215819315/autoplan.git
```

3. 进入`autoplan/docker-run`目录

    1. 修改`application.yml`文件的密码为你像设置的密码
    ```yml
        # 在下面填上你的数据库密码(注意空格)
        password: 密码 
    ```
    2. 修改`docker-compose.yml`文件的密码为你像设置的密码
    ```bash
        environment:
          # 设置数据库密码
          - MYSQL_ROOT_PASSWORD=
    ```
    3. 执行`docker-compose`文件
    ```bash
        # docker-compose安装的版本不同执行的指令也不同
        # 根据自己安装的版本，下面两个命令二选一
        docker-compose up -d
        docker compose up -d
    ```

## 一些问题

1. 代码不是一般的乱，（非常非常乱....而且很多地方不符合规范），本人萌新一枚，请大佬多多指教

## 未来

1. 管理员功能：查看日志，删除任务等
2. go-cqhttp推送（需要加机器人为好友）
3. ~~手动执行b站任务（咕咕咕）~~
5. 修改密码功能
6. 自动清理n天之前的日志

## 更新日志

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
* 22.2.3 **2.0版本必更！**
    1. 推送测试支持回显错误信息便于用户排查
    2. 发布2.1版本，修改了mybatis-plus的配置，需要修改配置文件，详情请查看`application-example`
    3. 增加删除功能，之前忘记写了
* 22.2.5 修复网易云账号信息验证失败时无法打印错误信息的问题
* 22.2.9 修复webhook页面显示bug，修改公告修改页面的样式，给网易云重试添加延迟操作
* 22.5.24 bili-helper重构基本实现，可能还会有小问题，咕了n久，只是懒得写说明文档
* 22.11.18 **新增小米运动**，支持定时，步数可以同步到微信、支付宝。修复定时任务管理分页查询问题。
* 23.9.24 新增docker部署教程

## 鸣谢

1. <a href="https://github.com/JunzhouLiu/BILIBILI-HELPER-PRE">BILIBILI-HELPER-PRE（作者不干了）</a>
2. <a href="https://github.com/secriy/CloudMusic-LevelUp">CloudMusic-LevelUp</a>
3. <a href="https://github.com/PonKing66/genshi-helper">genshi-helper</a>
4. <a href="https://github.com/y1ndan/genshinhelper">genshinhelper</a>

感谢 JetBrains 对本项目的支持。

[![JetBrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg?_gl=1*y52vqx*_ga*NTE4NjY3NDA2LjE2MjY5NDU3MDk.*_ga_V0XZL7QHEB*MTYzMzE4NjE1Mi4yLjEuMTYzMzE4NjE4MS4w&_ga=2.80927447.171770786.1633179814-518667406.1626945709)](https://www.jetbrains.com/)

**免责声明：请勿将本项目用于付费代挂，或者是作为骗取cookie的黑产业链，任何造成的结果均与本项目无关！**
