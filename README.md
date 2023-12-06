<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Auto Plan Helper</h1>
<h4 align="center">自动化的托管系统</h4>
<p align="center">
	<a href="https://github.com/wyt1215819315/autoplan"><img src="https://img.shields.io/github/v/release/wyt1215819315/autoplan?color=green"></a>
	<img src="https://img.shields.io/github/stars/wyt1215819315/autoplan">
</p>

## 3.0重构目标
- [x] 更换鉴权框架为sa-token，升级springboot版本（并没有使用springboot3，因为这个项目前端还不分离的，怕出来各种奇怪的问题），使用jdk17（17增加了很多新特性，并且相对java8，gc机制优化了很多）
- [x] 删掉不需要的依赖，以后基本工具类都尽量用hutool吧，毕竟功能已经很齐了，不适应的话适应一下，一大堆乱七八糟的工具类增加项目体积不说，哪个出了个漏洞还得动，麻烦
- [ ] 兼容mysql和sqlite
- [x] 定时任务执行器优化，原先并不支持spring bean获取，得解决下；然后任务超时得设定下，不咋想用开源框架比如xxl-job啥的没法集成在项目里还得重新布一个 麻烦，看来只能硬着头皮改quartz了
- [ ] 任务执行这边优化，底层重构，封装一个完善的任务执行底层，争取做到低代码，每次新增模块就不用疯狂ctrl cv了，并且可以统一任务状态等，而不是一个任务一种代码，根本没法维护
- [x] 主要还是任务执行器这块吧，统一优化的话有利于大家二开
- [ ] 前端使用vue重构
- [ ] 不依赖任何其他中间件来启动，在sqlite模式下只需要启动项目本体即可
- [ ] 打包发布自动化，包括前端也一起打进来

## 项目简介
本项目为自动化的托管系统，目前支持以下功能：
1. b站每日自动经验任务
2. b站赛事预测赚硬币任务
3. 网易云自动签到刷歌任务
4. 米游社原神签到领奖励任务以及米游币任务

**如果觉得好用，点个star吧**

## 演示站地址
<a href="https://auto.oldwu.top/" target="_blank">点击打开Auto Plan</a>

**本人不会利用任何cookie，但是为了安全考虑，建议还是自己搭建运行环境**

## 项目架构

* Springboot
* <a href="https://github.com/dromara/sa-token">SaToken</a>
* Mybatis-Plus
* Quartz

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

> **提示：[Releases](https://github.com/wyt1215819315/autoplan/releases) 中的jar包可能更新不及时，项目设置有自动构建，急需最新版jar包，可前往 [actions](https://github.com/wyt1215819315/autoplan/actions) 自行下载**

**版本更新时，请务必备份数据库，以免未知的后果造成影响**

## 拉取项目
```shell
# 拉取后台主项目
$ git clone https://github.com/wyt1215819315/autoplan.git
# 进入目录
$ cd autoplan
# 初始化子项目（前端）
$ git submodule init
# 更新子项目
$ git submodule update
```


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
* 22.2.3 **2.0版本必更！** 
   1. 推送测试支持回显错误信息便于用户排查
   2. 发布2.1版本，修改了mybatis-plus的配置，需要修改配置文件，详情请查看`application-example`
   3. 增加删除功能，之前忘记写了
* 22.2.5 修复网易云账号信息验证失败时无法打印错误信息的问题
* 22.2.9 修复webhook页面显示bug，修改公告修改页面的样式，给网易云重试添加延迟操作
* 22.5.24 bili-helper重构基本实现，可能还会有小问题，咕了n久，只是懒得写说明文档
* 22.11.18 **新增小米运动**，支持定时，步数可以同步到微信、支付宝。修复定时任务管理分页查询问题。

### 构建

##### 初始化git仓库
```shell
git clone https://github.com/wyt1215819315/autoplan
cd autoplan/autoplan-front
git submodule update --init --recursive
```

### 鸣谢
1. <a href="https://github.com/JunzhouLiu/BILIBILI-HELPER-PRE">BILIBILI-HELPER-PRE（作者不干了）</a>
2. <a href="https://github.com/secriy/CloudMusic-LevelUp">CloudMusic-LevelUp</a>
3. <a href="https://github.com/PonKing66/genshi-helper">genshi-helper</a>
4. <a href="https://github.com/y1ndan/genshinhelper">genshinhelper</a>

感谢 JetBrains 对本项目的支持。

[![JetBrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg?_gl=1*y52vqx*_ga*NTE4NjY3NDA2LjE2MjY5NDU3MDk.*_ga_V0XZL7QHEB*MTYzMzE4NjE1Mi4yLjEuMTYzMzE4NjE4MS4w&_ga=2.80927447.171770786.1633179814-518667406.1626945709)](https://www.jetbrains.com/)

**免责声明：请勿将本项目用于付费代挂，或者是作为骗取cookie的黑产业链，任何造成的结果均与本项目无关！**
