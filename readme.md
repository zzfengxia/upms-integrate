# upms用户权限后台综合管理系统

## 技术框架
### 后端
- Spring Boot 2.0
- Spring MVC
- Apache Shiro 1.4
- MyBatis-plus
- Druid
- Freemarker

### 前端
- Vue2.0
https://cn.vuejs.org/v2/guide/

- jquery3.3.1

- 弹窗插件-layer
http://www.layui.com/doc/modules/layer.html

- bootstrap4.1
https://getbootstrap.com/docs/4.1/components/forms/

- 后台控制面板-adminLTE3.0

- 树插件-zTree
http://www.treejs.cn/v3/api.php

- 表格-bootstrap-table
http://bootstrap-table.wenzhixin.net.cn/documentation/

## 工程结构
``` 
upms-integrate
├── upms-base -- 基础类，工具类
├── upms-generator -- 自动生成代码工具项目
├── upms-rabbitmq  -- 消息队列集成
├── upms-web       -- 通用管理平台模板项目

```
## upms-web项目搭建启动说明
1. 安装mysql数据库，导入`db/system-mysql.sql`初始化脚本
2. 安装redis
3. 修改resources下的配置文件中的mysql、redis配置
4. 启动项目
5. 启动成功后，浏览器输入`http://localhost:8081/upms`访问  
默认登录账户为:  
admin/123456

登录界面：
![登录界面](https://images.gitee.com/uploads/images/2021/0506/153243_bdf5ea0f_1384692.png "登录界面.png")

菜单管理界面：
![功能菜单](https://images.gitee.com/uploads/images/2021/0506/153335_3a9494c3_1384692.png "功能菜单.png")
