#sql("update")
update course
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

#sql("all")
select c.*, a.nickName, a.avatar, a.realPicture
from course c,
     account a
where c.accountId = a.id
#end

#sql("allSimpleByType")
select c.id,
       c.accountId,
       c.name,
       c.shortIntro,
       c.introduceImage,
       c.publishAt,
       c.buyerCount,
       c.courseType,
       c.price,
       c.discountedPrice,
       c.offerTo,
       c.status,
       c.lectureCount,
       a.nickName,
       a.avatar,
       a.realPicture
from course c,
     account a
where c.accountId = a.id
  and (c.status & (1 << 2)) != 0
  and c.courseType = #para(0)
#end

#sql("intro")
select c.*, a.nickName, a.avatar, a.realPicture, a.personalProfile
from course c, account a
where c.id = #para(0) and c.accountId = a.id
#end

#sql("subscribedCourses")
select  c.id,
        c.accountId,
        c.name,
        c.shortIntro,
        c.introduceImage,
        c.publishAt,
        c.buyerCount,
        c.courseType,
        c.price,
        c.discountedPrice,
        c.offerTo,
        c.status,
        c.lectureCount,
        a.nickName,
        a.avatar,
        a.realPicture,
        cc.count publishedCount
from course c, subscription s, account a, course_published_count cc
where s.accountId = #para(0)
  and s.subscribeType = 'course'
  and s.status = 1
  and s.refId = c.id
  and cc.id = c.id
  AND a.id = c.`accountId`
#end

#sql("allIdWithCourses")
select a.id, c.id courseId
from course c, article a
where
  #for(id : courseIdList)
    #(for.first ? "" : "or")
      ( c.courseType = 1
        and c.id = #para(id)
        and a.courseId = c.id )
  #end
union
select p.id, c.id courseId
from course c, paragraph p, section s
where
  #for(id : courseIdList)
    #(for.first ? "" : "or")
      ( c.courseType = 2
        and c.id = #para(id)
        and s.courseId = c.id
        and s.id = p.sectionId )
  #end
#end

