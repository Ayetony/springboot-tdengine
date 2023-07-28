# SpringBoot-TDengine

#### 介绍
快速搭建springboot+TDengine+Mybatis+Mysql项目，此项目为TDengine+Mysql双数据源配置。

#### 安装教程

1.  本项目是使用JDBC-JNI的driver，端口为6030.如果没有安装TDengine的client端请先进入涛思官网下载2.0.17.0版本客户端。如果你使用的是JDBC-RESTful格式请将端口修改为6041.
2.  安装成功后配置本地hosts文件，输入ip和FNDQ
3.  启动使用

#### 使用说明

1.  请勿更换连接池，否则无法使用
2.  JDBC-JNI必须配置hosts文件，JDBC-RESTful未测试
3.  如有问题请看官方文档 [TDengine官网](https://www.taosdata.com/cn/documentation)