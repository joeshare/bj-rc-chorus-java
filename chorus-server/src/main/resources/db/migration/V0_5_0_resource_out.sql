--
-- add ftp/sftp path column to resource_out
--
ALTER TABLE `resource_out`
  ADD COLUMN `path` varchar(200) DEFAULT NULL COMMENT 'ftp/sftp 目录路径',
  ADD COLUMN `connect_mode` varchar(2) DEFAULT NULL COMMENT 'ftp/sftp 连接模式1：被动连接 2：主动连接';