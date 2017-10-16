
DROP TABLE IF EXISTS `rb_jdchannel`;
CREATE TABLE `rb_jdchannel` (
  `channelId` int(11) NOT NULL AUTO_INCREMENT,
  `jdChannelId` int(11) NOT NULL COMMENT '京东推广位ID',
  `unionId` bigint(11) NOT NULL COMMENT '联盟ID',
  `spaceName` varchar(50) NOT NULL COMMENT '推广位名称',
  `jdCreateTime` datetime NOT NULL,
  `clickCount` int(11) DEFAULT NULL COMMENT '30天点击',
  `orderCount` int(11) DEFAULT NULL COMMENT '30天有效订单',
  `orderPrice` double(15,2) DEFAULT NULL COMMENT '30天有效引入订单金额',
  `orderCommission` double(15,2) DEFAULT NULL COMMENT '30天预估收入',
  `createDate` datetime DEFAULT NULL COMMENT '本地创建时间',
  PRIMARY KEY (`channelId`),
  UNIQUE KEY `jdChannelId` (`jdChannelId`) USING BTREE,
  KEY `unionId` (`unionId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=198 DEFAULT CHARSET=utf8;


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
  `isValid` int(1) DEFAULT '0' COMMENT '1、采集群、2、群发',
  `isPutaway` tinyint(1) NOT NULL DEFAULT '1' COMMENT '微信登录获取有效群，退出群失效，默认有效',
  `channelId` int(11) DEFAULT '0' COMMENT '京东推广位主键',
  PRIMARY KEY (`groupId`),
  KEY `Uin` (`Uin`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `rb_linksmsg`;
CREATE TABLE `rb_linksmsg` (
  `linksMsgId` int(11) NOT NULL AUTO_INCREMENT COMMENT '转链接后的表记录',
  `listenerMsgId` int(11) DEFAULT NULL COMMENT '监听消息主键',
  `unionId` bigint(15) NOT NULL COMMENT '京东联盟ID',
  `skuId` bigint(15) NOT NULL,
  `adminId` int(11) DEFAULT NULL,
  `jdChannelId` int(11) DEFAULT '0' COMMENT '推广位主键',
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
  `isSelf` tinyint(1) DEFAULT NULL COMMENT '是否是自动转链的商品',
  `statusType` int(2) DEFAULT '1' COMMENT '发送状态：1、待发送，2、发成功、-1、失败',
  `createDate` datetime NOT NULL,
  PRIMARY KEY (`linksMsgId`),
  KEY `unionId` (`unionId`) USING BTREE,
  KEY `adminId` (`adminId`) USING BTREE,
  KEY `listenerMsgId` (`listenerMsgId`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


