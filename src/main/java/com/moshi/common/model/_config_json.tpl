### for dev, all views has allowed
{
  "learner": {
    "denyFunctions": [],
    "denyTables": [
                  #for(meta : tableMetas)
                      "#(meta.name)"#(for.last ? "" : ", ")
                  #end
                  ],
    "denySchemas": [],
    "denyVariants": [],
    "denyObjects": [],
    "permitFunctions": [],
    "permitTables": [
                    #for(meta : tableMetas)
                        "#(meta.name)_l"#(for.last ? "" : ", ")
                    #end
                    ],
    "permitSchemas": [
      "moshi"
    ],
    "permitVariants": [],
    "readOnlyTables": []
  },
  "teacher": {
    "denyFunctions": [],
    "denyTables": [
                  #for(meta : tableMetas)
                      "#(meta.name)"#(for.last ? "" : ", ")
                  #end
                  ],
    "denySchemas": [],
    "denyVariants": [],
    "denyObjects": [],
    "permitFunctions": [],
    "permitTables": [
                    #for(meta : tableMetas)
                        "#(meta.name)_t"#(for.last ? "" : ", ")
                    #end
                    ],
    "permitSchemas": [
      "moshi"
    ],
    "permitVariants": [],
    "readOnlyTables": []
  },
  "manager": {
    "denyFunctions": [],
    "denyTables": [
                  #for(meta : tableMetas)
                      "#(meta.name)"#(for.last ? "" : ", ")
                  #end
                  ],
    "denySchemas": [],
    "denyVariants": [],
    "denyObjects": [],
    "permitFunctions": [],
    "permitTables": [
                    #for(meta : tableMetas)
                        "#(meta.name)_m"#(for.last ? "" : ", ")
                    #end
                    ],
    "permitSchemas": [
      "moshi"
    ],
    "permitVariants": [],
    "readOnlyTables": []
  }
}