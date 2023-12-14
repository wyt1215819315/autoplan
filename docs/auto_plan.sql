/*
 Navicat Premium Data Transfer

 Source Server Type    : MySQL
 Source Server Version : 80024
 Source Schema         : auto_plan

 Target Server Type    : MySQL
 Target Server Version : 80024
 File Encoding         : 65001

 Date: 14/12/2023 09:04:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for auto_index
-- ----------------------------
DROP TABLE IF EXISTS `auto_index`;
CREATE TABLE `auto_index`  (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                               `enable` int NOT NULL DEFAULT 1 COMMENT '是否开启，0不开启 1开启',
                               `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
                               `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '代号',
                               `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '前端渲染的图标',
                               `delay` int NOT NULL DEFAULT 0 COMMENT '任务延迟',
                               `thread_num` int NOT NULL DEFAULT 1 COMMENT '任务同时运行的并发数量，为了防止风控一般都是1',
                               `timeout` int NOT NULL DEFAULT 600 COMMENT '超时时间，单位秒',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `UK_AUTO_INDEX_CODE`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for auto_task
-- ----------------------------
DROP TABLE IF EXISTS `auto_task`;
CREATE TABLE `auto_task`  (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                              `index_id` bigint NOT NULL COMMENT '自动任务id',
                              `user_id` bigint NOT NULL COMMENT '用户id',
                              `only_id` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务唯一id标识，用于判断任务是否存在',
                              `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '自动任务类型',
                              `enable` int NOT NULL DEFAULT 1 COMMENT '是否开启，0不开启 1开启',
                              `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
                              `settings` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置属性JSON字符串',
                              `last_end_time` datetime NULL DEFAULT NULL COMMENT '最后任务完成时间',
                              `last_end_status` int NULL DEFAULT NULL COMMENT '最后任务状态',
                              `user_infos` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务信息储存JSON字符串（通常用于前端展示UID等级等信息）',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `UK_AUTO_TASK_UNIQUE`(`user_id`, `only_id`, `code`) USING BTREE,
                              INDEX `IDX_AUTO_TASK_ONLYID_CODE`(`only_id`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for log_hi_task
-- ----------------------------
DROP TABLE IF EXISTS `log_hi_task`;
CREATE TABLE `log_hi_task`  (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                `user_id` bigint NOT NULL COMMENT '任务所属的用户id',
                                `task_id` bigint NOT NULL COMMENT '任务id',
                                `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务类型',
                                `status` int NULL DEFAULT NULL COMMENT '任务状态',
                                `date` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
                                `text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '日志内容',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `IDX_HI_TASK_USER_TYPE_DATE`(`user_id`, `date`, `type`) USING BTREE,
                                INDEX `IDX_HI_TASK_DATE`(`date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for log_push_result
-- ----------------------------
DROP TABLE IF EXISTS `log_push_result`;
CREATE TABLE `log_push_result`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
                                    `log_id` bigint NULL DEFAULT NULL COMMENT '日志id',
                                    `success` int NULL DEFAULT NULL COMMENT '是否成功 0失败 1成功',
                                    `data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '推送结果数据',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `IDX_LOG_RES_USERID`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of log_push_result
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'key',
                               `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'value',
                               `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '中文名称解释',
                               `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `INDEX_UNIQUE_bond`(`key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'system_notice_content', NULL, '系统公告文本', '备注测试');
INSERT INTO `sys_config` VALUES (2, 'system_reg_enable', 'true', '是否开放注册', NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                             `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
                             `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色唯一code',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `UK_SYSROLE_CODE`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '系统管理员', 'ADMIN');
INSERT INTO `sys_role` VALUES (2, '普通用户', 'USER');

-- ----------------------------
-- Table structure for sys_role_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_user`;
CREATE TABLE `sys_role_user`  (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                  `user_id` bigint NOT NULL COMMENT '用户id',
                                  `role_id` bigint NOT NULL COMMENT '角色id',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `sys_user_id`(`user_id`) USING BTREE,
                                  INDEX `sys_role_id`(`role_id`) USING BTREE,
                                  CONSTRAINT `sys_role_user_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                  CONSTRAINT `sys_role_user_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                             `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                             `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                             `regdate` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `UK_SYS_USER_USERNAME`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_webhook
-- ----------------------------
DROP TABLE IF EXISTS `sys_webhook`;
CREATE TABLE `sys_webhook`  (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `user_id` bigint NOT NULL COMMENT '用户id',
                                `enable` int NOT NULL DEFAULT 1 COMMENT '是否启用',
                                `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注名称',
                                `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '推送类型',
                                `data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '数据json',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `IDX_SYS_WEBHOOK_USERID`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_sys_quartz_job
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_quartz_job`;
CREATE TABLE `t_sys_quartz_job`  (
                                     `id` bigint NOT NULL COMMENT '主键id',
                                     `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
                                     `invoke_target` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '调用目标字符串',
                                     `cron_expression` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cron执行表达式',
                                     `concurrent` int NULL DEFAULT NULL COMMENT '是否并发执行（0允许 1禁止）',
                                     `status` int NULL DEFAULT NULL COMMENT '任务状态（0正常 1暂停）',
                                     `timeout` int NULL DEFAULT NULL COMMENT '任务超时',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_quartz_job_log
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_quartz_job_log`;
CREATE TABLE `t_sys_quartz_job_log`  (
                                         `id` bigint NOT NULL COMMENT '主键',
                                         `job_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
                                         `job_group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务组名',
                                         `invoke_target` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '调用目标字符串',
                                         `job_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志信息',
                                         `status` int NULL DEFAULT NULL COMMENT '执行状态（0正常 1失败）',
                                         `exception_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '异常信息',
                                         `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
                                         `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
