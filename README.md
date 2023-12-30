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
- [x] 兼容mysql和sqlite
- [x] 定时任务执行器优化，原先并不支持spring bean获取，得解决下；然后任务超时得设定下，不咋想用开源框架比如xxl-job啥的没法集成在项目里还得重新布一个 麻烦，看来只能硬着头皮改quartz了
- [x] 任务执行这边优化，底层重构，封装一个完善的任务执行底层，争取做到低代码，每次新增模块就不用疯狂ctrl cv了，并且可以统一任务状态等，而不是一个任务一种代码，根本没法维护
- [x] 前端使用vue重构
- [x] 不依赖任何其他中间件来启动，在sqlite模式下只需要启动项目本体即可
- [x] docker部署
- [x] 打包发布自动化，包括前端也一起打进来
- [x] 修改密码
- [x] 用户管理
- [x] 任务列表管理
- [x] 推送的markdown文本样式优化
- [ ] 移动端样式优化
- [ ] 任务推送结果（集成在任务日志页面中）
- [ ] 首页版本日志
- [ ] 首页任务统计
- [ ] webhook推送模式（轮询一遍/轮询到成功为止）
- [ ] 自定义某些字段的渲染表达式

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

### docker部署（推荐）
#### 基本用法
```shell
docker run --name autoplan \
-p 需要映射的端口:80 \
-e APP_DB_TYPE=数据库类型  \
-e APP_DB_URL=数据库地址  \
-e APP_DB_USER=数据库用户  \
-e APP_DB_PWD=数据库密码  \
-v 内置数据库挂载位置:/app/db \
-d wyt1215819315/auto_plan:latest
```

#### 使用内置数据库sqlite启动
```shell
docker run --name autoplan \
-p 需要映射的端口:80 \
-v 内置数据库挂载位置:/app/db \
-d wyt1215819315/auto_plan:latest
```

#### 使用自建mysql启动
```shell
docker run --name autoplan \
-p 需要映射的端口:80 \
-e APP_DB_TYPE=mysql  \
-e APP_DB_URL=127.0.0.1:3306/auto_plan  \
-e APP_DB_USER=root  \
-e APP_DB_PWD=123456  \
-d wyt1215819315/auto_plan:latest
```

#### 查看日志
```shell
# docker logs [容器id或名称]
docker logs autoplan
```

### docker-compose部署
1. 按照`docker-compose.yml`中的提示自行修改，配置好数据库，挂载路径等配置
2. 执行`docker-compose up -d`
3. 使用`docker-compose logs`来查看运行日志

### 手动部署（前后端都需要单独部署）

1. release中打包好的压缩包中包含jdk17环境和启动脚本，根据你的环境选择相对应的包（如果之前已经下过包了，只需要下载auto_plan.jar覆盖掉原来解压出来的即可，如果配置文件有改动的话也要手动改一下）
2. 按需要修改配置文件： 默认配置为使用本地sqlite数据库，无需独立部署，如果需要改为mysql，请修改配置文件中`system.datasource`项中的配置，并将`docs/auto_plan.sql`导入你的数据库中
3. 运行start.bat（win）或start.sh（linux）启动即可，观察日志，如果无报错则后台部署完毕
4. 将release中的autoplan_front.zip前端包解压出来，部署到nginx即可，包支持gzip，推荐在nginx开启gzip以获得最佳性能

### 注意事项
1. 第一次启动时无用户的情况，系统会生成一个默认的超管用户，密码会打印在控制台上请注意查看
2. 自动构建的包仅支持根域名部署，后台访问路径为`/api`，如果需要在二级路径部署，请自行修改配置文件后重新编译（docker部署的也许能在外面一层nginx rewrite，没试过）
3. 如果使用mysql模式下，docker里的应用无法连接部署在宿主机的数据库，请检查防火墙配置，docker桥接的情况下，相当于容器本身有一个自己的ip，并且不能使用127.0.0.1来访问宿主机上部署的mysql，需要使用内网地址

**版本更新时，请务必备份数据库，以免未知的后果造成影响**


## 构建
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

### 运行环境
1. JDK17
2. Maven
3. Node.js 16+
4. pnpm

### 构建指令
#### 后台
```shell
mvn install
```
#### 前端
```shell
# 全局安装pnpm
npm install -g pnpm
# 使用pnpm安装依赖
pnpm install
# 本地开发环境测试
pnpm serve
# 编译前端包（输出在前端路径的dist下）
pnpm build
```

## [更新日志](docs/update_log.md)

## 项目说明
作者本人没有很多时间去维护各种任务，而且仅仅只会简单的抓包，如果任务本身有问题比如api换了什么，加了加密算法什么的，作者可能没有时间和能力去及时维护这些任务，所以还是欢迎各位大佬提交pr来共同维护项目吧

项目主体部分有问题的话我会尽力去修复的

## 鸣谢
1. <a href="https://github.com/JunzhouLiu/BILIBILI-HELPER-PRE">BILIBILI-HELPER-PRE（作者不干了）</a>
2. <a href="https://github.com/secriy/CloudMusic-LevelUp">CloudMusic-LevelUp</a>
3. <a href="https://github.com/PonKing66/genshi-helper">genshi-helper</a>
4. <a href="https://github.com/y1ndan/genshinhelper">genshinhelper</a>

感谢 JetBrains 对本项目的支持。

[![JetBrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg?_gl=1*y52vqx*_ga*NTE4NjY3NDA2LjE2MjY5NDU3MDk.*_ga_V0XZL7QHEB*MTYzMzE4NjE1Mi4yLjEuMTYzMzE4NjE4MS4w&_ga=2.80927447.171770786.1633179814-518667406.1626945709)](https://www.jetbrains.com/)

**免责声明：请勿将本项目用于付费代挂，或者是作为骗取cookie的黑产业链，任何造成的结果均与本项目无关！**
