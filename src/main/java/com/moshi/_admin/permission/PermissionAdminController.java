package com.moshi._admin.permission;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Permission;

/** 权限管理 */
public class PermissionAdminController extends BaseController {

  @Inject PermissionAdminService srv;

  public void sync() {
    Ret ret = srv.sync();
    renderJson(ret);
  }

  public void update() {
    Ret ret = srv.update(getModel(Permission.class, ""));
    renderJson(ret);
  }

  public void delete() {
    Ret ret = srv.delete(getParaToInt("id"));
    renderJson(ret);
  }
}
