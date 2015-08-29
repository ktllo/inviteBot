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

CREATE INDEX member_hostmask_hostmask ON member_hostmask(hostmask(200));