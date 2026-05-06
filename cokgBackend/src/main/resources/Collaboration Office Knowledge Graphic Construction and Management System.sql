-- 1. 部门表（保持不变）
CREATE TABLE IF NOT EXISTS `dept`
(
    `dept_id`     BIGINT      NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(50) NOT NULL UNIQUE,
    `parent_id`   BIGINT      NULL COMMENT '父部门id，支持部门树形结构',
    `create_time` DATETIME    NOT NULL DEFAULT NOW(),
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`dept_id`),
    INDEX `idx_parent_id` (`parent_id`)
) COMMENT ='部门表';

-- 2. 用户表（优化）
CREATE TABLE IF NOT EXISTS `user`
(
    `user_id`     BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名/登录名',
    `password`    VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    `real_name`   VARCHAR(20)  NOT NULL COMMENT '真实姓名',
    `email`       VARCHAR(100) NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)  NULL COMMENT '手机号',
    `avatar`      VARCHAR(255) NULL COMMENT '头像URL',
    `dept_id`     BIGINT       NOT NULL,
    `employee_no` VARCHAR(20)  NOT NULL UNIQUE COMMENT '工号',
    `role`        TINYINT      NOT NULL DEFAULT 1 COMMENT '0-超级管理员 1-普通用户 2-部门管理员',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
    `last_login`  DATETIME     NULL,
    `create_time` DATETIME     NOT NULL DEFAULT NOW(),
    `update_time` DATETIME     NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`user_id`),
    FOREIGN KEY (`dept_id`) REFERENCES `dept` (`dept_id`),
    INDEX `idx_dept` (`dept_id`),
    INDEX `idx_status` (`status`)
) COMMENT ='用户表';

-- 3. 实体类型定义表（新增）
CREATE TABLE IF NOT EXISTS `entity_type`
(
    `type_id`     INT         NOT NULL AUTO_INCREMENT,
    `type_code`   VARCHAR(30) NOT NULL UNIQUE COMMENT '类型代码，如：EMPLOYEE, DEPARTMENT, DOCUMENT',
    `type_name`   VARCHAR(50) NOT NULL COMMENT '类型名称，如：员工、部门、文档',
    `icon`        VARCHAR(50) NULL COMMENT '前端图标',
    `color`       VARCHAR(20) NULL COMMENT '节点颜色',
    `schema`      JSON        NOT NULL COMMENT '属性JSON Schema模板',
    `sort_order`  INT         NOT NULL DEFAULT 0,
    `is_system`   TINYINT     NOT NULL DEFAULT 0 COMMENT '是否系统预设类型',
    `create_time` DATETIME    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (`type_id`)
) COMMENT ='实体类型定义表';

-- 4. 关系类型定义表（新增）
CREATE TABLE IF NOT EXISTS `relation_type`
(
    `rel_type_id` INT         NOT NULL AUTO_INCREMENT,
    `rel_code`    VARCHAR(30) NOT NULL UNIQUE COMMENT '关系代码，如：BELONGS_TO, CREATED_BY',
    `rel_name`    VARCHAR(50) NOT NULL COMMENT '关系名称，如：属于、创建',
    `source_type` VARCHAR(30) NOT NULL COMMENT '允许的源实体类型',
    `target_type` VARCHAR(30) NOT NULL COMMENT '允许的目标实体类型',
    `directed`    TINYINT     NOT NULL DEFAULT 1 COMMENT '是否有向',
    `color`       VARCHAR(20) NULL COMMENT '线条颜色',
    `create_time` DATETIME    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (`rel_type_id`)
) COMMENT ='关系类型定义表';

-- 5. 知识图谱元数据表（优化）
CREATE TABLE IF NOT EXISTS `kg_meta`
(
    `kg_id`       BIGINT       NOT NULL AUTO_INCREMENT,
    `kg_name`     VARCHAR(100) NOT NULL,
    `kg_desc`     TEXT         NULL,
    `kg_type`     TINYINT      NOT NULL DEFAULT 0 COMMENT '0-个人图谱 1-部门图谱 2-项目图谱',
    `scope_type`  TINYINT      NOT NULL DEFAULT 0 COMMENT '0-私有 1-部门共享 2-公开',
    `dept_id`     BIGINT       NULL COMMENT '所属部门（部门图谱时使用）',
    `project_id`  BIGINT       NULL COMMENT '关联项目（项目图谱时使用）',
    `owner_id`    BIGINT       NOT NULL COMMENT '所有者',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '0-草稿 1-已发布 2-归档',
    `version`     INT          NOT NULL DEFAULT 1 COMMENT '版本号',
    `thumbnail`   VARCHAR(255) NULL COMMENT '缩略图',
    `config`      JSON         NULL COMMENT '图谱配置',
    `create_time` DATETIME     NOT NULL DEFAULT NOW(),
    `update_time` DATETIME     NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`kg_id`),
    FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`),
    FOREIGN KEY (`dept_id`) REFERENCES `dept` (`dept_id`),
    INDEX `idx_owner` (`owner_id`),
    INDEX `idx_dept_status` (`dept_id`, `status`)
) COMMENT ='知识图谱元数据表';

-- 6. 知识图谱节点表（优化）
CREATE TABLE IF NOT EXISTS `kg_node`
(
    `kg_node_id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `kg_meta_id`          BIGINT       NOT NULL,
    `node_type`      VARCHAR(30)  NOT NULL COMMENT '引用entity_type.type_code',
    `node_name`      VARCHAR(100) NOT NULL,
    `node_desc`      VARCHAR(500) NULL,
    `properties`     JSON         NOT NULL COMMENT '节点属性',
    `tags`           JSON         NULL COMMENT '标签数组',
    `position_x`     DOUBLE       NULL COMMENT '可视化位置X',
    `position_y`     DOUBLE       NULL COMMENT '可视化位置Y',
    `size`           INT          NULL COMMENT '节点大小',
    `create_user_id` BIGINT       NOT NULL,
    `update_user_id` BIGINT       NULL,
    `create_time`    DATETIME     NOT NULL DEFAULT NOW(),
    `update_time`    DATETIME     NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    `is_deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`kg_node_id`),
    FOREIGN KEY (`kg_meta_id`) REFERENCES `kg_meta` (`kg_id`),
    FOREIGN KEY (`create_user_id`) REFERENCES `user` (`user_id`),
    FOREIGN KEY (`update_user_id`) REFERENCES `user` (`user_id`),
    INDEX `idx_kg_id` (`kg_meta_id`),
    INDEX `idx_node_type` (`node_type`),
    FULLTEXT INDEX `idx_search` (`node_name`, `node_desc`) -- 全文索引
) COMMENT ='知识图谱节点表';

-- 7. 知识图谱关系表（优化）
CREATE TABLE IF NOT EXISTS `kg_relation`
(
    `kg_relation_id`    BIGINT        NOT NULL AUTO_INCREMENT,
    `kg_meta_id`          BIGINT        NOT NULL,
    `kg_node_src_id`      BIGINT        NOT NULL,
    `kg_node_target_id`      BIGINT        NOT NULL,
    `relation_type`  VARCHAR(30)   NOT NULL COMMENT '引用relation_type.rel_code',
    `properties`     JSON          NOT NULL,
    `weight`         DECIMAL(3, 2) NULL     DEFAULT 1.0 COMMENT '关系权重',
    `create_user_id` BIGINT        NOT NULL,
    `create_time`    DATETIME      NOT NULL DEFAULT NOW(),
    `update_time`    DATETIME      NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    `is_deleted`     TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (`kg_relation_id`),
    UNIQUE KEY `uk_relation` (`kg_meta_id`, `kg_node_src_id`, `kg_node_target_id`, `relation_type`), -- 防止重复关系
    INDEX `idx_source_target` (`kg_node_src_id`, `kg_node_target_id`),
    INDEX `idx_kg_relation` (`kg_meta_id`, `relation_type`)
) COMMENT ='知识图谱关系表';

-- 8. 文档表（优化）
CREATE TABLE IF NOT EXISTS `doc`
(
    `doc_id`       BIGINT       NOT NULL AUTO_INCREMENT,
    `doc_name`     VARCHAR(100) NOT NULL,
    `doc_type`     VARCHAR(50)  NOT NULL COMMENT '如：pdf, docx, txt',
    `file_path`    VARCHAR(500) NOT NULL,
    `file_size`    BIGINT       NOT NULL COMMENT '文件大小（字节）',
    `mime_type`    VARCHAR(100) NOT NULL,
    `thumbnail`    VARCHAR(500) NULL COMMENT '缩略图路径',
    `content_text` LONGTEXT     NULL COMMENT '提取的文本内容（用于搜索）',
    `owner_id`     BIGINT       NOT NULL,
    `dept_id`      BIGINT       NULL,
    `permission`   TINYINT      NOT NULL DEFAULT 0 COMMENT '0-私有 1-部门共享 2-公开',
    `version`      INT          NOT NULL DEFAULT 1,
    `create_time`  DATETIME     NOT NULL DEFAULT NOW(),
    `update_time`  DATETIME     NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    `is_deleted`   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`doc_id`),
    FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`),
    FOREIGN KEY (`dept_id`) REFERENCES `dept` (`dept_id`),
    INDEX `idx_owner` (`owner_id`),
    FULLTEXT INDEX `idx_content` (`doc_name`, `content_text`)
) COMMENT ='文档表';

-- 9. 文档-节点关联表（新增，支持多对多）
CREATE TABLE IF NOT EXISTS `doc_node_rel`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `doc_id`      BIGINT      NOT NULL,
    `node_id`     BIGINT      NOT NULL,
    `rel_type`    VARCHAR(30) NOT NULL DEFAULT 'RELATED_TO' COMMENT '关联类型',
    `create_time` DATETIME    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_doc_node` (`doc_id`, `node_id`)
) COMMENT ='文档-节点关联表';

-- 10. 知识图谱共享表（优化）
CREATE TABLE IF NOT EXISTS `kg_share`
(
    `share_id`       BIGINT   NOT NULL AUTO_INCREMENT,
    `kg_id`          BIGINT   NOT NULL,
    `user_id`        BIGINT   NOT NULL COMMENT '被共享的用户',
    `permission`     TINYINT  NOT NULL DEFAULT 0 COMMENT '0-查看 1-编辑 2-管理',
    `share_type`     TINYINT  NOT NULL DEFAULT 0 COMMENT '0-个人共享 1-部门继承',
    `expire_time`    DATETIME NULL COMMENT '过期时间',
    `create_user_id` BIGINT   NOT NULL COMMENT '共享发起人',
    `create_time`    DATETIME NOT NULL DEFAULT NOW(),
    `update_time`    DATETIME NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    `is_deleted`     TINYINT  NOT NULL DEFAULT 0,
    PRIMARY KEY (`share_id`),
    FOREIGN KEY (`kg_id`) REFERENCES `kg_meta` (`kg_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    FOREIGN KEY (`create_user_id`) REFERENCES `user` (`user_id`),
    UNIQUE KEY `uk_kg_user` (`kg_id`, `user_id`),
    INDEX `idx_user_kg` (`user_id`, `kg_id`)
) COMMENT ='知识图谱共享表';

-- 11. 操作日志表（新增）
CREATE TABLE IF NOT EXISTS `operation_log`
(
    `log_id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT       NOT NULL,
    `operation_type` VARCHAR(50)  NOT NULL COMMENT '操作类型',
    `target_type`    VARCHAR(50)  NOT NULL COMMENT '操作对象类型',
    `target_id`      BIGINT       NULL COMMENT '操作对象ID',
    `kg_id`          BIGINT       NULL COMMENT '关联的知识图谱',
    `details`        JSON         NULL COMMENT '操作详情',
    `ip_address`     VARCHAR(45)  NULL,
    `user_agent`     VARCHAR(500) NULL,
    `create_time`    DATETIME     NOT NULL DEFAULT NOW(),
    PRIMARY KEY (`log_id`),
    INDEX `idx_user_time` (`user_id`, `create_time`),
    INDEX `idx_kg_time` (`kg_id`, `create_time`)
) COMMENT ='操作日志表';

-- 12. 系统配置表（新增）
CREATE TABLE IF NOT EXISTS `system_config`
(
    `config_id`    BIGINT       NOT NULL AUTO_INCREMENT,
    `config_key`   VARCHAR(100) NOT NULL UNIQUE,
    `config_value` TEXT         NOT NULL,
    `config_desc`  VARCHAR(200) NULL,
    `is_system`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否系统配置',
    `create_time`  DATETIME     NOT NULL DEFAULT NOW(),
    `update_time`  DATETIME     NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    PRIMARY KEY (`config_id`)
) COMMENT ='系统配置表';