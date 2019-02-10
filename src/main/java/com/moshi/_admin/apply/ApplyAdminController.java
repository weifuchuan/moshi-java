package com.moshi._admin.apply;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;

public class ApplyAdminController extends BaseController {
  @Inject
  private  ApplyAdminService srv;

  public void pass(int id, String reply){
    Ret ret = srv.pass(id, reply);
    renderJson(ret);
  }

  public void reject(int id, String reply){
    Ret ret = srv.reject(id, reply);
    renderJson(ret);
  }


}
