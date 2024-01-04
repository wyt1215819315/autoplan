ALTER TABLE `log_push_result`
    CHANGE COLUMN `task_id` `log_id` bigint NULL DEFAULT NULL COMMENT '日志id' AFTER `user_id`,
    DROP INDEX `IDX_LOG_RES_TASKID`,
    ADD INDEX `IDX_LOG_RES_LOGID` (`log_id`) USING BTREE;

ALTER TABLE `log_push_result`
    ADD COLUMN `webhook_id` bigint NULL COMMENT 'webhook Id' AFTER `log_id`,
    ADD INDEX `IDX_LOG_RES_WEBHOOKID`(`webhook_id`);