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
where c.accountId = a.id and (c.status & (1 << 2)) != 0 and c.courseType = #para(0)
#end