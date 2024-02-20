DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user`(
    account_number VARCHAR(10) NOT NULL PRIMARY KEY,
    balance INT,
    name VARCHAR(255),
    pin VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN
) ENGINE = INNODB;