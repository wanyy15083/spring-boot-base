-- ----------------------------
-- test Table
-- ----------------------------
CREATE TABLE `person` (
  `id` bigint(20) unsigned NOT NULL,
  `fname` varchar(100) NOT NULL,
  `lname` varchar(100) NOT NULL,
  `age` tinyint(3) unsigned NOT NULL,
  `sex` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8

CREATE DEFINER=`root`@`localhost` PROCEDURE `generate`(IN num INT)
BEGIN
	DECLARE chars varchar(100) DEFAULT 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
	DECLARE fname VARCHAR(25) DEFAULT '';
	DECLARE lname VARCHAR(25) DEFAULT '';
	DECLARE id int UNSIGNED;
	DECLARE len int;
	set id=1;
	DELETE from person;
	WHILE id <= num DO
		set len = FLOOR(1 + RAND()*25);
		set fname = '';
		WHILE len > 0 DO
			SET fname = CONCAT(fname,substring(chars,FLOOR(1 + RAND()*62),1));
			SET len = len - 1;
		END WHILE;
		set len = FLOOR(1+RAND()*25);
		set lname = '';
		WHILE len > 0 DO
			SET lname = CONCAT(fname,SUBSTR(chars,FLOOR(1 + RAND()*62),1));
			SET len = len - 1;
		END WHILE;
		INSERT into person VALUES (id,fname,lname, FLOOR(RAND()*100), FLOOR(RAND()*2));
		set id = id + 1;
	END WHILE;
END

CALL generate(10000)

