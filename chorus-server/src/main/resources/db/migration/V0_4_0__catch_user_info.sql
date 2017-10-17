
ALTER TABLE `job_fail_history`
ADD COLUMN `email`  varchar(255) NULL AFTER `send_time`,
ADD COLUMN `error`  text NULL AFTER `email`;

ALTER TABLE `t_user`
DROP COLUMN `user_type`,
DROP COLUMN `user_status`,
DROP COLUMN `user_rank`,
DROP COLUMN `user_account`,
DROP COLUMN `password`,
DROP COLUMN `company`,
DROP COLUMN `department`,
DROP COLUMN `tel`,
DROP COLUMN `mobile`,
DROP COLUMN `qq`,
DROP COLUMN `sys_yn`,
DROP COLUMN `use_yn`,
DROP COLUMN `is_active`,
DROP COLUMN `active_key`,
DROP COLUMN `remark`,
DROP COLUMN `create_time`,
DROP COLUMN `update_time`,
DROP COLUMN `rop_user_id`,
DROP COLUMN `lib_name`,
DROP COLUMN `role_id`,
DROP COLUMN `token`,
DROP COLUMN `queue`;

--
-- add column to record atlas entity guid
--
ALTER TABLE `resource_out`
  ADD COLUMN `guid` VARCHAR(50) NOT NULL DEFAULT '';
ALTER TABLE `resource_out`
  ADD UNIQUE INDEX `U_PROJECT_NAME_USER_URL`(`project_id`, `resource_name`, `conn_url`, `conn_user`);

ALTER TABLE `task_execution_timeout`
ADD COLUMN `email_status`  varchar(16) NULL AFTER `create_time`,
ADD COLUMN `email`  varchar(255) NULL AFTER `email_status`,
ADD COLUMN `error`  text NULL AFTER `email`,
ADD COLUMN `job_name`  varchar(50) NULL AFTER `error`;

--
-- add enabled column to authorization_detail.
--
ALTER TABLE authorization_detail ADD enabled TINYINT DEFAULT 1 NOT NULL COMMENT '1:enabled,0:disabled';
--
-- add category column to authorization_detail.
--
ALTER TABLE authorization_detail ADD category varchar(16) DEFAULT 'HIVE' NOT NULL COMMENT 'HIVE,HDFS....';

ALTER TABLE `task_execution_timeout`
ADD COLUMN `id`  bigint NOT NULL AUTO_INCREMENT AFTER `job_name`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`);

