package com.moshi.common.model;

import cn.hutool.core.io.file.FileWriter;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.DataDictionaryGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.codegen.model.JbootBaseModelGenerator;
import io.jboot.codegen.model.JbootModelGenerator;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class _Generator {
  public static void main(String[] args) {
    // base model 所使用的包名
    String baseModelPackageName = "com.moshi.common.model.base";
    // base model 文件保存路径
    String baseModelOutputDir =
        PathKit.getWebRootPath() + "/src/main/java/com/moshi/common/model/base";

    System.out.println("输出路径：" + baseModelOutputDir);

    // model 所使用的包名 (MappingKit 默认使用的包名)
    String modelPackageName = "com.moshi.common.model";
    // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
    String modelOutputDir = baseModelOutputDir + "/..";

    // 创建生成器
    JbootModelGenerator gen =
        new JbootModelGenerator(modelPackageName, baseModelPackageName, modelOutputDir);
    JbootBaseModelGenerator baseGen =
        new JbootBaseModelGenerator(baseModelPackageName, baseModelOutputDir);
    DataDictionaryGenerator dictGen =
        new DataDictionaryGenerator(CodeGenHelpler.getDatasource(), modelOutputDir);

    gen.setGenerateDaoInModel(true);

    gen.generate();

    baseGen.generate();

    dictGen.setDataDictionaryFileName("数据字典.txt");
    List<TableMeta> tableMetas = CodeGenHelpler.createMetaBuilder().build();
    dictGen.generate(tableMetas);

    Template dbView =
        Engine.create("db_view")
            .getTemplate(
                PathKit.getWebRootPath() + "/src/main/java/com/moshi/common/model/_db_view.tpl");
    String dbViewRaw =
        dbView.renderToString(
            Kv.by("tableMetas", tableMetas).set("suffixs", new String[] {"l", "t", "m"}));
    new FileWriter(PathKit.getWebRootPath() + "/src/main/java/com/moshi/common/model/_db_view.sql")
        .write(dbViewRaw);

    // NOTE: ONLY IN DEV
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.setSqlScriptEncoding("UTF-8");
    populator.addScript(new ByteArrayResource(dbViewRaw.getBytes(StandardCharsets.UTF_8)));
    populator.execute(CodeGenHelpler.getDatasource());

    Template configView =
        Engine.create("config_json")
            .getTemplate(
                PathKit.getWebRootPath()
                    + "/src/main/java/com/moshi/common/model/_config_json.tpl");
    String configRaw =
        configView.renderToString(
            Kv.by("tableMetas", tableMetas).set("suffixs", new String[] {"l", "t", "m"}));
    new FileWriter(PathKit.getWebRootPath() + "/src/main/resources/sqlService/config.json")
        .write(configRaw);
  }
}
