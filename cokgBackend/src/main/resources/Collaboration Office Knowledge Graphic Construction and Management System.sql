CREATE TABLE IF NOT EXISTS `user` (
	`user_id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT '用户id',
	`name` VARCHAR(20) NOT NULL UNIQUE COMMENT '邮箱',
	`password` VARCHAR(20) NOT NULL COMMENT '密码',
	`dept_id` INTEGER NOT NULL COMMENT '所属部门id,关联dept.dept_id',
	`code` VARCHAR(20) NOT NULL UNIQUE COMMENT '工号: 部门id+用户在部门中的序号',
	`role` TINYINT NOT NULL DEFAULT 0 COMMENT '登录角色(0-管理员, 1-普通用户)',
	`create_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间',
	`is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除 0:未删除;1:已删除',
	PRIMARY KEY(`user_id`)
) COMMENT='用户表';


CREATE TABLE IF NOT EXISTS `dept` (
	`dept_id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT '部门id',
	`name` VARCHAR(20) NOT NULL UNIQUE COMMENT '部门名',
	`create_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间',
	`is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除 0:未删除;1:已删除',
	PRIMARY KEY(`dept_id`)
) COMMENT='部门表';


CREATE TABLE IF NOT EXISTS `kg_meta` (
	`kg_meta_id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT '图谱核心数据id',
	`kg_name` VARCHAR(100) NOT NULL COMMENT '图谱名称',
	`kg_type` TINYINT NOT NULL DEFAULT 0 COMMENT '图谱类型(0-个人, 1-部门)',
	`dept_id` BIGINT NOT NULL COMMENT '所属部门(个人创建填所属部门id)',
	`create_user_id` BIGINT NOT NULL COMMENT '创建者id,关联user.id',
	`status` TINYINT DEFAULT 1 COMMENT '状态(0-草稿, 1-已发布)',
	`create_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '更新时间',
	`is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
	PRIMARY KEY(`kg_meta_id`)
) COMMENT='图谱核心数据表';


CREATE TABLE IF NOT EXISTS `kg_share` (
	`kg_share_id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT 'id',
	`kg_id` BIGINT NOT NULL COMMENT '图谱id,关联kg_meta.kg_meta_id',
	`share_user_id` BIGINT NOT NULL COMMENT '关联user.user_id,表示某个知识图谱可被谁看见',
	`permission` TINYINT NOT NULL DEFAULT 0 COMMENT '权限(0-仅查看, 1-可编辑)',
	`create_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间',
	PRIMARY KEY(`kg_share_id`)
) COMMENT='知识图谱共享表';


CREATE TABLE IF NOT EXISTS `doc` (
	`doc_id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT '文档id',
	`name` VARCHAR(30) NOT NULL,
	`doc_type` VARCHAR(20) NOT NULL COMMENT '文档类型',
	`file_path` VARCHAR(255) NOT NULL COMMENT '文件路径',
	`create_user_id` BIGINT NOT NULL COMMENT '创建者id,关联user.user_id',
	`kg_node_id` VARCHAR(50) NOT NULL COMMENT '关联kg_node.kg_node_id',
	`create_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间',
	`is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
	PRIMARY KEY(`doc_id`)
);


CREATE TABLE IF NOT EXISTS `kg_node` (
	`kg_node_id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE COMMENT 'id',
	`kg_id` BIGINT NOT NULL COMMENT '所属图谱id',
	`node_type` VARCHAR(30) NOT NULL COMMENT '节点类型(Employee, Department等)',
	`node_name` VARCHAR(50) NOT NULL COMMENT '节点名称,如: "张三", "研发部", "毕业设计文档"',
	`node_attr` VARCHAR(500) NOT NULL COMMENT '节点属性(JSON, 如: {"工号":"001", "职位": "经理"})',
	`create_user_id` BIGINT NOT NULL COMMENT '关联userid',
	`is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
	PRIMARY KEY(`kg_node_id`)
) COMMENT='知识图谱节点表';


CREATE TABLE IF NOT EXISTS `kg_relation` (
	`id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE COMMENT 'id',
	`kg_id` BIGINT NOT NULL COMMENT '所属图谱id,关联kg_meta.kg_meta_id',
	`src_node_id` BIGINT NOT NULL COMMENT '起始节点id:kg_node.kg_node_id',
	`target_node_id` BIGINT NOT NULL COMMENT '目标节点id',
	`relation_type` VARCHAR(30) NOT NULL COMMENT '关系类型,如:属于,创建...',
	`relation_attr` VARCHAR(200) NOT NULL COMMENT '关系属性:JSON格式',
	`create_user_id` BIGINT NOT NULL COMMENT '创建者id',
	`create_time` DATETIME NOT NULL DEFAULT NOW(),
	`is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
	PRIMARY KEY(`id`)
) COMMENT='知识图谱关系表';


