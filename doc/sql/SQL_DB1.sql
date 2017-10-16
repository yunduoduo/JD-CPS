
CREATE DATABASE `robot` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci; 

ALTER DATABASE robot DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `rb_delay_send_goods`;
CREATE TABLE `rb_delay_send_goods` (
  `delayGoodsId` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品延迟推送接口',
  `adminId` int(11) NOT NULL,
  `UserName` varchar(50) DEFAULT NULL COMMENT '用户帐号',
  `DelayID` varchar(50) DEFAULT NULL,
  `GoodsID` int(11) DEFAULT NULL COMMENT '商品ID',
  `DelayTime` datetime DEFAULT NULL COMMENT '延迟时间',
  `isValid` tinyint(1) DEFAULT '0',
  `createDate` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`delayGoodsId`),
  KEY `GoodsID` (`GoodsID`) USING BTREE,
  KEY `adminId` (`adminId`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS `rb_jdpermission`;
CREATE TABLE `rb_jdpermission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `loginname` varchar(50) DEFAULT NULL,
  `unionId` bigint(15) DEFAULT NULL,
  `JD_Appkey` varchar(255) NOT NULL,
  `JD_AppSecret` varchar(255) NOT NULL,
  `JD_AccessToken` varchar(255) DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL COMMENT '刷新token',
  `expires_in` int(11) DEFAULT NULL COMMENT '过期时间（秒）',
  `tokenValidTime` int(11) NOT NULL DEFAULT '0' COMMENT '到这个时间过期',
  `time` varchar(50) DEFAULT NULL,
  `token_type` varchar(50) DEFAULT NULL,
  `uid` varchar(50) DEFAULT NULL,
  `user_nick` varchar(50) DEFAULT NULL,
  `createDate` datetime NOT NULL,
  `upTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `unionId` (`unionId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `rb_jdpermission` VALUES ('1', 'bailingclub', '1000249047', 'B08F87444183D31422461A010DC6E7F2', '94c5d77869e94ce99dace22e94cb0f05', '5d1045ab-e95f-4254-88c6-b6c35581f3bf', '063432de-c38c-404b-b5e2-d4205672f600', '86399', '1503371164', '1503284901638', 'bearer', '9917490527', 'bailingclub', '2017-08-08 09:37:52', '2017-08-21 11:06:06');

DROP TABLE IF EXISTS `rb_jd_importorders`;
CREATE TABLE `rb_jd_importorders` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '引入订单数据',
  `parentId` bigint(15) DEFAULT NULL COMMENT '父单ID,父单拆分后,子单中parentId为父单的id;',
  `subUnionId` bigint(15) NOT NULL COMMENT '联盟Id',
  `channelUnionId` bigint(15) DEFAULT NULL COMMENT '渠道子联盟ID',
  `spName` varchar(50) DEFAULT NULL COMMENT '渠道名称',
  `balance` int(5) DEFAULT NULL COMMENT '1:已结算，2：未结算; ',
  `balanceName` varchar(50) DEFAULT NULL COMMENT '结算状态（未结算，已结算）',
  `commission` double(10,2) DEFAULT NULL COMMENT '佣金(cosPrice * commissionRate); ',
  `cosPrice` double(10,2) DEFAULT NULL COMMENT '用于佣金计算的基础值',
  `finishTime` datetime DEFAULT NULL COMMENT '完成时间',
  `orderId` bigint(15) NOT NULL COMMENT '京东订单号',
  `sourceEmt` int(5) DEFAULT NULL COMMENT '下单设备 1.pc 2.无线; ',
  `splitType` int(5) DEFAULT NULL COMMENT '拆分类型固定为3;',
  `totalMoney` double(10,2) DEFAULT NULL COMMENT '订单金额（会员价 包含优惠，不含运费,是各个商品会员价的总和); ',
  `yn` int(2) DEFAULT NULL COMMENT '订单取消标识，1：买家未取消，0：买家已取消;',
  `plus` int(2) DEFAULT NULL COMMENT '是否plus会员 1是，0不是;',
  `orderTime` datetime DEFAULT NULL COMMENT '下单时间',
  `popId` int(11) DEFAULT NULL COMMENT '商家ID',
  `splitName` varchar(50) DEFAULT NULL COMMENT '是否拆单状态',
  `orderStatusName` varchar(50) DEFAULT NULL COMMENT '订单状态',
  `createDate` datetime NOT NULL,
  `upTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `orderId` (`orderId`) USING BTREE,
  KEY `subUnionId` (`subUnionId`) USING BTREE,
  KEY `channelUnionId` (`channelUnionId`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_jd_importorder_skus`;
CREATE TABLE `rb_jd_importorder_skus` (
  `skusId` int(11) NOT NULL AUTO_INCREMENT COMMENT '引入订单中商品数据',
  `importOrderId` int(11) NOT NULL COMMENT '引入订单数据主键',
  `orderId` bigint(15) DEFAULT NULL COMMENT '商品订单Id',
  `subUnionId` bigint(20) DEFAULT '0' COMMENT '子联盟ID',
  `commission` double(10,2) DEFAULT NULL COMMENT 'commission:佣金(cosPrice * commissionRate)',
  `commissionRate` double(10,2) DEFAULT NULL COMMENT '佣金比例;',
  `subsideProportion` double(10,2) DEFAULT NULL COMMENT '分成比例 ',
  `cosPrice` double(10,2) DEFAULT NULL COMMENT '用于佣金计算的基础值; ',
  `firstLevel` int(11) DEFAULT NULL COMMENT '一级类目',
  `price` double(10,2) DEFAULT NULL,
  `secondLevel` int(11) DEFAULT NULL COMMENT '二级类目',
  `skuId` bigint(15) DEFAULT NULL COMMENT '商品ID;',
  `skuName` varchar(255) DEFAULT NULL COMMENT '商品名称;',
  `skuPrice` double(10,2) DEFAULT NULL COMMENT '商品金额',
  `skuNum` int(5) DEFAULT NULL COMMENT '商品数量',
  `skuReturnNum` int(5) DEFAULT NULL COMMENT '商品退货数量',
  `subSideRate` double(10,2) DEFAULT NULL COMMENT '分佣方的分佣比例(收入分成比例)',
  `subsidyRate` double(10,2) DEFAULT NULL COMMENT '平台分成比例',
  `thirdLevel` int(11) DEFAULT NULL COMMENT '三级类目',
  `valid` int(3) DEFAULT NULL COMMENT 'sku有效标识，1：有效，2：无效 ',
  `wareId` bigint(15) DEFAULT NULL COMMENT '款ID',
  `splitName` varchar(50) DEFAULT NULL COMMENT '是否拆单 （正常）',
  `ruleType` int(5) DEFAULT NULL,
  `orderStatusName` varchar(50) DEFAULT NULL COMMENT '订单状态',
  `balance` int(11) DEFAULT NULL COMMENT '1:已结算，2：未结算; ',
  `careteDate` datetime DEFAULT NULL,
  PRIMARY KEY (`skusId`),
  KEY `importOrderId` (`importOrderId`) USING BTREE,
  KEY `subUnionId` (`subUnionId`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_linksmsg`;
CREATE TABLE `rb_linksmsg` (
  `linksMsgId` int(11) NOT NULL AUTO_INCREMENT COMMENT '转链接后的表记录',
  `listenerMsgId` int(11) DEFAULT NULL COMMENT '监听消息主键',
  `unionId` bigint(15) NOT NULL COMMENT '京东联盟ID',
  `skuId` bigint(15) NOT NULL,
  `adminId` int(11) DEFAULT NULL,
  `jdChannelId` int(11) DEFAULT NULL COMMENT '推广位主键',
  `contents` longtext NOT NULL COMMENT '微信采集的内容',
  `GoodsPict` varchar(255) DEFAULT NULL COMMENT '微信采集的图片',
  `linkUrl` varchar(255) DEFAULT NULL COMMENT '转链接后下单地址',
  `goodsName` varchar(255) DEFAULT NULL COMMENT '通过skuid获取接口名称',
  `unitPrice` double(10,2) DEFAULT '0.00' COMMENT '商品单价即京东价',
  `imgUrl` varchar(255) DEFAULT NULL COMMENT '京东接口获取的图片',
  `wlUnitPrice` double(10,2) DEFAULT NULL COMMENT '商品无线京东价（单价为-1表示未查询到改商品单价）',
  `commisionRatioPc` double(10,2) DEFAULT NULL COMMENT 'PC佣金比例',
  `commisionRatioWl` double(10,2) DEFAULT NULL COMMENT '无线佣金比例',
  `shopId` int(11) DEFAULT NULL COMMENT '店铺ID',
  `materialUrl` varchar(255) DEFAULT NULL COMMENT '商品落地页',
  `createDate` datetime NOT NULL,
  PRIMARY KEY (`linksMsgId`),
  KEY `unionId` (`unionId`) USING BTREE,
  KEY `adminId` (`adminId`) USING BTREE,
  KEY `listenerMsgId` (`listenerMsgId`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_listener_msg`;
CREATE TABLE `rb_listener_msg` (
  `listenerMsgId` int(11) NOT NULL AUTO_INCREMENT COMMENT '监听消息记录',
  `Uin` varchar(50) NOT NULL COMMENT '用户uin（用户标识）',
  `unionId` bigint(15) DEFAULT NULL COMMENT '京东联盟ID',
  `skuId` bigint(15) NOT NULL DEFAULT '0',
  `adminId` int(11) DEFAULT NULL COMMENT '登录用户',
  `channelId` int(11) DEFAULT NULL COMMENT '推广位主键',
  `NickName` varchar(100) DEFAULT NULL COMMENT '用户昵称',
  `FromUserName` varchar(255) NOT NULL COMMENT '群名称（变化的）',
  `GroupName` varchar(255) DEFAULT NULL COMMENT '群昵称',
  `content` longtext NOT NULL COMMENT '内容',
  `imgUrl` varchar(255) DEFAULT NULL COMMENT '图片地址',
  `isLinks` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已经转过连接',
  `createDate` datetime NOT NULL,
  PRIMARY KEY (`listenerMsgId`),
  KEY `unionId` (`unionId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_sendrecord`;
CREATE TABLE `rb_sendrecord` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品推送记录',
  `adminId` int(11) NOT NULL,
  `UserName` varchar(50) DEFAULT NULL COMMENT '用户帐号',
  `RecordID` varchar(50) DEFAULT NULL COMMENT '记录ID',
  `GoodsID` int(11) DEFAULT NULL COMMENT '商品ID',
  `SendWXCount` int(11) DEFAULT '0' COMMENT '推送微信群数量',
  `SuccessCount` int(11) DEFAULT '0' COMMENT '推送成功数量',
  `SendTime` datetime DEFAULT NULL COMMENT '推送时间',
  `createDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `adminId` (`adminId`) USING BTREE,
  KEY `GoodsID` (`GoodsID`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_user_log_record`;
CREATE TABLE `rb_user_log_record` (
  `logRecordId` int(11) NOT NULL AUTO_INCREMENT COMMENT '日志写入接口',
  `adminId` int(11) NOT NULL,
  `UserName` varchar(50) NOT NULL COMMENT '用户帐号',
  `IPAddress` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `LogTime` datetime DEFAULT NULL COMMENT '日志时间',
  `LogInfo` longtext COMMENT '日志内容',
  `LogType` varchar(50) DEFAULT NULL COMMENT '日志类型',
  `createDate` datetime DEFAULT NULL,
  PRIMARY KEY (`logRecordId`),
  KEY `adminId` (`adminId`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_wxjduser`;
CREATE TABLE `rb_wxjduser` (
  `accountsId` int(11) NOT NULL AUTO_INCREMENT COMMENT '京东用户',
  `adminId` int(11) DEFAULT NULL COMMENT '平台adminId',
  `unionId` bigint(15) DEFAULT NULL COMMENT '京东联盟Id',
  `loginname` varchar(50) NOT NULL COMMENT '帐户名',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `cookie` varchar(1000) NOT NULL COMMENT 'cookie',
  `createDate` datetime NOT NULL,
  `upTime` datetime DEFAULT NULL,
  `uid` varchar(100) DEFAULT NULL COMMENT '授权用户对应的京东ID',
  `user_nick` varchar(100) DEFAULT NULL COMMENT '授权用户对应的京东昵称',
  `expires_in` int(15) DEFAULT NULL COMMENT 'access_token失效时间（从当前时间算起，单位：秒）',
  `access_token` varchar(100) DEFAULT NULL COMMENT '京东授权token',
  `refresh_token` varchar(100) DEFAULT NULL,
  `isValidToken` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'token是否有效',
  `tokenValidTime` int(15) NOT NULL DEFAULT '0' COMMENT 'token过期时间（秒）',
  PRIMARY KEY (`accountsId`),
  KEY `unionId` (`unionId`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_wxsendtimes`;
CREATE TABLE `rb_wxsendtimes` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '群发时间参数',
  `adminId` int(11) NOT NULL COMMENT '登录主键',
  `GoodsSendTims` bigint(15) DEFAULT NULL COMMENT '商品发送时间间隔',
  `PicSendTims` bigint(15) DEFAULT NULL COMMENT '商品图片发送时间间隔',
  `WXSendTimes` bigint(15) DEFAULT NULL COMMENT '微信群发送时间间隔',
  `createDate` datetime NOT NULL,
  `upTime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_wxuser`;
CREATE TABLE `rb_wxuser` (
  `wxUserId` int(11) NOT NULL AUTO_INCREMENT COMMENT '微信登录用户',
  `Uin` bigint(15) NOT NULL COMMENT 'Uin用户唯一标识',
  `adminId` int(11) DEFAULT NULL COMMENT '平台用户Id',
  `userNickName` varchar(100) NOT NULL COMMENT '昵称',
  `UserName` varchar(100) NOT NULL COMMENT '用户名称',
  `HeadImgUrl` varchar(255) DEFAULT NULL COMMENT '头像',
  `isValid` tinyint(1) NOT NULL DEFAULT '0',
  `createDate` datetime NOT NULL,
  `upTime` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`wxUserId`),
  UNIQUE KEY `Uin` (`Uin`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_wxusergather`;
CREATE TABLE `rb_wxusergather` (
  `gatherId` int(11) NOT NULL AUTO_INCREMENT COMMENT '采集群记录表',
  `Uin` int(11) NOT NULL,
  `uNickName` varchar(255) DEFAULT NULL COMMENT '微信昵称',
  `gNickName` varchar(255) DEFAULT NULL COMMENT '群昵称',
  `isEffect` tinyint(1) NOT NULL DEFAULT '0',
  `createDate` datetime DEFAULT NULL,
  `upTime` datetime DEFAULT NULL,
  PRIMARY KEY (`gatherId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_wxusergroup`;
CREATE TABLE `rb_wxusergroup` (
  `groupId` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户群',
  `Uin` int(11) NOT NULL COMMENT '用户Uin',
  `groupUin` int(11) DEFAULT NULL COMMENT '群Uin',
  `toUserName` varchar(255) NOT NULL COMMENT '群名称',
  `NickName` varchar(255) NOT NULL COMMENT '群昵称',
  `base_uri` varchar(1000) DEFAULT NULL,
  `cookies` varchar(1000) DEFAULT NULL,
  `createDate` datetime NOT NULL,
  `upTime` datetime DEFAULT NULL COMMENT '修改时间',
  `isValid` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是有效才集群',
  `isPutaway` tinyint(1) NOT NULL DEFAULT '1' COMMENT '微信登录获取有效群，退出群失效，默认有效',
  PRIMARY KEY (`groupId`),
  KEY `Uin` (`Uin`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_wxwechatmeta`;
CREATE TABLE `rb_wxwechatmeta` (
  `wxMetaId` int(11) NOT NULL AUTO_INCREMENT,
  `Uin` int(11) NOT NULL,
  `base_uri` varchar(1000) DEFAULT NULL,
  `redirect_uri` varchar(1000) DEFAULT NULL,
  `webpush_url` varchar(1000) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `skey` varchar(255) DEFAULT NULL,
  `synckey` varchar(255) DEFAULT NULL,
  `wxsid` varchar(255) DEFAULT NULL,
  `wxuin` varchar(100) DEFAULT NULL,
  `pass_ticket` varchar(255) DEFAULT NULL,
  `deviceId` varchar(100) DEFAULT NULL,
  `cookie` varchar(1000) DEFAULT NULL,
  `baseRequest` longtext,
  `OSyncKey` longtext,
  `User` longtext,
  `createDate` datetime DEFAULT NULL,
  `upTime` datetime NOT NULL,
  PRIMARY KEY (`wxMetaId`),
  KEY `Uin` (`Uin`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rc_admin`;
CREATE TABLE `rc_admin` (
  `adminId` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `account` varchar(16) NOT NULL,
  `password` varchar(32) NOT NULL,
  `createDate` datetime NOT NULL,
  `lastLoginDate` datetime DEFAULT NULL,
  `lastLoginIp` varchar(64) DEFAULT NULL,
  `isValid` tinyint(1) NOT NULL,
  PRIMARY KEY (`adminId`),
  UNIQUE KEY `account` (`account`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
INSERT INTO `rc_admin` VALUES ('1', '1', 'admin', 'admin', '2015-02-04 13:33:56', '2017-08-22 09:51:25', '0:0:0:0:0:0:0:1', '1');

DROP TABLE IF EXISTS `rc_adminRole`;
CREATE TABLE `rc_adminRole` (
  `roleId` int(11) NOT NULL AUTO_INCREMENT,
  `roleName` varchar(20) NOT NULL,
  `isRoot` tinyint(1) NOT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
INSERT INTO `rc_adminRole` VALUES ('1', '管理员', '1');
INSERT INTO `rc_adminRole` VALUES ('2', '个人用户', '0');

DROP TABLE IF EXISTS `rc_permission`;
CREATE TABLE `rc_permission` (
  `permissionId` int(11) NOT NULL AUTO_INCREMENT,
  `permissionName` varchar(20) NOT NULL,
  `viewUrl` varchar(200) NOT NULL,
  PRIMARY KEY (`permissionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rc_rolePermissionRelation`;
CREATE TABLE `rc_rolePermissionRelation` (
  `relationId` int(11) NOT NULL AUTO_INCREMENT,
  `roleId` int(11) NOT NULL,
  `permissionId` int(11) NOT NULL,
  PRIMARY KEY (`relationId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tmp_flocklist`;
CREATE TABLE `tmp_flocklist` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '临时存储用户群列表',
  `Uin` varchar(50) NOT NULL,
  `toUserName` varchar(255) NOT NULL COMMENT '来自于哪个用户',
  `NickName` varchar(255) NOT NULL COMMENT '用户昵称',
  `flockList` longtext NOT NULL COMMENT '群列表信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

alter table rc_admin add JDtemporaryId int(11) default 0 ;
alter table rb_wxjduser  add JXJunionId bigint(15)  default 0 ;

DROP TABLE IF EXISTS `rb_user_logs`;
CREATE TABLE `rb_user_logs` (
  `logId` int(11) NOT NULL AUTO_INCREMENT COMMENT '后台日志',
  `adminId` int(11) NOT NULL COMMENT '帐号主键',
  `userName` varchar(50) DEFAULT NULL COMMENT '用户名',
  `IPAddress` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `logTypeId` int(11) NOT NULL COMMENT '日志类型',
  `logInfo` longtext NOT NULL COMMENT '日志描述',
  `createDate` datetime NOT NULL COMMENT '日志时间',
  PRIMARY KEY (`logId`),
  KEY `adminId` (`adminId`) USING BTREE,
  KEY `logTypeId` (`logTypeId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_logtype`;
CREATE TABLE `rb_logtype` (
  `logTypeId` int(11) NOT NULL AUTO_INCREMENT,
  `typeName` varchar(50) NOT NULL,
  PRIMARY KEY (`logTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `rb_logtype` VALUES ('1', '群发时间修改');
INSERT INTO `rb_logtype` VALUES ('2', '修改信息');
INSERT INTO `rb_logtype` VALUES ('3', '管理用户');
