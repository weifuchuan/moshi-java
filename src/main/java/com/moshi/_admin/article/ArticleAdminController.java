package com.moshi._admin.article;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;

public class ArticleAdminController extends BaseController {
  @Inject
  ArticleAdminService srv;

  public void updateStatus(int id, int status){
    Ret ret = srv.updateStatus(id, status);
    renderJson(ret);
  }
}
