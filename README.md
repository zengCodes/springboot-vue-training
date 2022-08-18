# 基于SpringBoot + Vue的软考在线培训系统

### **前言**
基于SpringBoot + Vue的软考在线培训系统，网站总体包含两部分，一部分是前台，一部分是后台管理；实现培训机构管理员、培训讲师、学员三者在管理端和学员端中的需求。管理端系统包括了讲师管理、课程管理 、考试管理等模块，主要实现讲师申请和审核、课程设置和审核、导入试卷和题库的管理。学员端系统则是包括了考试、课程、综合等模块，分别实现了学员的题目训练、模拟考试以及社区讨论等功能。

### **组织结构**

后端项目结构的一些简单说明

```js

├─zeng-business 业务模块服务
│  
├─zeng-common   公共模块服务
│  
├─zeng-framework  开发组件服务
│  
├─zeng-mail-server  邮箱模块服务
│  
├─zeng-system  系统模块服务
│   
└─zeng-web   系统启动服务

```

### **技术选型**

|  后端技术   |        名称         |  版本  |
| :---------: | :-----------------: | :----: |
| Spring Boot |     Spring框架      | 2.2.13  |
|   Redis    |  分布式缓存数据库   | 2.2.7  |
|   spring-boot-starter-amqp    |  RabbitMQ应用   | 2.2.13  |
| tk.mybatis  |       ORM框架       | 4.0.3  |
|    Maven   |    项目构建管理     | 3.6.0+ |
|   alibaba Druid    |    数据库连接池     | 1.2.6 |
|    MySQL   |       数据库        |  5.6+  |
| com.github.pagehelper  | 分页插件 | 1.3.1 |
|  com.alibaba.fastjson |      数据解析工具       | 1.2.76  |
|  swagger  |      后台api管理工具       | 3.0.0  |
|  com.github.penggle  |      验证码生成       | 2.3.2  |
|  qiniu-java-sdk  |      oss存储       | [7.7.0, 7.7.99]  |


| 前端技术  |          使用            |
| :-------:  |:------------------------: |
|    Vue2    |        前端js框架         |
|   vue-router   |       vue路由       |
|   Vuex     |       状态管理          |
| Element-UI |       前端UI组件库       |
|  axios     |       http请求库       |
| echarts、antv/g2、antv/g6     |       图表可视化库       |
|   stompjs   |       websocket库       |
|   vue-simple-uploader   |       文件断点续传、分片上传       |
|   sass   |       css编译语言       |
|   webpack     |       前端构建工具          |

### **开发环境**

```
安装 JDK（1.8+）
安装 Maven (3.6.0+)
安装 Redis 服务 (3.0+)
安装 RabbitMQ （3.9.11）
安装 MySQL (5.6+)
安装 IDEA（2019.3）
安装 Node (16.15.1)
安装 npm (8.13.1)
安装 VS CODE
```

### 项目截图

前台

![image-20220716155055925](https://cdn.jsdelivr.net/gh/zengCodes/zeng-figure-bed/202207161550009.png)

![image-20220716154608066](https://cdn.jsdelivr.net/gh/zengCodes/zeng-figure-bed/202207162302264.png)


![image-20220716154538672](https://cdn.jsdelivr.net/gh/zengCodes/zeng-figure-bed/202207161547968.png)

后台管理

![image-20220716142825917](https://cdn.jsdelivr.net/gh/zengCodes/zeng-figure-bed/202207161428096.png)