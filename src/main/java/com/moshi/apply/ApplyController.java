package com.moshi.apply;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Application;


import java.util.List;

public class ApplyController extends BaseController {
  @Inject private ApplyService srv;

  public void my() {
    int category = getParaToInt("category");
    List<Application> applications = srv.findMy(getLoginAccountId(), category);
    renderJson(applications);
  }

  public void commit() {
    int id = getParaToInt("id");
    String title = getPara("title");
    String content = getPara("content");
    int category = getParaToInt("category");
    Ret ret = srv.commit(getLoginAccountId(), id, title, content, category, getLoginAccountId());
    renderJson(ret);
  }

  public void cancel() {
    Ret ret = srv.cancel(getLoginAccountId(), getParaToInt("id"));
    renderJson(ret);
  }
}
