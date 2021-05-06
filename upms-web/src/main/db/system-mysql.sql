
/*CREATE TABLE `cai_piao_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `code` varchar(10) NOT NULL COMMENT '期数代码',
  `date` varchar(10) DEFAULT NULL COMMENT '日期',
  `poolmoney` decimal(15,0) DEFAULT NULL COMMENT '奖池金额(元)',
  `sales` int(11) DEFAULT NULL COMMENT '销售注数',
  `content` varchar(600) DEFAULT NULL,
  `red` varchar(32) DEFAULT NULL COMMENT '红球',
  `blue` varchar(4) DEFAULT NULL COMMENT '篮球',
  `blue2` varchar(4) DEFAULT NULL,
  `prizegrades` text COMMENT '中奖信息',
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_name_code` (`name`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;*/

/*Data for the table `cai_piao_history` */

/*Table structure for table `pm_config_params` */

CREATE TABLE `pm_config_params` (
  `param_key` varchar(125) NOT NULL COMMENT '参数KEY',
  `param_value` text COMMENT '参数值',
  `status` tinyint(1) DEFAULT '1' COMMENT '1:正常;0:禁用',
  `data_group` varchar(10) DEFAULT NULL,
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `c_time` datetime DEFAULT NULL,
  `m_time` datetime DEFAULT NULL,
  PRIMARY KEY (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `pm_config_params` */

insert  into `pm_config_params`(`param_key`,`param_value`,`status`,`data_group`,`remark`,`c_time`,`m_time`) values ('toolConf','{\"authcode\": [\"generateUID\"], \"450000\": [], \"460000\": [\"createReturnFile\", \"checkCardKey\"], \"370200\": [\"createReturnFile\", \"checkCardKey\"], \"220000\": [\"createReturnFile\", \"checkCardKey\", \"saveTMK\", \"checkIn\"], \"130000\": [\"createReturnFile\", \"checkCardKey\"], \"130100\": [\"createReturnFile\", \"checkCardKey\"], \"610101\": [\"createReturnFile\", \"checkCardKey\"], \"610100\": [\"importAuthCode\"], \"320500\": [], \"210100\": [\"checkCardKey\"], \"650100\": [\"checkCardKey\"], \"340100\": [\"createReturnFile\", \"checkCardKey\"], \"330700\": [\"checkCardKey\"], \"315000\": [\"checkCardKey\"], \"320200\": [\"checkCardKey\"], \"650101\": [\"checkCardKey\"],\"370700\": [\"createReturnFile\", \"checkCardKey\"], \"440400\": [\"checkCardKey\"]}',1,'2','工具包配置','2019-04-11 11:11:31','2019-11-07 15:39:28');

/*Table structure for table `pm_dict` */

CREATE TABLE `pm_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dict_name` varchar(128) DEFAULT NULL,
  `dict_type` varchar(128) NOT NULL COMMENT '缓存key',
  `dict_key` varchar(128) DEFAULT NULL COMMENT 'hash key',
  `dict_val` varchar(128) DEFAULT NULL COMMENT 'hash value',
  `dict_ord` int(10) DEFAULT '1',
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `pm_dict` */

/*Table structure for table `pm_menu` */

CREATE TABLE `pm_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `view_name` varchar(128) DEFAULT NULL,
  `type` int(6) DEFAULT NULL COMMENT '功能类型0:目录;1:菜单;2:按钮',
  `url` varchar(128) DEFAULT NULL COMMENT 'url',
  `perm` varchar(255) DEFAULT NULL COMMENT '权限标识',
  `parent_id` int(11) DEFAULT NULL COMMENT '父功能',
  `status` varchar(6) DEFAULT '1' COMMENT '0:删除；1:启用',
  `idx` int(6) DEFAULT '0' COMMENT '序号',
  `icon_class` varchar(32) DEFAULT NULL COMMENT '图标',
  `show_flag` tinyint(1) DEFAULT '1' COMMENT '1:展示;0:不展示 设置为0时作为单独的权限使用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;

/*Data for the table `pm_menu` */

insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (1,'用户',0,NULL,NULL,0,'1',1,'nav-icon fa fa-user',1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (2,'管理员账户',1,'views/system/user','user:show',1,'1',1,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (3,'功能菜单',1,'views/system/menu','menu:show',1,'1',2,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (4,'角色管理',1,'views/system/role','role:show',1,'1',3,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (5,'新增',2,NULL,'system:user:create;system:user:save',2,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (6,'修改',2,NULL,'system:user:update;system:user:save',2,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (7,'保存',2,'/admin/user/save/','system:user:save',2,'1',0,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (8,'删除',2,NULL,'system:user:del',2,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (9,'新增',2,NULL,'menu:create;menu:save',3,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (10,'修改',2,NULL,'menu:update;menu:save',3,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (11,'删除',2,NULL,'menu:del',3,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (12,'保存',2,'/menu/save/','menu:save',3,'1',0,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (13,'新增',2,NULL,'role:create;role:save',4,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (14,'修改',2,NULL,'role:update;role:save',4,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (15,'删除',2,NULL,'role:del',4,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (16,'保存',2,'/role/save/','role:save',4,'1',0,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (17,'系统',0,NULL,NULL,0,'1',2,'nav-icon fa fa-cog',1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (18,'数据字典',1,'views/system/dict','dict:show',17,'1',1,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (19,'查询',2,'/admin/user/list','user:list',2,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (20,'查询',2,'/menu/list','menu:list',3,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (21,'查询',2,'/role/list','role:list',4,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (22,'查询',2,'/dict/list','dict:list',18,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (23,'新增',2,NULL,'dict:create;dict:save',18,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (24,'修改',2,NULL,'dict:update;dict:save',18,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (25,'删除',2,NULL,'dict:del',18,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (26,'保存',2,'/dict/save','dict:save',18,'1',0,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (27,'重置密码',2,NULL,'user:reset:pwd',2,'1',0,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (29,'系统参数',1,'views/system/params','config:list',17,'1',3,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (30,'新增',2,NULL,'config:create;config:save',29,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (31,'修改',2,NULL,'config:update;config:save',29,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (32,'删除',2,NULL,'config:del',29,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (33,'查询',2,'/system/config/list','config:list',29,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (34,'保存',2,'/system/config/save/','config:save',29,'1',0,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (35,'平台管理',0,NULL,NULL,0,'1',3,'nav-icon fa fa-television',1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (36,'SEI管理',1,'views/platform/sei','sei:show',35,'1',1,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (37,'查询',2,'/paltform/sei/list','platform:sei:list',36,'1',1,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (38,'新增',2,NULL,'platform:sei:create;platform:sei:save',36,'1',1,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (39,'删除',2,NULL,'platform:sei:del',36,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (40,'修改',2,NULL,'platform:sei:update;platform:sei:save',36,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (42,'保存',2,'platform/sei/save/','platform:sei:save',36,'1',0,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (48,'加密机管理',1,'views/platform/hsmkey','hsmkey:show',35,'1',3,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (49,'保存',2,'/platform/hsmkey/save/','platform:hsmkey:save',48,'1',3,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (50,'修改',2,NULL,'platform:hsmkey:update;platform:hsmkey:save',48,'1',3,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (51,'删除',2,NULL,'platform:hsmkey:del',48,'1',3,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (52,'新增',2,NULL,'platform:hsmkey:create;platform:hsmkey:save',48,'1',3,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (53,'查询',2,'/paltform/hsmkey/list','platform:hsmkey:list',48,'1',3,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (54,'安全域管理',1,'views/platform/sdSetting','sdSetting:show',35,'1',4,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (55,'保存',2,'/platform/sdSetting/save/','platform:sdSetting:save',54,'1',4,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (56,'修改',2,NULL,'platform:sdSetting:update;platform:sdSetting:save',54,'1',4,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (57,'删除',2,NULL,'platform:sdSetting:del',54,'1',4,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (58,'新增',2,NULL,'platform:sdSetting:create;platform:sdSetting:save',54,'1',4,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (59,'查询',2,'/paltform/sdSetting/list','platform:sdSetting:list',54,'1',4,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (60,'SP管理',1,'views/platform/sp','sp:show',35,'1',2,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (61,'保存',2,'/paltform/sp/save/','paltform:sp:save',60,'1',2,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (62,'修改',2,NULL,'platform:sp:update;platform:sp:save',60,'1',2,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (63,'删除',2,'','platform:sp:del',60,'1',2,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (64,'新增',2,NULL,'platform:sp:create;platform:sp:save',60,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (65,'查询',2,'/paltform/sp/list','paltform:sp:list',60,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (66,'卡数据管理',0,NULL,NULL,0,'1',5,'nav-icon fa fa-id-card-o',1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (67,'DP文件',1,'views/dpcard/dp_file',NULL,66,'1',1,'',1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (68,'应用管理',1,'views/business/application','application:show',35,'1',5,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (69,'保存',2,'/business/application/save/','business:application:save',68,'1',0,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (70,'修改',2,NULL,'business:application:update;business:application:save',68,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (71,'删除',2,NULL,'business:application:del',68,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (72,'新增',2,NULL,'business:application:create;business:application:save',68,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (73,'查询',2,NULL,'business:application:list',68,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (74,'文件上传',2,NULL,'dpcard:dpFile:create',67,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (75,'修改',2,NULL,'dpcard:dpFile:update',67,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (76,'卡片类型配置',1,'views/spbiz/card_code_dict',NULL,35,'1',1,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (77,'新增',2,NULL,'spbiz:cardCodeDict:create;spbiz:cardCodeDict:save',76,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (78,'修改',2,NULL,'spbiz:cardCodeDict:update;spbiz:cardCodeDict:save',76,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (79,'删除',2,NULL,'spbiz:cardCodeDict:del',76,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (80,'保存',2,'/spbiz/cardCodeDict/save/','spbiz:cardCodeDict:save',76,'1',0,NULL,0);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (81,'查看详情',2,'dpInfo/dpFile/detail','dpcard:dpFile:detail',67,'1',0,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (82,'工具包',1,'views/dpcard/dp_tools',NULL,66,'1',10,'',1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (83,'卡片信息',1,'views/dpcard/dp_card',NULL,66,'1',2,NULL,1);
insert  into `pm_menu`(`id`,`view_name`,`type`,`url`,`perm`,`parent_id`,`status`,`idx`,`icon_class`,`show_flag`) values (84,'编辑',2,'business/application/editPage','business:application:editPage',68,'1',0,NULL,1);

/*Table structure for table `pm_role` */

CREATE TABLE `pm_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rolename` varchar(30) DEFAULT NULL,
  `roleintro` varchar(100) DEFAULT NULL,
  `c_time` datetime DEFAULT NULL,
  `m_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='ROLE';

/*Data for the table `pm_role` */

insert  into `pm_role`(`id`,`rolename`,`roleintro`,`c_time`,`m_time`) values (11,'admin','管理员','2018-08-07 17:59:11','2018-10-30 14:30:49');

/*Table structure for table `pm_role_menu` */

CREATE TABLE `pm_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `menu_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Reference_6` (`role_id`),
  KEY `FK_Reference_7` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=570 DEFAULT CHARSET=utf8;

/*Data for the table `pm_role_menu` */

insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (505,11,1);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (506,11,2);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (507,11,5);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (508,11,6);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (509,11,8);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (510,11,19);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (511,11,3);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (512,11,9);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (513,11,10);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (514,11,11);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (515,11,20);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (516,11,4);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (517,11,13);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (518,11,14);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (519,11,15);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (520,11,21);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (521,11,17);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (522,11,18);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (523,11,22);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (524,11,23);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (525,11,24);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (526,11,25);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (527,11,29);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (528,11,30);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (529,11,31);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (530,11,32);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (531,11,33);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (532,11,35);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (533,11,36);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (534,11,37);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (535,11,38);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (536,11,39);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (537,11,40);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (538,11,48);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (539,11,50);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (540,11,51);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (541,11,52);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (542,11,53);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (543,11,54);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (544,11,56);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (545,11,57);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (546,11,58);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (547,11,59);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (548,11,60);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (549,11,62);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (550,11,63);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (551,11,64);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (552,11,65);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (553,11,68);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (554,11,70);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (555,11,71);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (556,11,72);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (557,11,73);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (558,11,84);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (559,11,76);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (560,11,77);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (561,11,78);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (562,11,79);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (563,11,66);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (564,11,67);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (565,11,74);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (566,11,75);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (567,11,81);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (568,11,82);
insert  into `pm_role_menu`(`id`,`role_id`,`menu_id`) values (569,11,83);

/*Table structure for table `pm_user` */

CREATE TABLE `pm_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(128) NOT NULL COMMENT '用户名',
  `realname` varchar(128) DEFAULT NULL COMMENT '姓名',
  `password` varchar(255) DEFAULT NULL,
  `salt` varchar(64) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL COMMENT 'email',
  `jobuuid` varchar(64) DEFAULT NULL,
  `department_id` int(11) DEFAULT NULL COMMENT '部门',
  `c_time` datetime DEFAULT NULL,
  `m_time` datetime DEFAULT NULL,
  `last_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `warehouses` varchar(128) DEFAULT NULL COMMENT '所属仓库',
  `status` varchar(32) DEFAULT NULL COMMENT '状态 1:正常;0:删除',
  `phone` varchar(25) DEFAULT NULL COMMENT '联系方式',
  `home_page` varchar(128) DEFAULT NULL COMMENT '首页显示',
  `bg_style` varchar(500) DEFAULT NULL,
  `dac_group` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Data for the table `pm_user` */

insert  into `pm_user`(`id`,`username`,`realname`,`password`,`salt`,`email`,`jobuuid`,`department_id`,`c_time`,`m_time`,`last_time`,`warehouses`,`status`,`phone`,`home_page`,`bg_style`,`dac_group`) values (1,'admin','admin','8716419CABEEE9E5038178A4AB130E70','5B76D10D95B931B0','123@qq.com','a8add74fffec4a2e9e567a0a11b13e8a',NULL,'2017-05-07 15:12:06','2020-07-14 11:49:48','2020-07-14 11:49:49',NULL,'1','18673697511',NULL,'{\"navbar\":\"bg-primary navbar-dark\",\"logo\":\"bg-primary\",\"sidebar\":\"sidebar-dark-primary\",\"borderBottom\":true}','1,2');
insert  into `pm_user`(`id`,`username`,`realname`,`password`,`salt`,`email`,`jobuuid`,`department_id`,`c_time`,`m_time`,`last_time`,`warehouses`,`status`,`phone`,`home_page`,`bg_style`,`dac_group`) values (13,'Tom2',NULL,NULL,NULL,NULL,NULL,NULL,'2019-11-14 11:17:19',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

/*Table structure for table `pm_user_role` */

CREATE TABLE `pm_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Reference_1` (`user_id`),
  KEY `FK_Reference_2` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

/*Data for the table `pm_user_role` */

insert  into `pm_user_role`(`id`,`user_id`,`role_id`) values (12,22,11);

/*Table structure for table `sequence` */

CREATE TABLE `sequence` (
  `seq_name` varchar(50) NOT NULL DEFAULT '',
  `current_val` int(11) DEFAULT NULL,
  `increment_val` int(11) DEFAULT NULL,
  `min_val` int(11) DEFAULT NULL COMMENT '最小值',
  `max_val` int(11) DEFAULT NULL COMMENT '最大值',
  PRIMARY KEY (`seq_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Data for the table `sequence` */

insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('CITY_UNION_TERMINAL_TRANS_NO',880,1,1,999999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('HAINAN_TERMINAL_TRANS_NO',732,1,1,999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('HBEI_TERMINAL_TRANS_NO',198886,1,1,999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('HEFEI_TERMINAL_TRANS_NO',194,1,1,999999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('HUAWEI_SEQ_ID',56690,1,1,65535);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('JILIN_TERMINAL_TRANS_NO',184475,1,1,999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('NINGBO_TERMINAL_TRANS_NO',1,1,1,999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('QINGDAO_TERMINAL_TRANS_NO',1776,1,1,999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('SHENYANG_TERMINAL_TRANS_NO',236,1,1,999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('SHENZHENCZ_TERMINAL_TRANS_NO',1,1,1,999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('SHENZHEN_TERMINAL_TRANS_NO',17,1,1,99);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('SHIJIAZHUANG_TERMINAL_TRANS_NO',2379,1,1,999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('SUZHOU_TERMINAL_TRANS_NO',3857,1,1,999999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('S_CHECK_ID',329,1,1,999999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('S_PKG_ID',22,1,1,999999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('S_REC_DATA_ID',61,1,1,999999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('S_TERMINAL_TRANS_NO',9822,1,1,999999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('WORKER_ID_NO',269,1,1,65535);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('WULUMUQI_TERMINAL_TRANS_NO',239,1,1,999999);
insert  into `sequence`(`seq_name`,`current_val`,`increment_val`,`min_val`,`max_val`) values ('XIAN_TERMINAL_TRANS_NO',17181,1,1,999999999);

/* Function  structure for function  `currval` */

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `currval`(v_seq_name VARCHAR(50)) RETURNS int(11)
BEGIN          
    DECLARE VALUE INT;           
    SET VALUE = 0;           
    SELECT current_val INTO VALUE  FROM sequence WHERE seq_name = v_seq_name;     
   RETURN VALUE;     
END */$$
DELIMITER ;

/* Function  structure for function  `getSequenceValue` */

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `getSequenceValue`(v_seq_name VARCHAR(50)) RETURNS int(11)
BEGIN 
DECLARE maxVal,minVal,currentVal,incrementVal INT;
SELECT max_val,min_val,current_val,increment_val INTO maxVal,minVal,currentVal,incrementVal FROM sequence WHERE seq_name = v_seq_name FOR UPDATE;
SET currentVal = currentVal + incrementVal;
IF currentVal > maxVal THEN SET currentVal = minVal;
END IF;
UPDATE sequence SET current_val = currentVal 
WHERE seq_name = v_seq_name; 
RETURN currval(v_seq_name); 
END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
