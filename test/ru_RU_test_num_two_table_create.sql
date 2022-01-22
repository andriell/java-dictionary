CREATE TABLE `dic_lemma` (
	`id` INT(11) NOT NULL,
	`word` VARCHAR(255) NOT NULL,
	PRIMARY KEY (`id`)
);
CREATE TABLE `dic_word` (
	`dic_lemma_id` INT(11) NOT NULL,
	`word` VARCHAR(255) NOT NULL
);
