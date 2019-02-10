#sql("find")
  SELECT i.*, a.nickName, a.avatar
  FROM `issue` i, `course` c, `account` a, (
    select count(*) commentCount, ic.issueId
    from `issue_comment` ic
    group by ic.issueId
  ) ic
  WHERE ((c.id = #para(0) AND i.`status` = 0 AND i.courseId = c.id AND i.accountId = a.id)
    OR (c.id = #para(0) AND i.`status` = 1 AND i.courseId = c.id AND i.closerId = a.id ))
    and ic.issueId = i.id
#end