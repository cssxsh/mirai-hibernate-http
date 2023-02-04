# [Mirai Hibernate Http](https://github.com/cssxsh/mirai-hibernate-http)

> Mirai Hibernate Plugin 的 Http 扩展

相关项目:  
[Mirai Hibernate Plugin](https://github.com/cssxsh/mirai-hibernate-plugin) 前置插件，用于 Hibernate ORM 框架的初始化  
[Mirai Hibernate Web](https://github.com/cssxsh/mirai-hibernate-web) 用于提供 WEB 页面  
[Mirai Api Http](https://github.com/project-mirai/mirai-api-http) 前置插件，提供HTTP API  

## Adapter

为 `mirai-api-http` 提供了新的 Adapter `hibernate`  
使用 `mirai-hibernate-plugin` 所提供的 ORM 消息记录器 实现了历史消息接口

### Route

下列路由中，start 和 end 参数都是 unix timestamp

1. GET `/message/bot?bot={}&start={}&end={}`  
    获取指定机器人从 start 到 end 之间的消息记录  
2. GET `/message/group?bot={}&group={}&start={}&end={}`  
    获取指定群聊从 start 到 end 之间的消息记录
3. GET `/message/friend?bot={}&friend={}&start={}&end={}`  
    获取指定好友从 start 到 end 之间的消息记录
4. GET `/message/member?bot={}&group={}&member={}&start={}&end={}`  
    获取指定群员从 start 到 end 之间的消息记录
5. GET `/message/stranger?bot={}&stranger={}&start={}&end={}`  
    获取指定陌生人从 start 到 end 之间的消息记录
6. GET `/message/kind?kind={}&start={}&end={}`  
    获取指定类型 `(GROUP, FRIEND, TEMP, STRANGER)` 从 start 到 end 之间的消息记录
7. GET `/message/source?target={}&time={}&ids={}`  
    获取指定源的消息记录
8. GET `/face/random`  
    随机一条表情包记录
9. PUT `/face/disable?md5={}`  
    屏蔽一条表情包记录
10. GET `/face/tag?md5={}`  
    获取一条指定的表情包记录
11. PUT `/face/tag?md5={}&tag={}`  
    为表情包设置一个tag
12. DELETE `/face/tag?md5={}&tag={}`  
    为表情包删除一个tag
13. GET `/archive/bot`  
    获取所有能作为条件的 bot id
14. GET `/archive/group?bot={}`  
    获取所有能作为条件的 group id
14. GET `/archive/user?bot={}`  
    获取所有能作为条件的 user id

### Web

***未完工***  
到 [mirai-hibernate-web](https://github.com/cssxsh/mirai-hibernate-web/releases) 下载 web.zip 压缩包  
解压到 `data/xyz.cssxsh.mirai.plugin.mirai-hibernate-http/web` 即可提供浏览器访问WEB页面

## 安装

### 手动安装

1. 运行 [Mirai Console](https://github.com/mamoe/mirai-console) 生成 `plugins` 文件夹
2. 从 [Releases](https://github.com/cssxsh/mirai-hibernate-http/releases) 下载 `jar` 并将其放入 `plugins` 文件夹中