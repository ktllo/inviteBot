DROP DATABASE IF EXISTS inviteBot;
CREATE DATABASE IF NOT EXISTS inviteBot;
USE invitebot;
CREATE TABLE IF NOT EXISTS member(
	member_id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE KEY,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS member_hostmask(
	member_id BIGINT UNSIGNED NOT NULL,
    hostmask_id INT UNSIGNED NOT NULL,
    hostmask VARCHAR(512),
    CONSTRAINT PRIMARY KEY(member_id,hostmask_id)
);

CREATE TABLE IF NOT EXISTS config(
	`key` VARCHAR(255) NOT NULL PRIMARY KEY,
	`value` TEXT NOT NULL
);

CREATE INDEX member_hostmask_hostmask ON member_hostmask(hostmask(200));

CREATE TABLE role(
	role_id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
	rolename VARCHAR(255) NOT NULL UNIQUE KEY
);

CREATE TABLE role_relationship(
	parent_role BIGINT UNSIGNED NOT NULL,
	child_role BIGINT UNSIGNED NOT NULL,
	CONSTRAINT PRIMARY KEY(parent_role,child_role),
	CONSTRAINT FOREIGN KEY(parent_role) REFERENCES role(role_id),
	CONSTRAINT FOREIGN KEY(child_role) REFERENCES role(role_id)
);

CREATE TABLE permission(
	permission_id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    permission_name VARCHAR(255) NOT NULL UNIQUE KEY,
    permission_TYPE INT UNSIGNED
);

CREATE TABLE role_permission(
	permission_id BIGINT UNSIGNED NOT NULL,
    member_id BIGINT UNSIGNED NOT NULL,
    CONSTRAINT PRIMARY KEY(permission_id,member_id),
    CONSTRAINT FOREIGN KEY(permission_id) REFERENCES permission(permission_id),
    CONSTRAINT FOREIGN KEY(member_id) REFERENCES member(member_id)
);

INSERT INTO role(role_id,rolename) VALUES (1, 'Root'),(2,'SuperAdministrator');
INSERT INTO role_relationship(parent_role,child_role) VALUE(1,2);