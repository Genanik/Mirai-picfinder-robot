# Mirai-PicFinder-robot
感谢 Tsuk1ko 所写的 [CQ-picfinder-robot](https://github.com/Tsuk1ko/CQ-picfinder-robot), 为本repo直接提供灵感  

基于Mirai的搜图机器人  
未来将支持 WhatAnime  
通过管理员指令PicFinder APIKey xxxxx添加SauceNAO APIKey

目前支持的搜图引擎：

- [SauceNAO](https://saucenao.com)
- [Ascii2d](http://www.ascii2d.net)

其他功能：

- 复读
- bilibili视频详细信息

更多功能正在开发，欢迎提出想法或bug至issue

##部署方式
在本页面Releases处下载jar并丢到mirai目录下的/plugins里  
  
在mirai-console里输入`PicFinder APIKey xxxxx`添加SauceNAO APIKey，其中xxxxx需要改为你自己的APIKey，详细信息在第一次启动时有提示

##使用方式

群内发送`/help`显示 指令|功能 菜单

搜图：@机器人并在同一条消息内加上待搜索的图片  
复读：群内出现两条相同消息，机器人自动复读  
B站：群内出现关于b站视频的消息，自动解析

使用`关闭xx`或`打开xx`可临时关闭或是打开某功能，详见群内`/help`