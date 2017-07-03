DROP TABLE IF EXISTS `company`;
CREATE TABLE company (
	id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(80) NOT NULL,
	contact_name VARCHAR(150),
	contact_email VARCHAR(150),
	max_accounts INTEGER,
	max_size INTEGER,
	PRIMARY KEY (`id`),
	UNIQUE KEY `name` (`name`)
);

DROP TABLE IF EXISTS `user`;
CREATE TABLE user (
	id INTEGER NOT NULL AUTO_INCREMENT,
	company_name VARCHAR(150),
	login VARCHAR(80) NOT NULL,
	password VARCHAR(80),
	quota INTEGER,
	enabled TINYINT(1),
	PRIMARY KEY (`id`),
	UNIQUE KEY `login` (`login`)
);