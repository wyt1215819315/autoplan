<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Auto Plan Helper</h1>
<h4 align="center">自动化的托管系统</h4>
<p align="center">
	<a href="https://github.com/wyt1215819315/autoplan"><img src="https://img.shields.io/github/v/release/wyt1215819315/autoplan?color=green"></a>
	<img src="https://img.shields.io/github/stars/wyt1215819315/autoplan">
</p>

## 3.0版本重构

### 说明
3.0版本的重构已经完全不兼容老数据表结构，可以理解为基本上是个新的项目，无法从2.x进行数据迁移，3.0版本目前还在测试阶段，有些功能并没有完全写完，不过主要功能都可以正常使用了

### 重构目标

- [x] 更换鉴权框架为sa-token，升级springboot版本（并没有使用springboot3，因为这个项目前端还不分离的，怕出来各种奇怪的问题），使用jdk17（17增加了很多新特性，并且相对java8，gc机制优化了很多）
- [x] 删掉不需要的依赖，以后基本工具类都尽量用hutool吧，毕竟功能已经很齐了，不适应的话适应一下，一大堆乱七八糟的工具类增加项目体积不说，哪个出了个漏洞还得动，麻烦
- [ ] 兼容mysql和sqlite
- [x] 定时任务执行器优化，原先并不支持spring bean获取，得解决下；然后任务超时得设定下，不咋想用开源框架比如xxl-job啥的没法集成在项目里还得重新布一个 麻烦，看来只能硬着头皮改quartz了
- [x] 任务执行这边优化，底层重构，封装一个完善的任务执行底层，争取做到低代码，每次新增模块就不用疯狂ctrl cv了，并且可以统一任务状态等，而不是一个任务一种代码，根本没法维护
- [x] 前端使用vue重构
- [ ] 不依赖任何其他中间件来启动，在sqlite模式下只需要启动项目本体即可
- [ ] docker部署
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
* 支持b站签到任务以及赛事预测任务
* 支持扫码登录和cookie登录

### 米游社
* 原神签到任务
* 星铁签到任务
* 米游币任务

### 云原神
* 每日获取免费时长

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
     * 导入idea并下载依赖（请使用JDK17）
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

### 拉取项目
```shell
# 拉取后台主项目
$ git clone https://github.com/wyt1215819315/autoplan.git
# 进入目录
$ cd autoplan/autoplan-front
# 初始化子项目（前端）
$ git submodule init
# 更新子项目
$ git submodule update --init --recursive
```

### 构建

### [更新日志](docs/update_log.md)

### 项目说明
作者本人没有很多时间去维护各种任务，而且仅仅只会简单的抓包，如果任务本身有问题比如api换了什么，加了加密算法什么的，作者可能没有时间和能力去及时维护这些任务，所以还是欢迎各位大佬提交pr来共同维护项目吧

项目主体部分有问题的话我会尽力去修复的

### 鸣谢
1. <a href="https://github.com/JunzhouLiu/BILIBILI-HELPER-PRE">BILIBILI-HELPER-PRE（作者不干了）</a>
2. <a href="https://github.com/secriy/CloudMusic-LevelUp">CloudMusic-LevelUp</a>
3. <a href="https://github.com/PonKing66/genshi-helper">genshi-helper</a>
4. <a href="https://github.com/y1ndan/genshinhelper">genshinhelper</a>

感谢 JetBrains 对本项目的支持。

[![JetBrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg?_gl=1*y52vqx*_ga*NTE4NjY3NDA2LjE2MjY5NDU3MDk.*_ga_V0XZL7QHEB*MTYzMzE4NjE1Mi4yLjEuMTYzMzE4NjE4MS4w&_ga=2.80927447.171770786.1633179814-518667406.1626945709)](https://www.jetbrains.com/)

**免责声明：请勿将本项目用于付费代挂，或者是作为骗取cookie的黑产业链，任何造成的结果均与本项目无关！**
