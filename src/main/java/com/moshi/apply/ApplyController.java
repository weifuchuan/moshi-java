package com.moshi.apply;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Application;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/apply")
public class ApplyController extends BaseController {
  @Inject private ApplyService srv;

  public void my() {
    Application application = srv.findByAccountId(getLoginAccountId());
    renderJson(application == null ? Ret.fail() : Ret.ok("application", application));
  }

  public void commit() {
    int id = getParaToInt("id");
    String title = getPara("title");
    String content = getPara("content");
    Ret ret = srv.commit(getLoginAccountId(), id, title, content);
    renderJson(ret);
  }

  public void cancel() {
    Ret ret = srv.cancel(getLoginAccountId(), getParaToInt("id"));
    renderJson(ret);
  }
}
