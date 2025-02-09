#sql("find")
select a.*
from `course` c,
     `article` a
where c.id = #para(0) and c.accountId = #para(1) and c.id = a.courseId
#end

#sql("findComments")
select c.*, u.nickName, u.avatar, u.status accountStatus
from `article_comment` c,
     `account` u
where c.articleId = #para(0) and c.accountId = u.id
#end

#sql("update")
update article
set
  #for(item : items)
  #if(for.last)
  #(item.key) = #para(item.value)
  #else
  #(item.key) = #para(item.value),
  #end
  #end
  where id = #(id)
#end

#sql("deleteComment")
DELETE ac
FROM `article_comment` ac,
     `article` a,
     `course` c
WHERE ac.id = #para(commentId) AND
      ac.articleId = #para(articleId) AND
      ac.articleId = a.id
  AND a.courseId = c.id
  AND (c.accountId = #para(accountId) OR ac.accountId = #para(accountId))
         #end

         #sql("updateCommentStatus")
         UPDATE article_comment ac, article a, course c
  SET ac.status = #para(status)
  WHERE
    ac.id = #para(commentId)
    AND ac.`accountId` = #para(accountId)
    AND ac.`articleId` = #para(articleId)
    AND ac.`articleId` = a.`id`
    AND a.`courseId` = c.id
#end

#--
;
--#

#sql("trialReading")
select a.id, a.courseId, a.title, a.summary, a.publishAt, a.audioId
from article a
where a.courseId = #para(0)
  and (a.status & (1 << 2)) != 0
#end

#sql("list")
select a.id, a.courseId, a.title, a.summary, a.publishAt, a.audioId
from article a
where a.courseId = #para(courseId)
  and (a.status & (1 << 1)) != 0 ### 发布
  and (a.status & (1 << 0)) = 0 ### 未锁定
order by a.publishAt #(cending)
#end