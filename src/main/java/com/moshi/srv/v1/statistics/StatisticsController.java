package com.moshi.srv.v1.statistics;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;

public class StatisticsController extends BaseController {
  @Inject private StatisticsService srv;

  public void visit(int id, String type) {
    Ret ret = srv.visit(getLoginAccountId(), id, type);
    renderJson(ret);
  }
}
