package com.moshi.common.model

import cn.hutool.core.io.file.FileWriter
import com.jfinal.kit.*
import com.jfinal.kit.PropKit.*
import com.jfinal.plugin.activerecord.dialect.MysqlDialect
import com.jfinal.plugin.activerecord.generator.*
import com.jfinal.plugin.druid.DruidPlugin
import com.jfinal.template.Engine
import com.moshi.common.MainConfig
import org.springframework.core.io.ByteArrayResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator

import java.nio.charset.StandardCharsets

object _Generator {
  fun getDruidPlugin(p: Prop): DruidPlugin {
    return DruidPlugin(
      p.get("db.datasource.url"),
      p.get("db.datasource.user"),
      p.get("db.datasource.password").trim { it <= ' ' })
  }

  @JvmStatic
  fun main(args: Array<String>) {
    val prop = MainConfig.loadConfig()
    val druidPlugin = getDruidPlugin(prop)
    druidPlugin.start()
    val dataSource = druidPlugin.dataSource

    // base model 所使用的包名
    val baseModelPackageName = "com.moshi.common.model.base"
    // base model 文件保存路径
    val baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/moshi/common/model/base"

    println("输出路径：$baseModelOutputDir")

    // model 所使用的包名 (MappingKit 默认使用的包名)
    val modelPackageName = "com.moshi.common.model"
    // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
    val modelOutputDir = "$baseModelOutputDir/.."
    // 创建生成器
    val gen = Generator(dataSource!!, baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir)
    // 设置数据库方言
    gen.setDialect(MysqlDialect())
    // 设置是否生成字段备注
    gen.setGenerateRemarks(true)
    gen.setGenerateDataDictionary(true)
    gen.generate()

    val dictGen = DataDictionaryGenerator(dataSource, modelOutputDir)



    dictGen.dataDictionaryFileName = "数据字典.txt"
    val tableMetas = MetaBuilder(dataSource).build()
    dictGen.generate(tableMetas)

    val dbView = Engine.create("db_view")
      .getTemplate(
        PathKit.getWebRootPath() + "/src/main/java/com/moshi/common/model/_db_view.tpl"
      )
    val dbViewRaw = dbView.renderToString(
      Kv.by("tableMetas", tableMetas).set("suffixs", arrayOf("l", "t", "m"))
    )
    FileWriter(PathKit.getWebRootPath() + "/src/main/java/com/moshi/common/model/_db_view.sql")
      .write(dbViewRaw)

    val engine = Engine.create("form")

    engine.addSharedMethod(StrKit())

    val form = engine.getTemplate(
      PathKit.getWebRootPath() + "/src/main/java/com/moshi/common/model/_form.tpl"
    )
    for (meta in tableMetas) {
      val s = form.renderToString(
        Kv.by("name", StrKit.firstCharToUpperCase(StrKit.toCamelCase(meta.name)))
          .set("columns", meta.columnMetas)
      )
      FileWriter(
        PathKit.getWebRootPath()
            + "/src/main/java/com/moshi/common/model/_forms/"
            + StrKit.firstCharToUpperCase(StrKit.toCamelCase(meta.name))
            + "Form.tsx"
      )
        .write(s)
    }

    val mkgen = MappingKitGenerator(modelPackageName, modelOutputDir)
    mkgen.generate(tableMetas)

    // NOTE: ONLY IN DEV
    val populator = ResourceDatabasePopulator()
    populator.setSqlScriptEncoding("UTF-8")
    populator.addScript(ByteArrayResource(dbViewRaw.toByteArray(StandardCharsets.UTF_8)))
    populator.execute(dataSource)

    val configView = Engine.create("config_json")
      .getTemplate(
        PathKit.getWebRootPath() + "/src/main/java/com/moshi/common/model/_config_json.tpl"
      )
    val configRaw = configView.renderToString(
      Kv.by("tableMetas", tableMetas).set("suffixs", arrayOf("l", "t", "m"))
    )
    FileWriter(PathKit.getWebRootPath() + "/src/main/resources/sqlService/config.json")
      .write(configRaw)
  }
}