package com.moshi.select;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moshi.common.controller.BaseController;


import java.util.List;

/** 不使用GraphQL，使用SQL搭建类似服务 此服务使用SQL语法解析器过滤掉所有非select语句 */

public class SqlServiceController extends BaseController {

  @Before({POST.class, SqlSrvLearnerInterceptor.class})
  public void index() {
    SqlServiceParams p = getAttr("params");
    List<Record> ret = Db.find(p.getSql(), p.getArgs());
    renderJson(ret);
  }

  @Before({POST.class, SqlSrvTeacherInterceptor.class})
  public void teacher() {
    SqlServiceParams p = getAttr("params");
    List<Record> ret = Db.find(p.getSql(), p.getArgs());
    renderJson(ret);
  }

  @Before({POST.class, SqlSrvManagerInterceptor.class})
  public void manager() {
    SqlServiceParams p = getAttr("params");
    List<Record> ret = Db.find(p.getSql(), p.getArgs());
    renderJson(ret);
  }
}
