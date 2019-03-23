package com.moshi.srv.v1.statistics;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;

import java.util.concurrent.ExecutionException;

public class StatisticsController extends BaseController {
  @Inject private StatisticsService srv;

  public void visit(int id, String type) throws ExecutionException, InterruptedException {
    Ret ret = srv.visit(getLoginAccountId(), id, type);
    renderJson(ret);
  }
}
