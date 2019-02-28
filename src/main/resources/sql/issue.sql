#sql("find")
SELECT i.*, a.nickName, a.avatar, ic.commentCount
FROM `issue` i,
     `course` c,
     `account` a,
     (
       select count(*) commentCount, ic.issueId
       from `issue_comment` ic
       group by ic.issueId
     ) ic
WHERE ((c.id = #para(0) AND i.`status` = 0 AND i.courseId = c.id AND i.accountId = a.id)
  OR (c.id = #para(0) AND i.`status` = 1 AND i.courseId = c.id AND i.closerId = a.id ))
    and ic.issueId = i.id order by i.openAt desc
#end

#--
;
--#

#sql("findById")
SELECT i.*, a.nickName, a.avatar
FROM `issue` i,
     `account` a
WHERE i.id = #para(0) and i.accountId = a.id
#end

#sql("list")

SELECT i.*, a.nickName, a.avatar, ic.commentCount
FROM `issue` i,
     `course` c,
     `account` a,
     (
       select count(*) commentCount, ic.issueId
       from `issue_comment` ic
       group by ic.issueId
     ) ic
WHERE
  #if(filter=="open")
  i.`status` = 0
  AND
  #elseif(filter=="close")
  i.`status` = 1
  AND
  #else
  #end
  c.id = #para(courseId)
  AND i.courseId = c.id
  AND i.accountId = a.id
  and ic.issueId = i.id
order by i.openAt desc

#end

#sql("findComments")
select ic.*, a.avatar, a.nickName
from issue_comment ic,
     account a
where ic.issueId = #para(0)
  and ic.accountId = a.id
order by ic.createAt
#end