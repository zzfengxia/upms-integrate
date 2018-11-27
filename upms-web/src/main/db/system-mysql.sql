CREATE TABLE `pm_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dict_name` varchar(128) DEFAULT NULL,
  `dict_type` varchar(128) NOT NULL COMMENT '缓存key',
  `dict_key` varchar(128) DEFAULT NULL COMMENT 'hash key',
  `dict_val` varchar(128) DEFAULT NULL COMMENT 'hash value',
  `dict_ord` int(10) DEFAULT '1',
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `pm_dict` */

insert  into `pm_dict`(`id`,`dict_name`,`dict_type`,`dict_key`,`dict_val`,`dict_ord`,`remark`) values (5,'字典缓存时间','config:server:hash','sys:cacheTime',NULL,1,'单位:ms');

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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

/*Data for the table `pm_menu` */

insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('1','用户','0',NULL,NULL,'0','1','1','nav-icon fa fa-user','1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('2','管理员账户','1','views/system/user','user:show','1','1','1',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('3','功能菜单','1','views/system/menu','menu:show','1','1','2',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('4','角色管理','1','views/system/role','role:show','1','1','3',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('5','新增','2',NULL,'system:user:create;system:user:save','2','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('6','修改','2',NULL,'system:user:update;system:user:save','2','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('7','保存','2','/admin/user/save/','system:user:save','2','1','0',NULL,'0');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('8','删除','2',NULL,'system:user:del','2','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('9','新增','2',NULL,'menu:create;menu:save','3','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('10','修改','2',NULL,'menu:update;menu:save','3','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('11','删除','2',NULL,'menu:del','3','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('12','保存','2','/menu/save/','menu:save','3','1','0',NULL,'0');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('13','新增','2',NULL,'role:create;role:save','4','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('14','修改','2',NULL,'role:update;role:save','4','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('15','删除','2',NULL,'role:del','4','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('16','保存','2','/role/save/','role:save','4','1','0',NULL,'0');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('17','系统','0',NULL,NULL,'0','1','2','nav-icon fa fa-cog','1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('18','数据字典','1','views/system/dict','dict:show','17','1','1',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('19','查询','2','/admin/user/list','user:list','2','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('20','查询','2','/menu/list','menu:list','3','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('21','查询','2','/role/list','role:list','4','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('22','查询','2','/dict/list','dict:list','18','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('23','新增','2',NULL,'dict:create;dict:save','18','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('24','修改','2',NULL,'dict:update;dict:save','18','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('25','删除','2',NULL,'dict:del','18','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('26','保存','2','/dict/save','dict:save','18','1','0',NULL,'0');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('27','重置密码','2',NULL,'user:reset:pwd','2','1','0',NULL,'0');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('28','SQL监控','1','druid/sql.html','druid:state','17','1','2',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('29','系统参数','1','views/system/params','config:list','17','1','3',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('30','新增','2',NULL,'config:create;config:save','29','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('31','修改','2',NULL,'config:update;config:save','29','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('32','删除','2',NULL,'config:del','29','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('33','查询','2','/system/config/list','config:list','29','1','0',NULL,'1');
insert into `pm_menu` (`id`, `view_name`, `type`, `url`, `perm`, `parent_id`, `status`, `idx`, `icon_class`, `show_flag`) values('34','保存','2','/system/config/save/','config:save','29','1','0',NULL,'0');

/*Table structure for table `pm_role` */

CREATE TABLE `pm_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rolename` varchar(30) DEFAULT NULL,
  `roleintro` varchar(100) DEFAULT NULL,
  `c_time` datetime DEFAULT NULL,
  `m_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='ROLE';

/*Data for the table `pm_role` */

/*Table structure for table `pm_role_menu` */

CREATE TABLE `pm_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `menu_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Reference_6` (`role_id`),
  KEY `FK_Reference_7` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

/*Data for the table `pm_role_menu` */

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
  `bg_style` varchar(500) DEFAULT NULL COMMENT '主题',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

/*Data for the table `pm_user` */

insert  into `pm_user`(`id`,`username`,`realname`,`password`,`salt`,`email`,`jobuuid`,`department_id`,`c_time`,`m_time`,`last_time`,`warehouses`,`status`,`phone`,`home_page`) values (1,'admin','admin','8716419CABEEE9E5038178A4AB130E70','5B76D10D95B931B0','123@qq.com','a8add74fffec4a2e9e567a0a11b13e8a',NULL,'2017-05-07 15:12:06','2018-08-06 14:33:19','2018-08-06 14:33:19',NULL,'1','18673697511',NULL);

/*Table structure for table `pm_user_role` */

CREATE TABLE `pm_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Reference_1` (`user_id`),
  KEY `FK_Reference_2` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

CREATE TABLE `pm_config_params` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `param_key` varchar(125) NOT NULL COMMENT '参数KEY',
  `param_value` text COMMENT '参数值',
  `status` tinyint(1) DEFAULT '1' COMMENT '1:正常;0:禁用',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `c_time` datetime DEFAULT NULL,
  `m_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key` (`param_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8

CREATE TABLE `cai_piao_history` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8