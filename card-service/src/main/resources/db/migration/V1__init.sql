DROP TABLE IF EXISTS `card`;
CREATE TABLE IF NOT EXISTS `card`(
    id INT NOT NULL PRIMARY KEY,
    card_number VARCHAR(255),
    card_type VARCHAR(255),
    fk_user_id INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN
) ENGINE = INNODB;