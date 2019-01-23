/*
[
    "account", 
    "account_role", 
    "application", 
    "article", 
    "article_comment", 
    "audio", 
    "auth_code", 
    "course", 
    "issue", 
    "issue_comment", 
    "paragraph", 
    "paragraph_comment", 
    "permission", 
    "preset_text", 
    "role", 
    "role_permission", 
    "section", 
    "session", 
    "video"
]
[
    "account_l", 
    "account_role_l", 
    "application_l", 
    "article_l", 
    "article_comment_l", 
    "audio_l", 
    "auth_code_l", 
    "course_l", 
    "issue_l", 
    "issue_comment_l", 
    "paragraph_l", 
    "paragraph_comment_l", 
    "permission_l", 
    "preset_text_l", 
    "role_l", 
    "role_permission_l", 
    "section_l", 
    "session_l", 
    "video_l"
]
[
    "account_t", 
    "account_role_t", 
    "application_t", 
    "article_t", 
    "article_comment_t", 
    "audio_t", 
    "auth_code_t", 
    "course_t", 
    "issue_t", 
    "issue_comment_t", 
    "paragraph_t", 
    "paragraph_comment_t", 
    "permission_t", 
    "preset_text_t", 
    "role_t", 
    "role_permission_t", 
    "section_t", 
    "session_t", 
    "video_t"
]
[
    "account_m", 
    "account_role_m", 
    "application_m", 
    "article_m", 
    "article_comment_m", 
    "audio_m", 
    "auth_code_m", 
    "course_m", 
    "issue_m", 
    "issue_comment_m", 
    "paragraph_m", 
    "paragraph_comment_m", 
    "permission_m", 
    "preset_text_m", 
    "role_m", 
    "role_permission_m", 
    "section_m", 
    "session_m", 
    "video_m"
]

*/



CREATE OR REPLACE VIEW `moshi`.`account_l`
    AS
(
    SELECT
            `id`, 
            `nickName`, 
            `password`, 
            `email`, 
            `phone`, 
            `avatar`, 
            `realName`, 
            `identityNumber`, 
            `age`, 
            `company`, 
            `position`, 
            `personalProfile`, 
            `sex`, 
            `birthday`, 
            `education`, 
            `profession`, 
            `createAt`, 
            `status`
    from `moshi`.`account`
);


CREATE OR REPLACE VIEW `moshi`.`account_role_l`
    AS
(
    SELECT
            `accountId`, 
            `roleId`
    from `moshi`.`account_role`
);


CREATE OR REPLACE VIEW `moshi`.`application_l`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `category`, 
            `title`, 
            `content`, 
            `createAt`, 
            `status`, 
            `reply`
    from `moshi`.`application`
);


CREATE OR REPLACE VIEW `moshi`.`article_l`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `title`, 
            `content`, 
            `createAt`, 
            `status`, 
            `audioId`
    from `moshi`.`article`
);


CREATE OR REPLACE VIEW `moshi`.`article_comment_l`
    AS
(
    SELECT
            `id`, 
            `articleId`, 
            `accountId`, 
            `content`, 
            `createAt`, 
            `status`, 
            `replyTo`
    from `moshi`.`article_comment`
);


CREATE OR REPLACE VIEW `moshi`.`audio_l`
    AS
(
    SELECT
            `id`, 
            `resource`, 
            `recorder`, 
            `accountId`, 
            `status`
    from `moshi`.`audio`
);


CREATE OR REPLACE VIEW `moshi`.`auth_code_l`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `expireAt`, 
            `type`
    from `moshi`.`auth_code`
);


CREATE OR REPLACE VIEW `moshi`.`course_l`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `columnName`, 
            `introduce`, 
            `introduceImage`, 
            `note`, 
            `createAt`, 
            `publishAt`, 
            `buyerCount`, 
            `courseType`, 
            `price`, 
            `discountedPrice`, 
            `offerTo`, 
            `status`
    from `moshi`.`course`
);


CREATE OR REPLACE VIEW `moshi`.`issue_l`
    AS
(
    SELECT
            `id`, 
            `courseId`, 
            `accountId`, 
            `title`, 
            `createAt`, 
            `status`
    from `moshi`.`issue`
);


CREATE OR REPLACE VIEW `moshi`.`issue_comment_l`
    AS
(
    SELECT
            `id`, 
            `issueId`, 
            `accountId`, 
            `createAt`, 
            `content`, 
            `status`
    from `moshi`.`issue_comment`
);


CREATE OR REPLACE VIEW `moshi`.`paragraph_l`
    AS
(
    SELECT
            `id`, 
            `sectionId`, 
            `accountId`, 
            `vedioId`, 
            `content`, 
            `createAt`, 
            `status`
    from `moshi`.`paragraph`
);


CREATE OR REPLACE VIEW `moshi`.`paragraph_comment_l`
    AS
(
    SELECT
            `id`, 
            `paragraphId`, 
            `accountId`, 
            `content`, 
            `createAt`, 
            `status`, 
            `replyTo`
    from `moshi`.`paragraph_comment`
);


CREATE OR REPLACE VIEW `moshi`.`permission_l`
    AS
(
    SELECT
            `id`, 
            `actionKey`, 
            `controller`, 
            `remark`
    from `moshi`.`permission`
);


CREATE OR REPLACE VIEW `moshi`.`preset_text_l`
    AS
(
    SELECT
            `key`, 
            `value`
    from `moshi`.`preset_text`
);


CREATE OR REPLACE VIEW `moshi`.`role_l`
    AS
(
    SELECT
            `id`, 
            `name`, 
            `createAt`
    from `moshi`.`role`
);


CREATE OR REPLACE VIEW `moshi`.`role_permission_l`
    AS
(
    SELECT
            `roleId`, 
            `permissionId`
    from `moshi`.`role_permission`
);


CREATE OR REPLACE VIEW `moshi`.`section_l`
    AS
(
    SELECT
            `id`, 
            `courseId`, 
            `accountId`, 
            `title`, 
            `createAt`, 
            `status`
    from `moshi`.`section`
);


CREATE OR REPLACE VIEW `moshi`.`session_l`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `expireAt`
    from `moshi`.`session`
);


CREATE OR REPLACE VIEW `moshi`.`video_l`
    AS
(
    SELECT
            `id`, 
            `resource`, 
            `recorder`, 
            `accountId`, 
            `status`
    from `moshi`.`video`
);




CREATE OR REPLACE VIEW `moshi`.`account_t`
    AS
(
    SELECT
            `id`, 
            `nickName`, 
            `password`, 
            `email`, 
            `phone`, 
            `avatar`, 
            `realName`, 
            `identityNumber`, 
            `age`, 
            `company`, 
            `position`, 
            `personalProfile`, 
            `sex`, 
            `birthday`, 
            `education`, 
            `profession`, 
            `createAt`, 
            `status`
    from `moshi`.`account`
);


CREATE OR REPLACE VIEW `moshi`.`account_role_t`
    AS
(
    SELECT
            `accountId`, 
            `roleId`
    from `moshi`.`account_role`
);


CREATE OR REPLACE VIEW `moshi`.`application_t`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `category`, 
            `title`, 
            `content`, 
            `createAt`, 
            `status`, 
            `reply`
    from `moshi`.`application`
);


CREATE OR REPLACE VIEW `moshi`.`article_t`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `title`, 
            `content`, 
            `createAt`, 
            `status`, 
            `audioId`
    from `moshi`.`article`
);


CREATE OR REPLACE VIEW `moshi`.`article_comment_t`
    AS
(
    SELECT
            `id`, 
            `articleId`, 
            `accountId`, 
            `content`, 
            `createAt`, 
            `status`, 
            `replyTo`
    from `moshi`.`article_comment`
);


CREATE OR REPLACE VIEW `moshi`.`audio_t`
    AS
(
    SELECT
            `id`, 
            `resource`, 
            `recorder`, 
            `accountId`, 
            `status`
    from `moshi`.`audio`
);


CREATE OR REPLACE VIEW `moshi`.`auth_code_t`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `expireAt`, 
            `type`
    from `moshi`.`auth_code`
);


CREATE OR REPLACE VIEW `moshi`.`course_t`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `columnName`, 
            `introduce`, 
            `introduceImage`, 
            `note`, 
            `createAt`, 
            `publishAt`, 
            `buyerCount`, 
            `courseType`, 
            `price`, 
            `discountedPrice`, 
            `offerTo`, 
            `status`
    from `moshi`.`course`
);


CREATE OR REPLACE VIEW `moshi`.`issue_t`
    AS
(
    SELECT
            `id`, 
            `courseId`, 
            `accountId`, 
            `title`, 
            `createAt`, 
            `status`
    from `moshi`.`issue`
);


CREATE OR REPLACE VIEW `moshi`.`issue_comment_t`
    AS
(
    SELECT
            `id`, 
            `issueId`, 
            `accountId`, 
            `createAt`, 
            `content`, 
            `status`
    from `moshi`.`issue_comment`
);


CREATE OR REPLACE VIEW `moshi`.`paragraph_t`
    AS
(
    SELECT
            `id`, 
            `sectionId`, 
            `accountId`, 
            `vedioId`, 
            `content`, 
            `createAt`, 
            `status`
    from `moshi`.`paragraph`
);


CREATE OR REPLACE VIEW `moshi`.`paragraph_comment_t`
    AS
(
    SELECT
            `id`, 
            `paragraphId`, 
            `accountId`, 
            `content`, 
            `createAt`, 
            `status`, 
            `replyTo`
    from `moshi`.`paragraph_comment`
);


CREATE OR REPLACE VIEW `moshi`.`permission_t`
    AS
(
    SELECT
            `id`, 
            `actionKey`, 
            `controller`, 
            `remark`
    from `moshi`.`permission`
);


CREATE OR REPLACE VIEW `moshi`.`preset_text_t`
    AS
(
    SELECT
            `key`, 
            `value`
    from `moshi`.`preset_text`
);


CREATE OR REPLACE VIEW `moshi`.`role_t`
    AS
(
    SELECT
            `id`, 
            `name`, 
            `createAt`
    from `moshi`.`role`
);


CREATE OR REPLACE VIEW `moshi`.`role_permission_t`
    AS
(
    SELECT
            `roleId`, 
            `permissionId`
    from `moshi`.`role_permission`
);


CREATE OR REPLACE VIEW `moshi`.`section_t`
    AS
(
    SELECT
            `id`, 
            `courseId`, 
            `accountId`, 
            `title`, 
            `createAt`, 
            `status`
    from `moshi`.`section`
);


CREATE OR REPLACE VIEW `moshi`.`session_t`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `expireAt`
    from `moshi`.`session`
);


CREATE OR REPLACE VIEW `moshi`.`video_t`
    AS
(
    SELECT
            `id`, 
            `resource`, 
            `recorder`, 
            `accountId`, 
            `status`
    from `moshi`.`video`
);




CREATE OR REPLACE VIEW `moshi`.`account_m`
    AS
(
    SELECT
            `id`, 
            `nickName`, 
            `password`, 
            `email`, 
            `phone`, 
            `avatar`, 
            `realName`, 
            `identityNumber`, 
            `age`, 
            `company`, 
            `position`, 
            `personalProfile`, 
            `sex`, 
            `birthday`, 
            `education`, 
            `profession`, 
            `createAt`, 
            `status`
    from `moshi`.`account`
);


CREATE OR REPLACE VIEW `moshi`.`account_role_m`
    AS
(
    SELECT
            `accountId`, 
            `roleId`
    from `moshi`.`account_role`
);


CREATE OR REPLACE VIEW `moshi`.`application_m`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `category`, 
            `title`, 
            `content`, 
            `createAt`, 
            `status`, 
            `reply`
    from `moshi`.`application`
);


CREATE OR REPLACE VIEW `moshi`.`article_m`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `title`, 
            `content`, 
            `createAt`, 
            `status`, 
            `audioId`
    from `moshi`.`article`
);


CREATE OR REPLACE VIEW `moshi`.`article_comment_m`
    AS
(
    SELECT
            `id`, 
            `articleId`, 
            `accountId`, 
            `content`, 
            `createAt`, 
            `status`, 
            `replyTo`
    from `moshi`.`article_comment`
);


CREATE OR REPLACE VIEW `moshi`.`audio_m`
    AS
(
    SELECT
            `id`, 
            `resource`, 
            `recorder`, 
            `accountId`, 
            `status`
    from `moshi`.`audio`
);


CREATE OR REPLACE VIEW `moshi`.`auth_code_m`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `expireAt`, 
            `type`
    from `moshi`.`auth_code`
);


CREATE OR REPLACE VIEW `moshi`.`course_m`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `columnName`, 
            `introduce`, 
            `introduceImage`, 
            `note`, 
            `createAt`, 
            `publishAt`, 
            `buyerCount`, 
            `courseType`, 
            `price`, 
            `discountedPrice`, 
            `offerTo`, 
            `status`
    from `moshi`.`course`
);


CREATE OR REPLACE VIEW `moshi`.`issue_m`
    AS
(
    SELECT
            `id`, 
            `courseId`, 
            `accountId`, 
            `title`, 
            `createAt`, 
            `status`
    from `moshi`.`issue`
);


CREATE OR REPLACE VIEW `moshi`.`issue_comment_m`
    AS
(
    SELECT
            `id`, 
            `issueId`, 
            `accountId`, 
            `createAt`, 
            `content`, 
            `status`
    from `moshi`.`issue_comment`
);


CREATE OR REPLACE VIEW `moshi`.`paragraph_m`
    AS
(
    SELECT
            `id`, 
            `sectionId`, 
            `accountId`, 
            `vedioId`, 
            `content`, 
            `createAt`, 
            `status`
    from `moshi`.`paragraph`
);


CREATE OR REPLACE VIEW `moshi`.`paragraph_comment_m`
    AS
(
    SELECT
            `id`, 
            `paragraphId`, 
            `accountId`, 
            `content`, 
            `createAt`, 
            `status`, 
            `replyTo`
    from `moshi`.`paragraph_comment`
);


CREATE OR REPLACE VIEW `moshi`.`permission_m`
    AS
(
    SELECT
            `id`, 
            `actionKey`, 
            `controller`, 
            `remark`
    from `moshi`.`permission`
);


CREATE OR REPLACE VIEW `moshi`.`preset_text_m`
    AS
(
    SELECT
            `key`, 
            `value`
    from `moshi`.`preset_text`
);


CREATE OR REPLACE VIEW `moshi`.`role_m`
    AS
(
    SELECT
            `id`, 
            `name`, 
            `createAt`
    from `moshi`.`role`
);


CREATE OR REPLACE VIEW `moshi`.`role_permission_m`
    AS
(
    SELECT
            `roleId`, 
            `permissionId`
    from `moshi`.`role_permission`
);


CREATE OR REPLACE VIEW `moshi`.`section_m`
    AS
(
    SELECT
            `id`, 
            `courseId`, 
            `accountId`, 
            `title`, 
            `createAt`, 
            `status`
    from `moshi`.`section`
);


CREATE OR REPLACE VIEW `moshi`.`session_m`
    AS
(
    SELECT
            `id`, 
            `accountId`, 
            `expireAt`
    from `moshi`.`session`
);


CREATE OR REPLACE VIEW `moshi`.`video_m`
    AS
(
    SELECT
            `id`, 
            `resource`, 
            `recorder`, 
            `accountId`, 
            `status`
    from `moshi`.`video`
);


