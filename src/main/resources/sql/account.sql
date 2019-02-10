#sql("update")
  update `account`
  set #for(item : items)
        #(item.key) = #para(item.value) #(for.last ? "" : ",")
      #end
  where id = #para(id)
#end