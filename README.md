# [Mirai Hibernate Http](https://github.com/cssxsh/mirai-hibernate-http)

> Mirai Hibernate Plugin 的 Http 扩展

相关项目:  
[Mirai Hibernate Plugin](https://github.com/cssxsh/mirai-hibernate-plugin) 前置插件，用于 Hibernate ORM 框架的初始化  
[Mirai Hibernate Web](https://github.com/cssxsh/mirai-hibernate-web) 用于提供 WEB 页面  
[Mirai Api Http](https://github.com/project-mirai/mirai-api-http) 前置插件，提供HTTP API  

## PersistenceFactory

**since 1.1.0**
为 `mirai-api-http` 提供了新的 PersistenceFactory `hibernate`  
使用 `mirai-hibernate-plugin` 所提供的 ORM 消息记录器 实现了历史消息接口  
配置方法为修改 `config/net.mamoe.mirai-api-httpsetting.yml` 的 `persistenceFactory` 配置项 为 `hibernate`

## Adapter

为 `mirai-api-http` 提供了新的 Adapter `hibernate`

### Route

下列路由中，start 和 end 参数都是 unix timestamp

#### Message Route

1.  GET `/message/bot?bot={}&start={}&end={}`  
    获取指定机器人从 start 到 end 之间的消息记录  
2.  GET `/message/group?bot={}&group={}&start={}&end={}`  
    获取指定群聊从 start 到 end 之间的消息记录
3.  GET `/message/friend?bot={}&friend={}&start={}&end={}`  
    获取指定好友从 start 到 end 之间的消息记录
4.  GET `/message/member?bot={}&group={}&member={}&start={}&end={}`  
    获取指定群员从 start 到 end 之间的消息记录
5.  GET `/message/stranger?bot={}&stranger={}&start={}&end={}`  
    获取指定陌生人从 start 到 end 之间的消息记录
6.  GET `/message/kind?kind={}&start={}&end={}`  
    获取指定类型 `GROUP, FRIEND, TEMP, STRANGER` 从 start 到 end 之间的消息记录
7.  GET `/message/source?target={}&time={}&ids={}`  
    获取指定源的消息记录

#### Face Route

1. GET `/face/random`  
    随机一条表情包记录
2. PUT `/face/disable?md5={}`  
    屏蔽一条表情包记录
3. GET `/face/tag?md5={}`  
   获取一条指定的表情包记录
4. PUT `/face/tag?md5={}&tag={}`  
   为表情包设置一个tag
5. DELETE `/face/tag?md5={}&tag={}`  
   为表情包删除一个tag

#### Archive Route

1. GET `/archive/bot`  
   获取所有的 bot record
2. GET `/archive/group?bot={}`  
   获取所有的 group record
3. GET `/archive/friend?bot={}`  
   获取所有的 friend record
4. GET `/archive/member?group={}`  
   获取所有的 member record

### Web

***未完工***  
到 [mirai-hibernate-web](https://github.com/cssxsh/mirai-hibernate-web/releases) 下载 web.zip 压缩包  
解压到 `data/xyz.cssxsh.mirai.plugin.mirai-hibernate-http/web` 即可提供浏览器访问WEB页面

## 安装

### MCL 指令安装

**请确认 mcl.jar 的版本是 2.1.0+**  
`./mcl --update-package xyz.cssxsh.mirai:mirai-hibernate-http --channel maven-stable --type plugins`

### 手动安装

1. 从 [Releases](https://github.com/cssxsh/mirai-hibernate-http/releases) 或者 [Maven](https://repo1.maven.org/maven2/xyz/cssxsh/mirai/mirai-hibernate-http/) 下载 `mirai2.jar`
2. 将其放入 `plugins` 文件夹中

## [爱发电](https://afdian.net/@cssxsh)

![afdian](.github/afdian.jpg)