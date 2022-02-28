/*
 Navicat Premium Data Transfer

 Source Server         : localhost_mysql
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 127.0.0.1:3306
 Source Schema         : lms_new

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 28/02/2022 17:13:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for databasechangelog
-- ----------------------------
DROP TABLE IF EXISTS `databasechangelog`;
CREATE TABLE `databasechangelog` (
  `ID` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `AUTHOR` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `FILENAME` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) COLLATE utf8mb4_bin NOT NULL,
  `MD5SUM` varchar(35) COLLATE utf8mb4_bin DEFAULT NULL,
  `DESCRIPTION` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `COMMENTS` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `TAG` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `LIQUIBASE` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL,
  `CONTEXTS` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `LABELS` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) COLLATE utf8mb4_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for databasechangeloglock
-- ----------------------------
DROP TABLE IF EXISTS `databasechangeloglock`;
CREATE TABLE `databasechangeloglock` (
  `ID` int(11) NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of databasechangeloglock
-- ----------------------------
BEGIN;
INSERT INTO `databasechangeloglock` VALUES (1, b'0', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_application
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_application`;
CREATE TABLE `easyoa_application` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dept_id` int(11) NOT NULL COMMENT '部门ID',
  `dept_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '部门名称',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户名',
  `position` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '职位',
  `leave_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '请假类型 年休假，病假，婚假，丧假，陪产假，产检假，调休假，事假',
  `applicate_type` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '申请类型：请假/销假',
  `start_time` datetime NOT NULL COMMENT '休假开始时间',
  `end_time` datetime NOT NULL COMMENT '休假结束时间',
  `days` double(4,1) DEFAULT NULL COMMENT '请假天数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `leave_reason` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '请假原因',
  `status` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '已申请/待审核/已通过/已拒绝',
  `remark` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注说明',
  `resources` bigint(20) DEFAULT NULL COMMENT '上传资料的id',
  `stage` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '当前处于的审核环节',
  `previous_days` double(4,1) DEFAULT NULL COMMENT '使用上年年假',
  `company_id` int(11) DEFAULT NULL COMMENT '公司id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_application
-- ----------------------------

-- ----------------------------
-- Table structure for easyoa_application_flow
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_application_flow`;
CREATE TABLE `easyoa_application_flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` bigint(20) NOT NULL COMMENT '申请单ID',
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '流程名称',
  `type` int(2) NOT NULL COMMENT '流程类型 ：流转/结束/终止',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `assignee` bigint(20) DEFAULT NULL COMMENT '受理人',
  `assignee_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '受理人名称',
  `level` int(4) DEFAULT NULL COMMENT '流程层级',
  `status` tinyint(1) DEFAULT NULL COMMENT '流程状态：通过/拒绝',
  `flow_id` int(11) DEFAULT NULL COMMENT '绑定某一个流程',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '流程修改时间',
  `content` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '流程内容',
  `continue_trans` tinyint(1) DEFAULT NULL COMMENT '流程是否需要继续流转',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=291 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_application_flow
-- ----------------------------

-- ----------------------------
-- Table structure for easyoa_calendar
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_calendar`;
CREATE TABLE `easyoa_calendar` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `date` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '时间',
  `avoid` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '忌讳',
  `animals_year` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '生肖年',
  `weekday` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '星期',
  `suit` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '适宜',
  `lunar_info` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '农历年+农历日期',
  `description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述',
  `status` int(2) DEFAULT NULL COMMENT '节假日/周末-1，非节假日/工作日-0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_calendar
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_calendar` VALUES (1, '2019-7-26', '嫁娶.移徙.入宅.', '猪', '星期五', '纳采.祭祀.祈福.解除.动土.破土.安葬.', '己亥年|六月廿四', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (2, '2019-7-27', '诸事不宜.', '猪', '星期六', '祭祀.破屋.坏垣.余事勿取.', '己亥年|六月廿五', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (3, '2019-7-28', '祭祀.祈福.', '猪', '星期日', '嫁娶.纳采.开市.出行.动土.上梁.移徙.入宅.破土.安葬.', '己亥年|六月廿六', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (4, '2019-7-29', '赴任.', '猪', '星期一', '嫁娶.纳采.开市.出行.动土.上梁.移徙.入宅.破土.安葬.', '己亥年|六月廿七', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (5, '2019-7-30', '开市.破土.', '猪', '星期二', '祭祀.作灶.纳财.捕捉.', '己亥年|六月廿八', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (6, '2019-7-31', '造庙.安葬.', '猪', '星期三', '嫁娶.开市.立券.祭祀.祈福.动土.移徙.入宅.', '己亥年|六月廿九', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (7, '2019-8-1', '诸事不宜.', '猪', '星期四', '补垣.塞穴.结网.入殓.除服.成服.移柩.安葬.启攒.余事勿取.', '己亥年|七月初一', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (8, '2019-8-2', '动土.安葬.', '猪', '星期五', '嫁娶.纳采.出行.祭祀.祈福.解除.移徙.入宅.', '己亥年|七月初二', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (9, '2019-8-3', '开市.入宅.', '猪', '星期六', '嫁娶.祭祀.祈福.斋醮.治病.破土.安葬.', '己亥年|七月初三', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (10, '2019-8-4', '祈福.动土.破土.', '猪', '星期日', '嫁娶.出行.开市.安床.入殓.启攒.安葬.', '己亥年|七月初四', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (11, '2019-8-5', '开仓.出货财.置产.安葬.动土.破土.掘井.栽种.', '猪', '星期一', '嫁娶.祭祀.裁衣.结网.冠笄.沐浴.', '己亥年|七月初五', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (12, '2019-8-6', '嫁娶.破土.置产.栽种.安葬.修坟.行丧.', '猪', '星期二', '入宅.移徙.安床.开光.祈福.求嗣.进人口.开市.交易.立券.出火.拆卸.修造.动土.', '己亥年|七月初六', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (13, '2019-8-7', '嫁娶.入宅.移徙.作灶.开市.交易.安门.栽种.', '猪', '星期三', '祭祀.解除.沐浴.整手足甲.入殓.移柩.破土.启攒.安葬.', '己亥年|七月初七', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (14, '2019-8-8', '开市.交易.入宅.嫁娶.', '猪', '星期四', '祭祀.普渡.捕捉.解除.结网.畋猎.入殓.破土.安葬.', '己亥年|七月初八', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (15, '2019-8-9', '斋醮.开市.', '猪', '星期五', '沐浴.破屋.坏垣.余事勿取.', '己亥年|七月初九', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (16, '2019-8-10', '动土.破土.嫁娶.掘井.安床.', '猪', '星期六', '订盟.纳采.祭祀.祈福.安香.出火.开市.立券.入宅.挂匾.造桥.启攒.安葬.', '己亥年|七月初十', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (17, '2019-8-11', '纳采.订盟.经络.行丧.安葬.探病.', '猪', '星期日', '嫁娶.祭祀.祈福.斋醮.普渡.移徙.入宅.动土.治病.开市.交易.立券.开光.修造.造车器.安香.安床.捕捉.畋猎.结网.', '己亥年|七月十一', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (18, '2019-8-12', '掘井.出行.破土.行丧.安葬.', '猪', '星期一', '嫁娶.订盟.纳采.作灶.冠笄.裁衣.会亲友.纳畜.牧养.安机械.开市.立券.纳财.安床.', '己亥年|七月十二', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (19, '2019-8-13', '出火.入宅.造屋.安门.安葬.', '猪', '星期二', '嫁娶.订盟.纳采.祭祀.斋醮.普渡.解除.出行.会亲友.开市.纳财.修造.动土.竖柱.上梁.开光.开仓.出货财.纳畜.牧养.开池.破土.启攒.', '己亥年|七月十三', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (20, '2019-8-14', '动土.破土.掘井.开光.上梁.词讼.', '猪', '星期三', '嫁娶.普渡.祭祀.祈福.补垣.塞穴.断蚁.筑堤.入殓.除服.成服.安葬.', '己亥年|七月十四', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (21, '2019-8-15', '开市.动土.破土.安床.开仓.上梁.', '猪', '星期四', '嫁娶.冠笄.祭祀.沐浴.普渡.出行.纳财.扫舍.纳畜.赴任.', '己亥年|七月十五', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (22, '2019-8-16', '嫁娶.出行.入宅.开市.安门.', '猪', '星期五', '祭祀.沐浴.理发.整手足甲.冠笄.解除.入殓.移柩.破土.启攒.安葬.', '己亥年|七月十六', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (23, '2019-8-17', '祈福.开市.动土.行丧.安葬.', '猪', '星期六', '塑绘.冠笄.嫁娶.会亲友.进人口.经络.裁衣.栽种.纳畜.牧养.补垣.塞穴.捕捉.', '己亥年|七月十七', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (24, '2019-8-18', '嫁娶.安门.动土.安葬.', '猪', '星期日', '出行.沐浴.订盟.纳采.裁衣.竖柱.上梁.移徙.纳畜.牧养.', '己亥年|七月十八', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (25, '2019-8-19', '开市.立券.纳财.作灶.', '猪', '星期一', '纳采.订盟.嫁娶.祭祀.祈福.普渡.开光.安香.出火.移徙.入宅.竖柱.修造.动土.竖柱.上梁.起基.造屋.安门.造庙.造桥.破土.启攒.安葬.', '己亥年|七月十九', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (26, '2019-8-20', '嫁娶.纳采.订盟.开市.入宅.', '猪', '星期二', '祭祀.捕捉.畋猎.纳畜.牧养.入殓.除服.成服.移柩.破土.安葬.启攒.', '己亥年|七月廿十', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (27, '2019-8-21', '行丧.安葬.', '猪', '星期三', '破屋.坏垣.治病.余事勿取.', '己亥年|七月廿一', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (28, '2019-8-22', '动土.破土.订盟.安床.开池.', '猪', '星期四', '祈福.斋醮.出行.冠笄.嫁娶.雕刻.开柱眼.入宅.造桥.开市.交易.立券.纳财.入殓.除服.成服.移柩.破土.安葬.启攒.', '己亥年|七月廿二', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (29, '2019-8-23', '', '猪', '星期五', '祈福.求嗣.解除.订盟.纳采.动土.起基.放水.造仓.开市.纳畜.牧养.开生坟.入殓.除服.成服.移柩.破土.安葬.', '己亥年|七月廿三', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (30, '2019-8-24', '', '猪', '星期六', '塑绘.开光.解除.订盟.纳采.嫁娶.出火.修造.动土.移徙.入宅.拆卸.起基.安门.分居.开市.交易.立券.纳财.纳畜.牧养.', '己亥年|七月廿四', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (31, '2019-8-25', '', '猪', '星期日', '祈福.出行.订盟.纳采.嫁娶.裁衣.动土.安床.放水.开市.掘井.交易.立券.栽种.开渠.除服.成服.移柩.破土.', '己亥年|七月廿五', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (32, '2019-8-26', '动土.破土.', '猪', '星期一', '嫁娶.祭祀.祈福.斋醮.作灶.移徙.入宅.', '己亥年|七月廿六', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (33, '2019-8-27', '作灶.动土.破土.', '猪', '星期二', '嫁娶.出行.纳畜.祭祀.入殓.启攒.安葬.', '己亥年|七月廿七', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (34, '2019-8-28', '嫁娶.开市.', '猪', '星期三', '订盟.纳采.祭祀.祈福.修造.动土.上梁.破土.安葬.', '己亥年|七月廿八', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (35, '2019-8-29', '开市.安葬.', '猪', '星期四', '订盟.纳采.出行.会亲友.修造.上梁.移徙.入宅.', '己亥年|七月廿九', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (36, '2019-8-30', '嫁娶.祈福.余事勿取.', '猪', '星期五', '沐浴.修饰垣墙.平治道涂.余事勿取.', '己亥年|八月初一', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (37, '2019-8-31', '开市.安葬.', '猪', '星期六', '嫁娶.祭祀.祈福.斋醮.动土.移徙.入宅.', '己亥年|八月初二', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (38, '2019-9-1', '嫁娶.入宅.', '猪', '星期日', '捕捉.结网.入殓.破土.安葬.', '己亥年|八月初三', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (39, '2019-9-2', '诸事不宜.', '猪', '星期一', '沐浴.治病.破屋.坏垣.余事勿取.', '己亥年|八月初四', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (40, '2019-9-3', '动土.破土.', '猪', '星期二', '嫁娶.订盟.纳采.出行.开市.祭祀.祈福.移徙.入宅.启攒.安葬.', '己亥年|八月初五', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (41, '2019-9-4', '开光.针灸.', '猪', '星期三', '嫁娶.订盟.纳采.祭祀.祈福.求医.治病.动土.移徙.入宅.破土.安葬.', '己亥年|八月初六', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (42, '2019-9-5', '动土.安葬.', '猪', '星期四', '订盟.纳采.祭祀.祈福.安机械.作灶.纳畜.', '己亥年|八月初七', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (43, '2019-9-6', '入宅.作梁.安门.伐木.修造.上梁.入殓.造屋.', '猪', '星期五', '嫁娶.祭祀.祈福.求嗣.出行.动土.安床.掘井.破土.启攒.', '己亥年|八月初八', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (44, '2019-9-7', '造屋.开光.理发.造船.掘井.作灶.', '猪', '星期六', '嫁娶.祭祀.祈福.求嗣.出行.出火.拆卸.修造.移徙.动土.安床.入殓.破土.安葬.启攒.', '己亥年|八月初九', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (45, '2019-9-8', '嫁娶.开市.祭祀.祈福.斋醮.纳采.修坟.', '猪', '星期日', '冠笄.沐浴.出行.修造.动土.移徙.入宅.破土.安葬.', '己亥年|八月初十', NULL, 1);
INSERT INTO `easyoa_calendar` VALUES (46, '2019-9-9', '嫁娶.入宅.修造.动土.会亲友.破土.', '猪', '星期一', '祭祀.出行.', '己亥年|八月十一', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (47, '2019-9-10', '针灸.伐木.作梁.造庙.行丧.安葬.', '猪', '星期二', '嫁娶.订盟.纳采.祭祀.祈福.出行.修造.动土.移徙.入宅.', '己亥年|八月十二', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (48, '2019-9-11', '嫁娶.安葬.动土.造桥.', '猪', '星期三', '出行.开市.交易.立券.安机械.出火.上梁.移徙.', '己亥年|八月十三', NULL, 0);
INSERT INTO `easyoa_calendar` VALUES (49, '2019-9-12', '斋醮.嫁娶.移徙.出行.上梁.入宅.', '猪', '星期四', '祭祀.沐浴.修饰垣墙.平治道涂.余事勿取.', '己亥年|八月十四', NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_company
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_company`;
CREATE TABLE `easyoa_company` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_company
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_company` VALUES (2, '测试公司', '2022-02-19 08:42:30', '2022-02-20 08:42:30', 0);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_department
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_department`;
CREATE TABLE `easyoa_department` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dept_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '部门名称',
  `dept_type` smallint(6) NOT NULL COMMENT '部门类型',
  `center` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '所属中心',
  `level` smallint(6) NOT NULL COMMENT '部门等级',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `parent_id` int(11) DEFAULT NULL COMMENT '上级部门ID',
  `path` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '路径',
  `root` tinyint(1) DEFAULT NULL COMMENT '是否为顶级部门',
  `deleted` tinyint(1) DEFAULT NULL COMMENT '是否删除',
  `company_id` int(11) NOT NULL COMMENT '所属公司',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_department
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_department` VALUES (30, 'easyoa', 0, '', 0, '2019-07-25 16:44:03', '2022-02-21 12:16:38', -1, ' ', 0, 0, 2);
INSERT INTO `easyoa_department` VALUES (31, '研发部', 1, 'easyoa', 1, '2019-07-25 16:44:03', '2022-02-21 13:28:31', 30, ' /30', 0, 0, 2);
INSERT INTO `easyoa_department` VALUES (32, '行政人力部', 1, 'easyoa', 1, '2019-07-25 16:44:03', '2022-02-21 13:28:37', 30, ' /30', 0, 0, 2);
INSERT INTO `easyoa_department` VALUES (33, '财务部', 1, 'easyoa', 1, '2019-07-25 16:44:03', '2022-02-21 13:28:42', 30, ' /30', 0, 0, 2);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_dict
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_dict`;
CREATE TABLE `easyoa_dict` (
  `dict_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '字典id',
  `dict_key` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'key',
  `dict_value` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'value',
  `field_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字段名',
  `table_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表名',
  PRIMARY KEY (`dict_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_dict
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_dict` VALUES (1, '0', '男', 'sex', 'easyoa_user');
INSERT INTO `easyoa_dict` VALUES (2, '1', '女', 'sex', 'easyoa_user');
INSERT INTO `easyoa_dict` VALUES (3, '2', '保密', 'sex', 'easyoa_user');
INSERT INTO `easyoa_dict` VALUES (4, '1', '有效', 'status', 'easyoa_user');
INSERT INTO `easyoa_dict` VALUES (5, '0', '锁定', 'status', 'easyoa_user');
INSERT INTO `easyoa_dict` VALUES (6, '0', '菜单', 'type', 'easyoa_menu');
INSERT INTO `easyoa_dict` VALUES (7, '1', '按钮', 'type', 'easyoa_menu');
INSERT INTO `easyoa_dict` VALUES (30, '0', '正常', 'status', 'easyoa_job');
INSERT INTO `easyoa_dict` VALUES (31, '1', '暂停', 'status', 'easyoa_job');
INSERT INTO `easyoa_dict` VALUES (32, '0', '成功', 'status', 'easyoa_job_log');
INSERT INTO `easyoa_dict` VALUES (33, '1', '失败', 'status', 'easyoa_job_log');
COMMIT;

-- ----------------------------
-- Table structure for easyoa_global_apply_rules
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_global_apply_rules`;
CREATE TABLE `easyoa_global_apply_rules` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `range` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '<=3，<=7,>7',
  `days` int(4) NOT NULL COMMENT '天数1,3,14',
  `description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述请假天数需要提前多少天必须提交申请',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for easyoa_global_email
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_global_email`;
CREATE TABLE `easyoa_global_email` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `email` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '邮箱',
  `password` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `deleted` tinyint(1) DEFAULT NULL COMMENT '是否删除',
  `active` tinyint(1) DEFAULT NULL COMMENT '是否启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_global_email
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_global_email` VALUES (1, '6323*****@qq.com', '6b11be044058bee7c0f70073f0eb9935', 0, 1, '2019-08-05 15:13:18', '2019-08-05 15:13:21');
COMMIT;

-- ----------------------------
-- Table structure for easyoa_global_flow
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_global_flow`;
CREATE TABLE `easyoa_global_flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `root` int(2) DEFAULT NULL COMMENT '是否是根节点',
  `parent_id` int(11) DEFAULT NULL COMMENT '父节点',
  `deleted` tinyint(1) DEFAULT NULL COMMENT '是否删除',
  `total` int(11) DEFAULT NULL COMMENT '总层级',
  `level` int(11) DEFAULT NULL COMMENT '当前层级',
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务名称',
  `content` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请人，直接主管，部门经理，人力资源主管',
  `assignee_ids` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '绑定的受理人名单',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `company_id` int(11) DEFAULT NULL COMMENT '所属公司',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_global_flow
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_global_flow` VALUES (1, -1, 0, 0, NULL, NULL, '流程一', '员工休假流程', '27', '2019-07-24 00:00:00', '2019-07-24 00:00:00', 2);
INSERT INTO `easyoa_global_flow` VALUES (2, 1, 1, 0, NULL, NULL, '自动审核', '员工休假规则自动审核', '27', '2019-07-25 00:00:00', '2019-07-25 00:00:00', 2);
INSERT INTO `easyoa_global_flow` VALUES (3, 1, 2, 0, NULL, NULL, '一级审批', '员工休假规则一级审批', '36,37,39,44,49,59,27', '2019-07-24 00:00:00', '2022-02-28 00:00:00', 2);
INSERT INTO `easyoa_global_flow` VALUES (4, 1, 3, 0, NULL, NULL, '二级审批', '员工休假规则二级审批', '36', '2019-07-24 00:00:00', '2019-07-24 00:00:00', 2);
INSERT INTO `easyoa_global_flow` VALUES (5, 1, 4, 0, NULL, NULL, '人力资源审批', '员工休假规则人力资源审批', '39', '2019-07-23 00:00:00', '2019-08-03 00:00:00', 2);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_global_vacation
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_global_vacation`;
CREATE TABLE `easyoa_global_vacation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '节日名称',
  `festival` date DEFAULT NULL COMMENT '节日日期',
  `start_date` date DEFAULT NULL COMMENT '假期开始时间',
  `end_date` date DEFAULT NULL COMMENT '假期结束时间',
  `days` int(4) DEFAULT NULL COMMENT '假期持续时间',
  `description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '节假日描述',
  `advice` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '假期休假推荐',
  `finish` tinyint(1) DEFAULT NULL COMMENT '是否填写完整',
  `detail` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '1-放假 2-上班',
  `year` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_global_vacation
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_global_vacation` VALUES (33, '元旦', '2019-01-01', '2018-12-30', '2019-01-01', 3, '2018年12月30日至2019年1月1日放假调休，共3天。2018年12月29日（星期六）上班。', '拼假建议：2019年1月2日（周三）~2019年1月4日（周五）请假3天，可拼8天元旦小长假', 1, '2018-12-30,2018-12-31,2019-1-1', 2019);
INSERT INTO `easyoa_global_vacation` VALUES (34, '除夕', '2019-02-04', '2019-02-04', '2019-02-04', 1, '除夕', '农历腊月最后一天为除夕，即大年初一前夜，又称为年三十。', 1, '2019-2-4', 2019);
INSERT INTO `easyoa_global_vacation` VALUES (35, '春节', '2019-02-05', '2019-02-04', '2019-02-10', 7, '2月4日至10日放假调休，共7天。2月2日（星期六）、2月3日（星期日）上班', '拼假建议：2019年2月11日（周一）-2019年2月15日（周五）请假5天，可拼14天春节小长假。', 1, '2019-2-4,2019-2-5,2019-2-6,2019-2-7,2019-2-8,2019-2-9,2019-2-10', 2019);
INSERT INTO `easyoa_global_vacation` VALUES (36, '清明节', '2019-04-05', '2019-04-05', '2019-04-07', 3, '4月5日放假，与周末连休。', '拼假建议：2019年4月1日（周一）~2019年4月4日（周四）请假4天，可拼9天清明节小长假', 1, '2019-4-5,2019-4-6,2019-4-7', 2019);
INSERT INTO `easyoa_global_vacation` VALUES (37, '劳动节', '2019-05-01', '2019-05-01', '2019-05-04', 4, '5月1日放假', '拼假建议：4月28日（周日）~4月30日（周二）请假3天，可拼8天劳动节小长假。', 1, '2019-5-1,2019-5-2,2019-5-3,2019-5-4', 2019);
INSERT INTO `easyoa_global_vacation` VALUES (38, '端午节', '2019-06-07', '2019-06-07', '2019-06-09', 3, '6月7日放假，与周末连休。', '拼假建议：2019年6月3日（周一）~2019年6月6日（周四）请假4天，可拼9天端午节小长假', 1, '2019-6-7,2019-6-8,2019-6-9', 2019);
INSERT INTO `easyoa_global_vacation` VALUES (39, '中秋节', '2019-09-13', '2019-09-13', '2019-09-15', 3, '9月13日放假，与周末连休。', '拼假建议：2019年9月9日（周一）~2019年9月12日（周四）请假4天，可拼9天中秋节小长假', 1, '2019-9-13,2019-9-14,2019-9-15', 2019);
INSERT INTO `easyoa_global_vacation` VALUES (40, '国庆节', '2019-10-01', '2019-10-01', '2019-10-07', 7, '10月1日至10月7日放假，9月29日（星期日）、10月12日（星期六）上班。', '拼假建议：10月8日（周二）~10月12日（周六）请5天假，可拼13天国庆节小长假。', 1, '2019-10-1,2019-10-2,2019-10-3,2019-10-4,2019-10-5,2019-10-6,2019-10-7', 2019);
INSERT INTO `easyoa_global_vacation` VALUES (80, '元旦', '2020-01-01', '2020-01-01', '2020-01-01', 1, '无调休', '无调休', 1, '', 2020);
INSERT INTO `easyoa_global_vacation` VALUES (81, '春节', '2020-01-25', '2020-01-24', '2020-01-30', 7, '1月24日-1月30日放假调休共7天，1月19日（周日）、2月1日（周六）上班。', '1月24日-1月30日放假调休共7天，1月19日（周日）、2月1日（周六）上班。', 0, '2020-01-24,2020-01-25,2020-01-26,2020-01-27,2020-01-28,2020-01-29,2020-01-30', 2020);
INSERT INTO `easyoa_global_vacation` VALUES (82, '清明节', '2020-04-04', '2020-04-04', '2020-04-06', 3, '4月6日放假，与周末连休。', '4月6日放假，与周末连休。', 0, '2020-04-04,2020-04-05,2020-04-06', 2020);
INSERT INTO `easyoa_global_vacation` VALUES (83, '劳动节', '2020-05-01', '2020-05-01', '2020-05-05', 5, '5月1日-5月5日放假调休共5天，4月26日（周日）、5月9日（周六）上班。', '5月1日-5月5日放假调休共5天，4月26日（周日）、5月9日（周六）上班。', 0, '2020-05-01,2020-05-02,2020-05-03,2020-05-04,2020-05-05', 2020);
INSERT INTO `easyoa_global_vacation` VALUES (84, '端午节', '2020-06-25', '2020-06-25', '2020-06-27', 3, '6月25日-6月26日放假，与周六连休，6月28日（周日）上班。', '6月25日-6月26日放假，与周六连休，6月28日（周日）上班。', 0, '2020-06-25,2020-06-26,2020-06-27', 2020);
INSERT INTO `easyoa_global_vacation` VALUES (85, '国庆节', '2020-10-01', '2020-10-01', '2020-10-08', 8, '10月1日-10月8日放假调休共8天，9月27日（周日）、10月10日（周六）上班', '10月1日-10月8日放假调休共8天，9月27日（周日）、10月10日（周六）上班', 0, '2020-10-01,2020-10-02,2020-10-03,2020-10-04,2020-10-05,2020-10-06,2020-10-07,2020-10-08', 2020);
INSERT INTO `easyoa_global_vacation` VALUES (86, '中秋节', '2020-10-01', '2020-10-01', '2020-10-01', 1, '国庆与中秋同一天。', '国庆与中秋同一天。', 0, '', 2020);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_global_vacation_detail
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_global_vacation_detail`;
CREATE TABLE `easyoa_global_vacation_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `offday` varchar(50) DEFAULT NULL COMMENT '休息日期',
  `year` int(4) DEFAULT NULL COMMENT '年份',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of easyoa_global_vacation_detail
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_global_vacation_detail` VALUES (1, '2020-08-30', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (2, '2020-08-29', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (3, '2020-12-27', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (4, '2020-12-26', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (5, '2020-04-25', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (6, '2020-08-23', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (7, '2020-08-22', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (8, '2020-12-20', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (9, '2020-04-19', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (10, '2020-12-19', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (11, '2020-04-18', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (12, '2020-08-16', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (13, '2020-08-15', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (14, '2020-12-13', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (15, '2020-04-12', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (16, '2020-12-12', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (17, '2020-04-11', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (18, '2020-08-09', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (19, '2020-08-08', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (20, '2020-04-06', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (21, '2020-12-06', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (22, '2020-04-05', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (23, '2020-12-05', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (24, '2020-04-04', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (25, '2020-08-02', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (26, '2020-08-01', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (27, '2020-05-31', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (28, '2020-01-30', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (29, '2020-05-30', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (30, '2020-01-29', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (31, '2020-01-28', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (32, '2020-01-27', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (33, '2020-01-26', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (34, '2020-09-26', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (35, '2020-01-25', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (36, '2020-01-24', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (37, '2020-05-24', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (38, '2020-05-23', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (39, '2020-09-20', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (40, '2020-09-19', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (41, '2020-01-18', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (42, '2020-05-17', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (43, '2020-05-16', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (44, '2020-09-13', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (45, '2020-01-12', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (46, '2020-09-12', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (47, '2020-01-11', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (48, '2020-05-10', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (49, '2020-09-06', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (50, '2020-05-05', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (51, '2020-01-05', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (52, '2020-09-05', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (53, '2020-05-04', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (54, '2020-01-04', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (55, '2020-05-03', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (56, '2020-05-02', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (57, '2020-01-01', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (58, '2020-05-01', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (59, '2020-10-31', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (60, '2020-02-29', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (61, '2020-06-27', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (62, '2020-06-26', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (63, '2020-06-25', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (64, '2020-10-25', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (65, '2020-10-24', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (66, '2020-02-23', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (67, '2020-02-22', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (68, '2020-06-21', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (69, '2020-06-20', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (70, '2020-10-18', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (71, '2020-10-17', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (72, '2020-02-16', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (73, '2020-02-15', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (74, '2020-06-14', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (75, '2020-06-13', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (76, '2020-10-11', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (77, '2020-02-09', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (78, '2020-10-08', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (79, '2020-02-08', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (80, '2020-10-07', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (81, '2020-06-07', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (82, '2020-10-06', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (83, '2020-06-06', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (84, '2020-10-05', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (85, '2020-10-04', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (86, '2020-10-03', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (87, '2020-10-02', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (88, '2020-02-02', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (89, '2020-10-01', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (90, '2020-03-29', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (91, '2020-11-29', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (92, '2020-03-28', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (93, '2020-11-28', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (94, '2020-07-26', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (95, '2020-07-25', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (96, '2020-03-22', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (97, '2020-11-22', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (98, '2020-03-21', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (99, '2020-11-21', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (100, '2020-07-19', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (101, '2020-07-18', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (102, '2020-03-15', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (103, '2020-11-15', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (104, '2020-03-14', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (105, '2020-11-14', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (106, '2020-07-12', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (107, '2020-07-11', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (108, '2020-03-08', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (109, '2020-11-08', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (110, '2020-03-07', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (111, '2020-11-07', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (112, '2020-07-05', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (113, '2020-07-04', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (114, '2020-03-01', 2020);
INSERT INTO `easyoa_global_vacation_detail` VALUES (115, '2020-11-01', 2020);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_images
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_images`;
CREATE TABLE `easyoa_images` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '图片名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_images
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_images` VALUES (4, 'default.jpg');
INSERT INTO `easyoa_images` VALUES (5, '1d22f3e41d284f50b2c8fc32e0788698.jpeg');
INSERT INTO `easyoa_images` VALUES (6, '2dd7a2d09fa94bf8b5c52e5318868b4d9.jpg');
INSERT INTO `easyoa_images` VALUES (7, '2dd7a2d09fa94bf8b5c52e5318868b4df.jpg');
INSERT INTO `easyoa_images` VALUES (8, '8f5b60ef00714a399ee544d331231820.jpeg');
INSERT INTO `easyoa_images` VALUES (9, '17e420c250804efe904a09a33796d5a10.jpg');
INSERT INTO `easyoa_images` VALUES (10, '17e420c250804efe904a09a33796d5a16.jpg');
INSERT INTO `easyoa_images` VALUES (11, '87d8194bc9834e9f8f0228e9e530beb1.jpeg');
INSERT INTO `easyoa_images` VALUES (12, '496b3ace787342f7954b7045b8b06804.jpeg');
INSERT INTO `easyoa_images` VALUES (13, '595ba7b05f2e485eb50565a50cb6cc3c.jpeg');
INSERT INTO `easyoa_images` VALUES (14, '964e40b005724165b8cf772355796c8c.jpeg');
INSERT INTO `easyoa_images` VALUES (15, '5997fedcc7bd4cffbd350b40d1b5b987.jpg');
INSERT INTO `easyoa_images` VALUES (16, '5997fedcc7bd4cffbd350b40d1b5b9824.jpg');
INSERT INTO `easyoa_images` VALUES (17, 'a3b10296862e40edb811418d64455d00.jpeg');
INSERT INTO `easyoa_images` VALUES (18, 'a43456282d684e0b9319cf332f8ac468.jpeg');
INSERT INTO `easyoa_images` VALUES (19, 'bba284ac05b041a8b8b0d1927868d5c9x.jpg');
INSERT INTO `easyoa_images` VALUES (20, 'c7c4ee7be3eb4e73a19887dc713505145.jpg');
INSERT INTO `easyoa_images` VALUES (21, 'ff698bb2d25c4d218b3256b46c706ece.jpeg');
INSERT INTO `easyoa_images` VALUES (22, 'cnrhVkzwxjPwAaCfPbdc.png');
INSERT INTO `easyoa_images` VALUES (23, 'BiazfanxmamNRoxxVxka.png');
INSERT INTO `easyoa_images` VALUES (24, 'gaOngJwsRYRaVAuXXcmB.png');
INSERT INTO `easyoa_images` VALUES (25, 'WhxKECPNujWoWEFNdnJE.png');
INSERT INTO `easyoa_images` VALUES (26, 'ubnKSIfAJTxIgXOKlciN.png');
INSERT INTO `easyoa_images` VALUES (27, 'jZUIxmJycoymBprLOUbT.png');
INSERT INTO `easyoa_images` VALUES (28, '19034103295190235.jpg');
INSERT INTO `easyoa_images` VALUES (29, '20180414165920.jpg');
INSERT INTO `easyoa_images` VALUES (30, '20180414170003.jpg');
INSERT INTO `easyoa_images` VALUES (31, '20180414165927.jpg');
INSERT INTO `easyoa_images` VALUES (32, '20180414165754.jpg');
INSERT INTO `easyoa_images` VALUES (33, '20180414165815.jpg');
INSERT INTO `easyoa_images` VALUES (34, '20180414165821.jpg');
INSERT INTO `easyoa_images` VALUES (35, '20180414165827.jpg');
INSERT INTO `easyoa_images` VALUES (36, '20180414165834.jpg');
INSERT INTO `easyoa_images` VALUES (37, '20180414165840.jpg');
INSERT INTO `easyoa_images` VALUES (38, '20180414165846.jpg');
INSERT INTO `easyoa_images` VALUES (39, '20180414165855.jpg');
INSERT INTO `easyoa_images` VALUES (40, '20180414165909.jpg');
INSERT INTO `easyoa_images` VALUES (41, '20180414165914.jpg');
INSERT INTO `easyoa_images` VALUES (42, '20180414165936.jpg');
INSERT INTO `easyoa_images` VALUES (43, '20180414165942.jpg');
INSERT INTO `easyoa_images` VALUES (44, '20180414165947.jpg');
INSERT INTO `easyoa_images` VALUES (45, '20180414165955.jpg');
COMMIT;

-- ----------------------------
-- Table structure for easyoa_leave_file
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_leave_file`;
CREATE TABLE `easyoa_leave_file` (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `file_local_path` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件本地路径',
  `file_remote_path` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件远程路径',
  `file_origin_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件原始名称',
  `file_current_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件新名称',
  `file_content` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件内容',
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_leave_file
-- ----------------------------

-- ----------------------------
-- Table structure for easyoa_leave_rules
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_leave_rules`;
CREATE TABLE `easyoa_leave_rules` (
  `rule_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '假期名称',
  `rule_type` int(2) DEFAULT NULL COMMENT '规则类型',
  `sub_type` int(4) DEFAULT NULL COMMENT '子类别',
  `max_permit_day` int(2) DEFAULT NULL COMMENT '最大允许时间',
  `leave_days_from` int(4) DEFAULT NULL COMMENT '假期时间',
  `leave_days_to` int(2) DEFAULT NULL COMMENT '假期时间',
  `forward_days` int(4) DEFAULT NULL COMMENT '提前天数',
  `age` int(2) DEFAULT NULL COMMENT '年龄',
  `work_years_from` int(2) DEFAULT NULL COMMENT '工作年限',
  `work_years_to` int(2) DEFAULT NULL COMMENT '工作年限',
  `notice` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '提示：需要一些材料进行上传',
  `need_upload` tinyint(1) DEFAULT NULL COMMENT '是否需要上传',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `file_required` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '所需文件内容',
  `company_id` int(11) DEFAULT NULL COMMENT '所属公司',
  PRIMARY KEY (`rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_leave_rules
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_leave_rules` VALUES (1, '事假', NULL, NULL, 2, 0, 3, 1, NULL, NULL, NULL, '事假1-3天，需要提前一天提出申请', 0, '2019-07-08 00:00:00', '2019-07-08 00:00:00', '无', 2);
INSERT INTO `easyoa_leave_rules` VALUES (2, '事假', 1, NULL, 13, 3, 14, 3, NULL, NULL, NULL, '事假3-14天，需要提前3天提出申请', 0, '2019-07-10 14:51:05', '2019-07-10 14:51:09', '无', 2);
INSERT INTO `easyoa_leave_rules` VALUES (3, '事假', 1, NULL, 20, 14, 20, 7, NULL, NULL, NULL, '事假14天以上，需要提前7天提出申请', 0, '2019-07-10 14:52:33', '2019-07-10 14:52:38', '无', 2);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_login_log
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_login_log`;
CREATE TABLE `easyoa_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户名',
  `ip` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户IP',
  `location` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户登录位置',
  `device` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户登录设备',
  `create_time` datetime DEFAULT NULL COMMENT '用户登录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1094 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for easyoa_menu
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_menu`;
CREATE TABLE `easyoa_menu` (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `parent_id` int(11) DEFAULT NULL COMMENT '父菜单',
  `menu_name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单名称',
  `path` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '路由信息',
  `component` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '对应组件',
  `permissions` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '权限',
  `icon` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图标',
  `type` varchar(4) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单-0，按钮-1',
  `order_num` double(20,0) DEFAULT NULL COMMENT '排序',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=191 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_menu
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_menu` VALUES (1, 0, '系统管理', '/system', 'PageView', NULL, 'appstore-o', '0', 1, '2017-12-27 16:39:07', '2019-01-05 11:13:14');
INSERT INTO `easyoa_menu` VALUES (2, 0, '系统监控', '/monitor', 'PageView', NULL, 'dashboard', '0', 4, '2019-07-03 09:54:44', '2019-07-03 09:54:44');
INSERT INTO `easyoa_menu` VALUES (3, 1, '用户管理', '/system/user', 'system/user/User', 'user:view', '', '0', 1, '2017-12-27 16:47:13', '2019-01-22 06:45:55');
INSERT INTO `easyoa_menu` VALUES (4, 1, '角色管理', '/system/role', 'system/role/Role', 'role:view', '', '0', 2, '2017-12-27 16:48:09', '2018-04-25 09:01:12');
INSERT INTO `easyoa_menu` VALUES (5, 1, '菜单管理', '/system/menu', 'system/menu/Menu', 'menu:view', '', '0', 3, '2017-12-27 16:48:57', '2018-04-25 09:01:30');
INSERT INTO `easyoa_menu` VALUES (6, 1, '部门管理', '/system/dept', 'system/dept/Dept', 'dept:view', '', '0', 4, '2017-12-27 16:57:33', '2018-04-25 09:01:40');
INSERT INTO `easyoa_menu` VALUES (8, 2, '在线用户', '/monitor/online', 'monitor/Online', 'user:online', '', '0', 1, '2017-12-27 16:59:33', '2018-04-25 09:02:04');
INSERT INTO `easyoa_menu` VALUES (10, 2, '系统日志', '/monitor/systemlog', 'monitor/SystemLog', 'log:view', '', '0', 2, '2017-12-27 17:00:50', '2018-04-25 09:02:18');
INSERT INTO `easyoa_menu` VALUES (11, 3, '新增用户', '', '', 'user:add', NULL, '1', NULL, '2017-12-27 17:02:58', '2019-07-03 17:16:45');
INSERT INTO `easyoa_menu` VALUES (12, 3, '修改用户', '', '', 'user:update', NULL, '1', NULL, '2017-12-27 17:04:07', '2019-07-03 17:16:48');
INSERT INTO `easyoa_menu` VALUES (13, 3, '删除用户', '', '', 'user:delete', NULL, '1', NULL, '2017-12-27 17:04:58', '2019-07-03 17:16:51');
INSERT INTO `easyoa_menu` VALUES (14, 4, '新增角色', '', '', 'role:add', NULL, '1', NULL, '2017-12-27 17:06:38', '2019-07-03 17:17:01');
INSERT INTO `easyoa_menu` VALUES (15, 4, '修改角色', '', '', 'role:update', NULL, '1', NULL, '2017-12-27 17:06:38', '2019-07-03 17:16:58');
INSERT INTO `easyoa_menu` VALUES (16, 4, '删除角色', '', '', 'role:delete', NULL, '1', NULL, '2017-12-27 17:06:38', '2019-07-03 17:16:55');
INSERT INTO `easyoa_menu` VALUES (17, 5, '新增菜单', '', '', 'menu:add', NULL, '1', NULL, '2017-12-27 17:08:02', '2019-07-03 17:17:09');
INSERT INTO `easyoa_menu` VALUES (18, 5, '修改菜单', '', '', 'menu:update', NULL, '1', NULL, '2017-12-27 17:08:02', '2019-07-03 17:17:12');
INSERT INTO `easyoa_menu` VALUES (19, 5, '删除菜单', '', '', 'menu:delete', NULL, '1', NULL, '2017-12-27 17:08:02', '2019-07-03 17:17:15');
INSERT INTO `easyoa_menu` VALUES (20, 6, '新增部门', '', '', 'dept:add', NULL, '1', NULL, '2017-12-27 17:09:24', '2019-07-03 17:17:18');
INSERT INTO `easyoa_menu` VALUES (21, 6, '修改部门', '', '', 'dept:update', NULL, '1', NULL, '2017-12-27 17:09:24', '2019-07-03 17:17:21');
INSERT INTO `easyoa_menu` VALUES (22, 6, '删除部门', '', '', 'dept:delete', NULL, '1', NULL, '2017-12-27 17:09:24', '2019-07-03 17:17:25');
INSERT INTO `easyoa_menu` VALUES (23, 8, '踢出用户', '', '', 'user:kickout', NULL, '1', NULL, '2017-12-27 17:11:13', '2019-07-03 17:17:29');
INSERT INTO `easyoa_menu` VALUES (130, 3, '导出Excel', NULL, NULL, 'user:export', NULL, '1', NULL, '2019-01-23 06:35:16', '2019-07-03 17:18:23');
INSERT INTO `easyoa_menu` VALUES (131, 4, '导出Excel', NULL, NULL, 'role:export', NULL, '1', NULL, '2019-01-23 06:35:36', '2019-07-03 17:18:26');
INSERT INTO `easyoa_menu` VALUES (132, 5, '导出Excel', NULL, NULL, 'menu:export', NULL, '1', NULL, '2019-01-23 06:36:05', '2019-07-03 17:18:28');
INSERT INTO `easyoa_menu` VALUES (133, 6, '导出Excel', NULL, NULL, 'dept:export', NULL, '1', NULL, '2019-01-23 06:36:25', '2019-07-03 17:18:31');
INSERT INTO `easyoa_menu` VALUES (134, 64, '导出Excel', NULL, NULL, 'dict:export', NULL, '1', NULL, '2019-01-23 06:36:43', '2019-07-03 17:18:34');
INSERT INTO `easyoa_menu` VALUES (135, 3, '密码重置', NULL, NULL, 'user:reset', NULL, '1', NULL, '2019-01-23 06:37:00', '2019-07-03 17:18:36');
INSERT INTO `easyoa_menu` VALUES (139, 12, '导入头像', NULL, NULL, 'image:add', NULL, '1', NULL, '2019-07-02 11:09:39', '2019-07-02 11:09:44');
INSERT INTO `easyoa_menu` VALUES (140, 1, '流程管理', '/system/flow', 'system/flow/Flow', 'flow:view', '', '0', 6, '2019-07-03 09:54:02', '2019-07-03 09:54:02');
INSERT INTO `easyoa_menu` VALUES (142, 1, '规则管理', '/system/rule', 'system/rule/Rule', 'rule:view', '', '0', 8, '2019-07-11 19:10:09', '2019-07-11 19:10:09');
INSERT INTO `easyoa_menu` VALUES (143, 1, '邮件管理', '/system/mail', 'system/mail/Mail', 'mail:view', NULL, '0', 9, '2019-08-06 01:05:31', '2019-08-06 01:05:31');
INSERT INTO `easyoa_menu` VALUES (144, 1, '公告管理', '/system/notice', 'system/notice/Notice', 'notice:view', NULL, '0', 10, '2019-08-06 01:05:43', '2019-08-06 01:05:43');
INSERT INTO `easyoa_menu` VALUES (145, 1, '销假管理', '/system/cancel', 'system/cancel/Cancel', 'cancel:view', NULL, '0', 11, '2019-08-06 01:05:59', '2019-08-06 01:05:59');
INSERT INTO `easyoa_menu` VALUES (146, 0, '用户休假', '/application', 'PageView', NULL, 'form', '0', 2, '2019-07-03 09:58:28', '2019-07-03 09:58:28');
INSERT INTO `easyoa_menu` VALUES (147, 0, '处理中心', '/process', 'PageView', NULL, 'radius-setting', '0', 3, '2019-07-03 09:58:51', '2019-07-03 09:58:51');
INSERT INTO `easyoa_menu` VALUES (148, 146, '休假申请', '/application/submission', 'application/submission/Application', 'application:view', '', '0', NULL, '2019-07-03 11:08:49', '2019-07-03 11:08:49');
INSERT INTO `easyoa_menu` VALUES (149, 148, '新增申请', NULL, NULL, 'application:add', NULL, '1', NULL, '2019-07-03 11:09:43', '2019-07-03 11:09:43');
INSERT INTO `easyoa_menu` VALUES (150, 148, '修改申请', NULL, NULL, 'application:edit', NULL, '1', NULL, '2019-07-03 11:10:27', '2019-07-03 11:10:27');
INSERT INTO `easyoa_menu` VALUES (151, 148, '删除申请', NULL, NULL, 'application:delete', NULL, '1', NULL, '2019-07-03 11:10:51', '2019-07-03 11:10:51');
INSERT INTO `easyoa_menu` VALUES (153, 146, '我的数据', '/application/vacation', 'application/vacation/Vacation', 'vacation:view', NULL, '0', NULL, '2019-07-03 11:16:56', '2019-07-03 11:16:56');
INSERT INTO `easyoa_menu` VALUES (154, 147, '待审核申请', '/process/todo', 'process/todo/Process', 'process:view', '', '0', 1, '2019-07-19 17:18:36', '2019-07-19 17:18:36');
INSERT INTO `easyoa_menu` VALUES (155, 147, '我的处理', '/process/done', 'process/done/Done', 'done:view', '', '0', 2, '2019-07-19 17:18:46', '2019-07-19 17:18:46');
INSERT INTO `easyoa_menu` VALUES (156, 142, '修改节日', NULL, NULL, 'vacation:edit', NULL, '1', NULL, '2019-07-11 23:27:05', '2019-07-11 23:27:05');
INSERT INTO `easyoa_menu` VALUES (157, 142, '修改规则', NULL, NULL, 'rule:edit', NULL, '1', NULL, '2019-07-11 23:27:15', '2019-07-11 23:27:15');
INSERT INTO `easyoa_menu` VALUES (158, 142, '新增规则', NULL, NULL, 'rule:add', NULL, '1', NULL, '2019-07-12 16:16:30', '2019-07-12 16:16:30');
INSERT INTO `easyoa_menu` VALUES (159, 142, '删除规则', NULL, NULL, 'rule:delete', NULL, '1', NULL, '2019-07-11 23:26:53', '2019-07-11 23:26:53');
INSERT INTO `easyoa_menu` VALUES (160, 140, '新增流程', NULL, NULL, 'flow:add', NULL, '1', NULL, '2019-07-12 15:24:15', '2019-07-12 15:24:15');
INSERT INTO `easyoa_menu` VALUES (161, 140, '修改流程', NULL, NULL, 'flow:edit', NULL, '1', NULL, '2019-07-12 15:24:35', '2019-07-12 15:24:35');
INSERT INTO `easyoa_menu` VALUES (163, 140, '删除流程', NULL, NULL, 'flow:delete', NULL, '1', NULL, '2019-07-12 16:16:01', '2019-07-12 16:16:01');
INSERT INTO `easyoa_menu` VALUES (164, 154, '申请流转', NULL, NULL, 'process:pass', NULL, '1', NULL, '2019-07-26 14:37:23', '2019-07-26 14:37:23');
INSERT INTO `easyoa_menu` VALUES (165, 154, '申请结束', NULL, NULL, 'process:over', NULL, '1', NULL, '2019-07-26 14:37:33', '2019-07-26 14:37:33');
INSERT INTO `easyoa_menu` VALUES (166, 154, '拒绝申请', NULL, NULL, 'process:refuse', NULL, '1', NULL, '2019-07-26 14:37:43', '2019-07-26 14:37:43');
INSERT INTO `easyoa_menu` VALUES (167, 154, '重做申请', NULL, NULL, 'process:redo', NULL, '1', NULL, '2019-07-26 14:37:52', '2019-07-26 14:37:52');
INSERT INTO `easyoa_menu` VALUES (168, 147, '所有申请', '/process/check', 'process/check/Check', 'check:view', '', '0', 3, '2019-07-19 17:18:18', '2019-07-19 17:18:18');
INSERT INTO `easyoa_menu` VALUES (169, 168, '导出数据', NULL, NULL, 'check:export', NULL, '1', NULL, '2019-07-19 18:50:30', '2019-07-19 18:50:30');
INSERT INTO `easyoa_menu` VALUES (170, 146, '我的消息', '/application/notes', 'application/notes/MyNotes', 'mynotes:view', '', '0', 3, '2019-07-23 09:22:34', '2019-07-23 09:22:34');
INSERT INTO `easyoa_menu` VALUES (171, 170, '标记已读', NULL, NULL, 'mynotes:delete', NULL, '1', NULL, '2019-07-23 09:37:02', '2019-07-23 09:37:02');
INSERT INTO `easyoa_menu` VALUES (172, 144, '新增公告', NULL, NULL, 'notice:add', NULL, '1', NULL, '2019-08-02 10:18:28', '2019-08-02 10:18:28');
INSERT INTO `easyoa_menu` VALUES (173, 144, '删除公告', NULL, NULL, 'notice:delete', NULL, '1', NULL, '2019-08-02 10:18:48', '2019-08-02 10:18:48');
INSERT INTO `easyoa_menu` VALUES (174, 144, '修改公告', NULL, NULL, 'notice:edit', NULL, '1', NULL, '2019-08-02 10:19:18', '2019-08-02 10:19:18');
INSERT INTO `easyoa_menu` VALUES (175, 143, '新增邮件', NULL, NULL, 'mail:add', NULL, '1', NULL, '2019-08-05 07:58:45', '2019-08-05 07:58:45');
INSERT INTO `easyoa_menu` VALUES (176, 143, '修改邮件', NULL, NULL, 'mail:edit', NULL, '1', NULL, '2019-08-05 07:59:11', '2019-08-05 07:59:11');
INSERT INTO `easyoa_menu` VALUES (177, 143, '删除邮件', NULL, NULL, 'mail:delete', NULL, '1', NULL, '2019-08-05 07:59:39', '2019-08-05 07:59:39');
INSERT INTO `easyoa_menu` VALUES (178, 1, '休假报表', '/system/data', 'system/data/LmsData', 'lmsdata:view', '', '0', 12, '2019-08-06 07:52:14', '2019-08-06 07:52:14');
INSERT INTO `easyoa_menu` VALUES (179, 145, '销假', NULL, NULL, 'cancel:edit', NULL, '1', NULL, '2019-08-06 06:02:49', '2019-08-06 06:02:49');
INSERT INTO `easyoa_menu` VALUES (180, 178, '导出月报', NULL, NULL, 'lmsdata:export', NULL, '1', NULL, '2019-08-07 07:48:31', '2019-08-07 07:48:31');
INSERT INTO `easyoa_menu` VALUES (181, 3, '导入员工资料', NULL, NULL, 'user:upload', NULL, '1', NULL, '2019-08-07 09:25:36', '2019-08-07 09:25:36');
INSERT INTO `easyoa_menu` VALUES (182, 168, '修改申请', NULL, NULL, 'application:update', NULL, '1', NULL, '2019-10-07 07:29:50', '2019-10-07 07:29:50');
INSERT INTO `easyoa_menu` VALUES (183, 168, '通过申请', NULL, NULL, 'application:finish', NULL, '1', NULL, '2019-10-08 09:53:09', '2019-10-08 09:53:09');
INSERT INTO `easyoa_menu` VALUES (184, 154, '申请撤销', NULL, NULL, 'process:cancel', NULL, '1', NULL, '2022-02-17 18:26:20', '2022-02-17 18:26:20');
INSERT INTO `easyoa_menu` VALUES (185, 1, '公司管理', '/system/company', 'system/company/Company', 'company:view', '', '0', 11, '2022-02-19 09:44:34', '2022-02-19 09:44:34');
INSERT INTO `easyoa_menu` VALUES (186, 185, '新增公司', NULL, NULL, 'company:add', NULL, '1', NULL, '2022-02-19 09:45:09', '2022-02-19 09:45:09');
INSERT INTO `easyoa_menu` VALUES (187, 185, '修改公司', NULL, NULL, 'company:update', NULL, '1', NULL, '2022-02-19 09:45:32', '2022-02-19 09:45:32');
INSERT INTO `easyoa_menu` VALUES (188, 185, '删除公司', NULL, NULL, 'company:delete', NULL, '1', NULL, '2022-02-19 09:46:05', '2022-02-19 09:46:05');
INSERT INTO `easyoa_menu` VALUES (189, 1, '全权限', NULL, NULL, 'data:super', NULL, '1', NULL, '2022-02-21 13:26:20', '2022-02-21 13:26:20');
INSERT INTO `easyoa_menu` VALUES (190, 148, '撤销申请', NULL, NULL, 'application:cancel', NULL, '1', NULL, '2022-02-25 11:08:03', '2022-02-25 11:08:03');
COMMIT;

-- ----------------------------
-- Table structure for easyoa_notice
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_notice`;
CREATE TABLE `easyoa_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID，没有用户ID表示广播消息',
  `message` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '广播消息',
  `create_date` date DEFAULT NULL COMMENT '创建时间',
  `sender` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '发送人',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `priority` int(1) DEFAULT NULL COMMENT 'L0 M1 H2 三种消息的等级 ',
  `title` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标题',
  `checked` tinyint(1) DEFAULT NULL COMMENT '消息是否已读',
  `type` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '类别：personal / system',
  `check_sort` int(1) DEFAULT NULL COMMENT '用于check字段排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=362 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_notice
-- ----------------------------

-- ----------------------------
-- Table structure for easyoa_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_operation_log`;
CREATE TABLE `easyoa_operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作',
  `time` bigint(20) DEFAULT NULL COMMENT '请求时长',
  `method` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '方法',
  `params` text COLLATE utf8mb4_bin COMMENT '参数',
  `ip` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户IP',
  `create_time` datetime DEFAULT NULL COMMENT '请求时间',
  `location` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '请求位置',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=401 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for easyoa_position
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_position`;
CREATE TABLE `easyoa_position` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '职位',
  `description` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '职位描述',
  `user_code` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '用户工号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=274 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_position
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_position` VALUES (272, '大数据工程师', '大数据工程师', 'admin002');
INSERT INTO `easyoa_position` VALUES (273, '大数据工程师', '大数据工程师', 'admin001');
COMMIT;

-- ----------------------------
-- Table structure for easyoa_resources
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_resources`;
CREATE TABLE `easyoa_resources` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `path` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '文件目录地址',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `remark` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '资源说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for easyoa_role
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_role`;
CREATE TABLE `easyoa_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '角色名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT NULL COMMENT '是否删除',
  `description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '角色描述',
  `comapny_id` int(11) DEFAULT NULL COMMENT '所属公司',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_role
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_role` VALUES (1, '研发人员', '2019-07-23 19:01:51', '2022-02-21 13:26:32', 0, '研发人员拥有所有权限', NULL);
INSERT INTO `easyoa_role` VALUES (2, '管理员', '2019-07-23 19:02:20', '2022-02-24 15:02:50', 0, '主要面向系统管理', NULL);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_role_menu`;
CREATE TABLE `easyoa_role_menu` (
  `role_id` int(11) NOT NULL,
  `menu_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_role_menu` VALUES (1, 1);
INSERT INTO `easyoa_role_menu` VALUES (1, 2);
INSERT INTO `easyoa_role_menu` VALUES (1, 3);
INSERT INTO `easyoa_role_menu` VALUES (1, 4);
INSERT INTO `easyoa_role_menu` VALUES (1, 5);
INSERT INTO `easyoa_role_menu` VALUES (1, 6);
INSERT INTO `easyoa_role_menu` VALUES (1, 8);
INSERT INTO `easyoa_role_menu` VALUES (1, 10);
INSERT INTO `easyoa_role_menu` VALUES (1, 11);
INSERT INTO `easyoa_role_menu` VALUES (1, 12);
INSERT INTO `easyoa_role_menu` VALUES (1, 13);
INSERT INTO `easyoa_role_menu` VALUES (1, 14);
INSERT INTO `easyoa_role_menu` VALUES (1, 15);
INSERT INTO `easyoa_role_menu` VALUES (1, 16);
INSERT INTO `easyoa_role_menu` VALUES (1, 17);
INSERT INTO `easyoa_role_menu` VALUES (1, 18);
INSERT INTO `easyoa_role_menu` VALUES (1, 19);
INSERT INTO `easyoa_role_menu` VALUES (1, 20);
INSERT INTO `easyoa_role_menu` VALUES (1, 21);
INSERT INTO `easyoa_role_menu` VALUES (1, 22);
INSERT INTO `easyoa_role_menu` VALUES (1, 23);
INSERT INTO `easyoa_role_menu` VALUES (1, 130);
INSERT INTO `easyoa_role_menu` VALUES (1, 131);
INSERT INTO `easyoa_role_menu` VALUES (1, 132);
INSERT INTO `easyoa_role_menu` VALUES (1, 133);
INSERT INTO `easyoa_role_menu` VALUES (1, 135);
INSERT INTO `easyoa_role_menu` VALUES (1, 139);
INSERT INTO `easyoa_role_menu` VALUES (1, 140);
INSERT INTO `easyoa_role_menu` VALUES (1, 142);
INSERT INTO `easyoa_role_menu` VALUES (1, 143);
INSERT INTO `easyoa_role_menu` VALUES (1, 144);
INSERT INTO `easyoa_role_menu` VALUES (1, 145);
INSERT INTO `easyoa_role_menu` VALUES (1, 146);
INSERT INTO `easyoa_role_menu` VALUES (1, 147);
INSERT INTO `easyoa_role_menu` VALUES (1, 148);
INSERT INTO `easyoa_role_menu` VALUES (1, 149);
INSERT INTO `easyoa_role_menu` VALUES (1, 150);
INSERT INTO `easyoa_role_menu` VALUES (1, 151);
INSERT INTO `easyoa_role_menu` VALUES (1, 153);
INSERT INTO `easyoa_role_menu` VALUES (1, 154);
INSERT INTO `easyoa_role_menu` VALUES (1, 155);
INSERT INTO `easyoa_role_menu` VALUES (1, 156);
INSERT INTO `easyoa_role_menu` VALUES (1, 157);
INSERT INTO `easyoa_role_menu` VALUES (1, 158);
INSERT INTO `easyoa_role_menu` VALUES (1, 159);
INSERT INTO `easyoa_role_menu` VALUES (1, 160);
INSERT INTO `easyoa_role_menu` VALUES (1, 161);
INSERT INTO `easyoa_role_menu` VALUES (1, 163);
INSERT INTO `easyoa_role_menu` VALUES (1, 164);
INSERT INTO `easyoa_role_menu` VALUES (1, 165);
INSERT INTO `easyoa_role_menu` VALUES (1, 166);
INSERT INTO `easyoa_role_menu` VALUES (1, 167);
INSERT INTO `easyoa_role_menu` VALUES (1, 168);
INSERT INTO `easyoa_role_menu` VALUES (1, 169);
INSERT INTO `easyoa_role_menu` VALUES (1, 170);
INSERT INTO `easyoa_role_menu` VALUES (1, 171);
INSERT INTO `easyoa_role_menu` VALUES (1, 172);
INSERT INTO `easyoa_role_menu` VALUES (1, 173);
INSERT INTO `easyoa_role_menu` VALUES (1, 174);
INSERT INTO `easyoa_role_menu` VALUES (1, 175);
INSERT INTO `easyoa_role_menu` VALUES (1, 176);
INSERT INTO `easyoa_role_menu` VALUES (1, 177);
INSERT INTO `easyoa_role_menu` VALUES (1, 178);
INSERT INTO `easyoa_role_menu` VALUES (1, 179);
INSERT INTO `easyoa_role_menu` VALUES (1, 180);
INSERT INTO `easyoa_role_menu` VALUES (1, 181);
INSERT INTO `easyoa_role_menu` VALUES (1, 182);
INSERT INTO `easyoa_role_menu` VALUES (1, 183);
INSERT INTO `easyoa_role_menu` VALUES (1, 184);
INSERT INTO `easyoa_role_menu` VALUES (1, 185);
INSERT INTO `easyoa_role_menu` VALUES (1, 186);
INSERT INTO `easyoa_role_menu` VALUES (1, 187);
INSERT INTO `easyoa_role_menu` VALUES (1, 188);
INSERT INTO `easyoa_role_menu` VALUES (1, 189);
INSERT INTO `easyoa_role_menu` VALUES (2, 1);
INSERT INTO `easyoa_role_menu` VALUES (2, 3);
INSERT INTO `easyoa_role_menu` VALUES (2, 4);
INSERT INTO `easyoa_role_menu` VALUES (2, 5);
INSERT INTO `easyoa_role_menu` VALUES (2, 6);
INSERT INTO `easyoa_role_menu` VALUES (2, 11);
INSERT INTO `easyoa_role_menu` VALUES (2, 12);
INSERT INTO `easyoa_role_menu` VALUES (2, 13);
INSERT INTO `easyoa_role_menu` VALUES (2, 14);
INSERT INTO `easyoa_role_menu` VALUES (2, 15);
INSERT INTO `easyoa_role_menu` VALUES (2, 16);
INSERT INTO `easyoa_role_menu` VALUES (2, 17);
INSERT INTO `easyoa_role_menu` VALUES (2, 18);
INSERT INTO `easyoa_role_menu` VALUES (2, 19);
INSERT INTO `easyoa_role_menu` VALUES (2, 20);
INSERT INTO `easyoa_role_menu` VALUES (2, 21);
INSERT INTO `easyoa_role_menu` VALUES (2, 22);
INSERT INTO `easyoa_role_menu` VALUES (2, 130);
INSERT INTO `easyoa_role_menu` VALUES (2, 131);
INSERT INTO `easyoa_role_menu` VALUES (2, 132);
INSERT INTO `easyoa_role_menu` VALUES (2, 133);
INSERT INTO `easyoa_role_menu` VALUES (2, 135);
INSERT INTO `easyoa_role_menu` VALUES (2, 139);
INSERT INTO `easyoa_role_menu` VALUES (2, 140);
INSERT INTO `easyoa_role_menu` VALUES (2, 142);
INSERT INTO `easyoa_role_menu` VALUES (2, 143);
INSERT INTO `easyoa_role_menu` VALUES (2, 144);
INSERT INTO `easyoa_role_menu` VALUES (2, 145);
INSERT INTO `easyoa_role_menu` VALUES (2, 146);
INSERT INTO `easyoa_role_menu` VALUES (2, 147);
INSERT INTO `easyoa_role_menu` VALUES (2, 148);
INSERT INTO `easyoa_role_menu` VALUES (2, 149);
INSERT INTO `easyoa_role_menu` VALUES (2, 150);
INSERT INTO `easyoa_role_menu` VALUES (2, 151);
INSERT INTO `easyoa_role_menu` VALUES (2, 153);
INSERT INTO `easyoa_role_menu` VALUES (2, 154);
INSERT INTO `easyoa_role_menu` VALUES (2, 155);
INSERT INTO `easyoa_role_menu` VALUES (2, 156);
INSERT INTO `easyoa_role_menu` VALUES (2, 157);
INSERT INTO `easyoa_role_menu` VALUES (2, 158);
INSERT INTO `easyoa_role_menu` VALUES (2, 159);
INSERT INTO `easyoa_role_menu` VALUES (2, 160);
INSERT INTO `easyoa_role_menu` VALUES (2, 161);
INSERT INTO `easyoa_role_menu` VALUES (2, 163);
INSERT INTO `easyoa_role_menu` VALUES (2, 164);
INSERT INTO `easyoa_role_menu` VALUES (2, 165);
INSERT INTO `easyoa_role_menu` VALUES (2, 166);
INSERT INTO `easyoa_role_menu` VALUES (2, 167);
INSERT INTO `easyoa_role_menu` VALUES (2, 168);
INSERT INTO `easyoa_role_menu` VALUES (2, 169);
INSERT INTO `easyoa_role_menu` VALUES (2, 170);
INSERT INTO `easyoa_role_menu` VALUES (2, 171);
INSERT INTO `easyoa_role_menu` VALUES (2, 172);
INSERT INTO `easyoa_role_menu` VALUES (2, 173);
INSERT INTO `easyoa_role_menu` VALUES (2, 174);
INSERT INTO `easyoa_role_menu` VALUES (2, 175);
INSERT INTO `easyoa_role_menu` VALUES (2, 176);
INSERT INTO `easyoa_role_menu` VALUES (2, 177);
INSERT INTO `easyoa_role_menu` VALUES (2, 178);
INSERT INTO `easyoa_role_menu` VALUES (2, 179);
INSERT INTO `easyoa_role_menu` VALUES (2, 180);
INSERT INTO `easyoa_role_menu` VALUES (2, 181);
INSERT INTO `easyoa_role_menu` VALUES (2, 182);
INSERT INTO `easyoa_role_menu` VALUES (2, 183);
INSERT INTO `easyoa_role_menu` VALUES (2, 184);
INSERT INTO `easyoa_role_menu` VALUES (2, 185);
INSERT INTO `easyoa_role_menu` VALUES (2, 186);
INSERT INTO `easyoa_role_menu` VALUES (2, 187);
INSERT INTO `easyoa_role_menu` VALUES (2, 188);
INSERT INTO `easyoa_role_menu` VALUES (2, 189);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_role_permission`;
CREATE TABLE `easyoa_role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for easyoa_syslog
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_syslog`;
CREATE TABLE `easyoa_syslog` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `info` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '详细信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for easyoa_user
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_user`;
CREATE TABLE `easyoa_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '用户名称',
  `nick_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '用户密码',
  `sex` char(2) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户性别',
  `salt` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '盐值',
  `dept_id` int(11) NOT NULL COMMENT '部门id',
  `user_code` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '员工工号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT NULL COMMENT '是否删除',
  `status` int(2) DEFAULT NULL COMMENT '是否锁住',
  `avatar` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '头像图片ID',
  `company_id` int(11) DEFAULT NULL COMMENT '所属公司',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_username` (`user_name`) USING BTREE COMMENT '用户名唯一',
  KEY `idx_createtime` (`create_time`) USING BTREE COMMENT '时间索引',
  KEY `idx_dept` (`dept_id`) USING BTREE COMMENT '部门索引'
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_user
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_user` VALUES (27, '123456@qq.com', '123456@qq.com', '93c5c9d6ba27d503a570e4c29ad77e53', 'F', NULL, 31, 'admin001', '2019-07-23 19:10:57', '2019-07-23 19:10:57', 0, 1, '4', 2);
INSERT INTO `easyoa_user` VALUES (29, '12345678@qq.com', '12345678@qq.com', '13f7d6b32e6bd39c6a16d971f545acfc', 'F', NULL, 32, 'admin002', '2019-07-23 19:30:11', '2019-07-23 19:30:11', 0, 1, '4', 2);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_user_config
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_user_config`;
CREATE TABLE `easyoa_user_config` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `theme` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户主题',
  `layout` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户排版，侧边栏、head栏',
  `multi_page` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '页面风格，多标签-1，单页-0',
  `fix_siderbar` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否固定侧边栏',
  `fix_header` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否固定header',
  `color` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '主题颜色',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_user_config
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_user_config` VALUES (27, 'dark', 'side', '0', '1', '1', 'rgb(66, 185, 131)');
INSERT INTO `easyoa_user_config` VALUES (29, 'dark', 'side', '0', '1', '1', 'rgb(66, 185, 131)');
COMMIT;

-- ----------------------------
-- Table structure for easyoa_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_user_dept`;
CREATE TABLE `easyoa_user_dept` (
  `user_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '用户工号',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门id',
  PRIMARY KEY (`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_user_dept
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_user_dept` VALUES ('10001', 1);
INSERT INTO `easyoa_user_dept` VALUES ('123456789@qq.com', 28);
INSERT INTO `easyoa_user_dept` VALUES ('12345678@qq.com', 32);
INSERT INTO `easyoa_user_dept` VALUES ('1234567@qq.com', 28);
INSERT INTO `easyoa_user_dept` VALUES ('123456@qq.com', 31);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_user_detail
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_user_detail`;
CREATE TABLE `easyoa_user_detail` (
  `id` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT 'userCode',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `chinese_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '中文名',
  `english_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '英文名',
  `age` smallint(6) DEFAULT NULL COMMENT '年龄',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别',
  `marriage` tinyint(1) DEFAULT NULL COMMENT '婚姻状况',
  `work_year` double(5,2) DEFAULT NULL COMMENT '工龄',
  `phone` varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '电话号码',
  `email` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '邮件',
  `center` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '中心',
  `birth_date` datetime DEFAULT NULL COMMENT '出生日期',
  `hire_date` datetime NOT NULL COMMENT '工作日期',
  `school` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '毕业学校',
  `address` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '现居住地',
  `position_name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '职位',
  `deleted` tinyint(1) DEFAULT NULL COMMENT '是否删除',
  `type` smallint(6) DEFAULT NULL COMMENT '员工类别',
  `work_date` datetime DEFAULT NULL COMMENT '参加工作时间',
  `work_in_compony` double(5,2) DEFAULT NULL COMMENT '司龄',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_user_detail
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_user_detail` VALUES ('admin001', 27, 'admin1', 'admin1', 10, 0, 0, 17.32, '11111111111', '123456@qq.com', 'easyoa', '2019-04-01 00:00:00', '2019-04-01 00:00:00', NULL, NULL, '大数据工程师', 0, 1, '2002-04-01 00:00:00', 1.00);
INSERT INTO `easyoa_user_detail` VALUES ('admin002', 29, 'admin2', 'admin2', 10, 0, 1, 17.32, '11111111111', '12345678@qq.com', 'easyoa', '2019-04-01 00:00:00', '2019-04-01 00:00:00', NULL, NULL, '大数据工程师', 0, 1, '2002-04-01 00:00:00', 1.00);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_user_reporter
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_user_reporter`;
CREATE TABLE `easyoa_user_reporter` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_code` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '员工编号',
  `user_name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '员工姓名',
  `reporter_code` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '汇报人编号',
  `reporter_name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '汇报人姓名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=219 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_user_reporter
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_user_reporter` VALUES (218, 'admin002', 'admin2', 'admin001', 'admin1');
COMMIT;

-- ----------------------------
-- Table structure for easyoa_user_role
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_user_role`;
CREATE TABLE `easyoa_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_user_role
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_user_role` VALUES (27, 1);
INSERT INTO `easyoa_user_role` VALUES (29, 7);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_user_vacation
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_user_vacation`;
CREATE TABLE `easyoa_user_vacation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `total_all` double(4,1) DEFAULT NULL COMMENT '全年总假期',
  `total` double(4,1) DEFAULT NULL COMMENT '剩余总假期数',
  `casual_leave` double(4,1) DEFAULT NULL COMMENT '事假',
  `sick_leave` double(4,1) DEFAULT NULL COMMENT '病假',
  `marriage_leave` double(4,1) DEFAULT NULL COMMENT '婚假',
  `funeral_leave` double(4,1) DEFAULT NULL COMMENT '丧假',
  `maternity_leave` double(4,1) DEFAULT NULL COMMENT '产假',
  `paternity_leave` double(4,1) DEFAULT NULL COMMENT '陪产假',
  `annual_leave` double(4,1) DEFAULT NULL COMMENT '年假',
  `sick_leave_normal` double(4,1) DEFAULT NULL COMMENT '普通病假',
  `weekly_apply` int(2) DEFAULT NULL COMMENT '周申请数',
  `monthly_apply` int(2) DEFAULT NULL COMMENT '月申请数',
  `cl_backup` double(4,1) DEFAULT NULL,
  `sl_backup` double(4,1) DEFAULT NULL,
  `ml_backup` double(4,1) DEFAULT NULL,
  `fl_backup` double(4,1) DEFAULT NULL,
  `mnl_backup` double(4,1) DEFAULT NULL,
  `pnl_backup` double(4,1) DEFAULT NULL,
  `al_backup` double(4,1) DEFAULT NULL,
  `sln_backup` double(4,1) DEFAULT NULL,
  `year` int(4) DEFAULT NULL,
  `annual_should` double(4,1) DEFAULT NULL COMMENT '应有年假',
  `rest_annual_leave` double(4,1) DEFAULT NULL COMMENT '上年剩余年假',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=561 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_user_vacation
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_user_vacation` VALUES (559, 27, 103.0, 103.0, 20.0, 5.0, 3.0, 3.0, 42.0, 0.0, 10.0, 20.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2022, 10.0, 10.0);
INSERT INTO `easyoa_user_vacation` VALUES (560, 29, 100.0, 100.0, 20.0, 5.0, 0.0, 3.0, 42.0, 0.0, 10.0, 20.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 2022, 10.0, 10.0);
COMMIT;

-- ----------------------------
-- Table structure for easyoa_user_vacation_cal
-- ----------------------------
DROP TABLE IF EXISTS `easyoa_user_vacation_cal`;
CREATE TABLE `easyoa_user_vacation_cal` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(11) DEFAULT NULL COMMENT '用户ID',
  `user_code` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户工号',
  `user_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户姓名',
  `hire_date` date DEFAULT NULL COMMENT '入职时间',
  `annual` double(4,1) DEFAULT NULL COMMENT '年假',
  `sick` double(4,1) DEFAULT NULL COMMENT '有薪病假',
  `sick_normal` double(4,1) DEFAULT NULL COMMENT '普通病假',
  `marriage` double(4,1) DEFAULT NULL COMMENT '婚假',
  `mater_paternity` double(4,1) DEFAULT NULL COMMENT '产假/陪产假',
  `funeral` double(4,1) DEFAULT NULL COMMENT '丧假',
  `casual` double(4,1) DEFAULT NULL COMMENT '事假',
  `maternity4` double(4,1) DEFAULT NULL COMMENT '产检假',
  `annual_should` double(4,1) DEFAULT NULL COMMENT '应有年假',
  `annual_cal` double(4,1) DEFAULT NULL COMMENT '折算年假',
  `annual_rest` double(4,1) DEFAULT NULL COMMENT '剩余年休',
  `calculate_month` date DEFAULT NULL COMMENT '计算的月份',
  `calculate_time` datetime DEFAULT NULL COMMENT '计算的时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=186 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of easyoa_user_vacation_cal
-- ----------------------------
BEGIN;
INSERT INTO `easyoa_user_vacation_cal` VALUES (146, 27, 'admin001', 'admin1', '2019-04-01', 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10.0, 8.0, 8.0, '2022-02-01', '2022-02-01 15:40:26');
INSERT INTO `easyoa_user_vacation_cal` VALUES (147, 29, 'admin002', 'admin2', '2019-04-01', 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10.0, 8.0, 8.0, '2022-02-01', '2022-08-01 15:40:27');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
