CREATE TABLE `ie_user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '序列ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_time` datetime DEFAULT NULL comment '修改时间',
  `user_name` varchar(169) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户姓名',
  `user_age` int(11) unsigned default 0 COMMENT '用户年龄',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE `ie_student` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '序列ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_time` datetime DEFAULT NULL comment '修改时间',
  `user_id` int(11) unsigned  NOT NULL default 0 COMMENT '用户ID',
  `class_name` varchar(169) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '班级名称',
  PRIMARY KEY (`id`),
  CONSTRAINT `ie_student_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `ie_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

CREATE TABLE `ie_student_subject` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '序列ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_time` datetime DEFAULT NULL comment '修改时间',
  `student_id` int(11) unsigned  NOT NULL default 0 COMMENT '学生ID',
  `subject_name` varchar(169) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '学科名称',
  `subject_teacher` varchar(169) COLLATE utf8mb4_unicode_ci DEFAULT null COMMENT '老师名称',
  PRIMARY KEY (`id`),
  CONSTRAINT `ie_student_subject_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `ie_student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生学科表';

CREATE TRIGGER `ie_user_before_update` BEFORE UPDATE ON `ie_user` FOR EACH ROW SET NEW.`modified_time` = NOW();
CREATE TRIGGER `ie_student_before_update` BEFORE UPDATE ON `ie_student` FOR EACH ROW SET NEW.`modified_time` = NOW();
CREATE TRIGGER `ie_student_subject_before_update` BEFORE UPDATE ON `ie_student_subject` FOR EACH ROW SET NEW.`modified_time` = NOW();