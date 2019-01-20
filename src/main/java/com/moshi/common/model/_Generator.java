package com.moshi.common.model;

import com.jfinal.kit.PathKit;
import io.jboot.codegen.model.JbootBaseModelGenerator;
import io.jboot.codegen.model.JbootModelGenerator;

public class _Generator {
  public static void main(String[] args) {
    // base model 所使用的包名
    String baseModelPackageName = "com.moshi.common.model.base";
    // base model 文件保存路径
    String baseModelOutputDir = PathKit.getWebRootPath()
        + "/src/main/java/com/moshi/common/model/base";

    System.out.println("输出路径：" + baseModelOutputDir);

    // model 所使用的包名 (MappingKit 默认使用的包名)
    String modelPackageName = "com.moshi.common.model";
    // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
    String modelOutputDir = baseModelOutputDir + "/..";

    // 创建生成器
    JbootModelGenerator gen = new JbootModelGenerator(modelPackageName, baseModelPackageName,
        modelOutputDir);
    JbootBaseModelGenerator baseGen = new JbootBaseModelGenerator(baseModelPackageName,
        baseModelOutputDir);
    // 设置是否在 Model 中生成 dao 对象
    gen.setGenerateDaoInModel(true);
    gen.generate();
    baseGen.generate();
  }
}
