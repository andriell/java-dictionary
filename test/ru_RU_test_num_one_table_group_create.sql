CREATE TABLE `dic_words_group` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`group_id` INT(11) NOT NULL,
	`is_lemma` TINYINT(4) NOT NULL,
	`word` VARCHAR(255) NOT NULL,
	PRIMARY KEY (`id`)
);
