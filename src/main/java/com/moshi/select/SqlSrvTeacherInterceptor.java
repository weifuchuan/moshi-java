package com.moshi.select;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.druid.DruidPlugin;
import com.moshi.common.MainConfig;
import com.moshi.common.model.Account;
import com.moshi.login.LoginService;
import com.moshi.select.wall.WallConfig;
import com.moshi.select.wall.WallFilter;


import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;

public class SqlSrvTeacherInterceptor implements Interceptor {
  private WallFilter filter;

  public SqlSrvTeacherInterceptor() {
    filter = new WallFilter();
    filter.setDbType(MainConfig.Companion.getP().get("db.datasource.type"));
    filter.setLogViolation(true);
    filter.setThrowException(true);
    filter.setConfig(buildConfig());
    DruidPlugin druidPlugin =
        new DruidPlugin(
            MainConfig.Companion.getP().get("db.datasource.url"),
            MainConfig.Companion.getP().get("db.datasource.user"),
            MainConfig.Companion.getP().get("db.datasource.password"));
    druidPlugin.start();

    filter.init((DruidDataSource) druidPlugin.getDataSource());
  }

  @Override
  public void intercept(Invocation inv) {
    Controller controller = inv.getController();

    Account account = controller.getAttr(LoginService.loginAccountCacheName);
    if (account != null && account.isTeacher()) {
      String raw = controller.getRawData();
      SqlServiceParams params = JSON.parseObject(raw, SqlServiceParams.class);

      try {
        params.setSql(filter.check(params.getSql()));
        controller.setAttr("params", params);
        inv.invoke();
      } catch (Exception e) {
        e.printStackTrace();
        controller.renderJson(Ret.fail("msg", "wishful thinking"));
      }
    } else {
      controller.renderError(404);
    }
  }

  private WallConfig buildConfig() {
    WallConfig config = new WallConfig();

    String json = new FileReader(MainConfig.Companion.getP().get("sqlService.configFile")).readString();
    JSONObject jsonObject = JSON.parseObject(json).getJSONObject("teacher");
    String[] keys =
        new String[] {
          "denyFunctions",
          "denyTables",
          "denySchemas",
          "denyVariants",
          "denyObjects",
          "permitFunctions",
          "permitTables",
          "permitSchemas",
          "permitVariants",
          "readOnlyTables"
        };
    for (String key : keys) {
      JSONArray jsonArray = jsonObject.getJSONArray(key);
      String[] array = jsonArray.toArray(new String[0]);
      try {
        Set<String> set = (Set<String>) ReflectUtil.getFieldValue(config, key);
        set.addAll(Arrays.asList(array));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    WallConfig config2 =
        BeanUtil.fillBean(
            config,
            new ValueProvider<String>() {
              @Override
              public Object value(String key, Type valueType) {
                if (valueType.getTypeName().toLowerCase().startsWith("bool")) {
                  return false;
                }
                return BeanUtil.getFieldValue(config, key);
              }

              @Override
              public boolean containsKey(String key) {
                return true;
              }
            },
            CopyOptions.create());

    config2.setSelelctAllow(true);
    config2.setSelectAllColumnAllow(true);
    config2.setSelectWhereAlwayTrueCheck(true);
    config2.setSelectHavingAlwayTrueCheck(true);
    config2.setSelectUnionCheck(true);
    config2.setSelectMinusCheck(true);
    config2.setSelectExceptCheck(true);
    config2.setMinusAllow(true);
    config2.setIntersectAllow(true);
    config2.setTableCheck(true);
    config2.setSchemaCheck(true);
    config2.setFunctionCheck(true);
    config2.setObjectCheck(true);
    config2.setVariantCheck(true);
    config2.setStrictSyntaxCheck(true);
    config2.setConditionOpBitwseAllow(true);
    config2.setConstArithmeticAllow(true);
    config2.setMustParameterized(true);
//    config2.setSelectLimit(Integer.parseInt(MainConfig.Companion.getP().get("sqlService.selectLimit").trim()));

    return config2;
  }

  // for test
  public static void main(String[] args) throws SQLException {
    SqlSrvTeacherInterceptor interceptor = new SqlSrvTeacherInterceptor();
    interceptor.filter.check("select email from `v_account` where id=1");
    //    SqlServiceParams params =
    //        JSON.parseObject("{\"sql\":\"sele\",\"args\":[999,1.2,\"aaa\"]}",
    // SqlServiceParams.class);
    //    for(Object o:params.getArgs()){
    //      System.out.printf("type = %s, value = %s\n", o.getClass().getName(),o);
    //    }
  }
}
