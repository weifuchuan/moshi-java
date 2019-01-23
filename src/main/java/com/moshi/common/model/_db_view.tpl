/*
[
#for(meta : tableMetas)
    "#(meta.name)"#(for.last ? "" : ", ")
#end
]
#for(suffix : suffixs)
[
#for(meta : tableMetas)
    "#(meta.name)_#(suffix)"#(for.last ? "" : ", ")
#end
]
#end

*/

#for(suffix : suffixs)

#for(meta : tableMetas)

CREATE OR REPLACE VIEW `moshi`.`#(meta.name)_#(suffix)`
    AS
(
    SELECT
        #for(col : meta.columnMetas)
            #(for.last ? "`"+col.name+"`" : "`"+col.name+"`" + ", ")
        #end
    from `moshi`.`#(meta.name)`
);

#end

#end