package com.moshi.common.model2.generator

import com.jfinal.kit.PathKit
import com.jfinal.plugin.activerecord.dialect.MysqlDialect
import com.jfinal.plugin.druid.DruidPlugin

fun main() {
  val druidPlugin = getDruidPlugin()
  druidPlugin.start()
  val dataSource = druidPlugin.dataSource
  //
  // // base model 所使用的包名
  // val baseModelPackageName = "com.moshi.common.model2.base"
  // // base model 文件保存路径
  // val baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/moshi/common/model2/base"
  //
  // println("输出路径：$baseModelOutputDir")
  //
  // // model 所使用的包名 (MappingKit 默认使用的包名)
  // val modelPackageName = "com.moshi.common.model2"
  // // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
  // val modelOutputDir = "$baseModelOutputDir/.."
  // // 创建生成器
  // val gen = Generator(dataSource!!, baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir)
  // // 设置数据库方言
  // gen.setDialect(MysqlDialect())
  // // 设置是否生成字段备注
  // gen.setGenerateRemarks(true)
  // gen.setGenerateDataDictionary(true)
  // gen.generate()

  val metaBuilder = MetaBuilder(dataSource)
  metaBuilder.setDialect(MysqlDialect())
  metaBuilder.setGenerateRemarks(true)

  val list = metaBuilder.build()

  val dataDictionaryGenerator = DataDictionaryGenerator(dataSource, PathKit.getWebRootPath() + "/src/main/java/com/moshi/common/model2")
  dataDictionaryGenerator.rebuildColumnMetas(list)
  dataDictionaryGenerator.setDataDictionaryFileName("DB-def.html")

  val sb = StringBuilder("""
    <!DOCTYPE HTML>
<html lang="zh" >
    <head>
        <meta charset="UTF-8">
        <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
        <style>
          .dbt{
              font-size:14px;
              page-break-before: auto;
              page-break-after: always;
          }
        </style>
    </head>
  """.trimIndent())

  list.forEach {
    sb.append("<div class='dbt'>")

    sb.append("<h4>${it.name}</h4>")

    sb.append(
      """
      <table  class="table">
        <thead>
          <tr>
            <th>属性名称</th>
            <th>数据类型</th>
            <th>属性描述</th>
            <th>是否主键</th>
            <th>是否外键</th>
          </tr>
        </thead>
        <tbody>
    """.trimIndent()
    )

    it.columnMetas.sortWith(Comparator<ColumnMeta> { a, b ->
      if (a.isPrimaryKey == "PRI") {
        -1
      } else if (b.isPrimaryKey == "PRI") {
        1
      } else if (a.isForeignKey) {
        1
      } else {
        -1
      }
    })
    it.columnMetas.forEach {
      sb.append(
        """
        <tr>
          <td>${it.name}</td>
          <td>${it.type}</td>
          <td>${it.remarks}</td>
          <td>${if (it.isPrimaryKey2)"是" else ""}</td>
          <td>${if(it.isForeignKey)"是" else ""}</td>
        </tr>
      """.trimIndent()
      )
    }

    sb.append("</tbody></table>")

    sb.append("</div>")
  }

  sb.append("</html>")

  dataDictionaryGenerator.writeToFile(sb.toString())
}

fun getDruidPlugin(): DruidPlugin {
  return DruidPlugin(
    "jdbc:mysql://123.207.28.107:3306/moshi2?characterEncoding=utf8&useSSL=false&zeroDateTimeBehavior=convertToNull",
    "root",
    "url977782"
  )
}