package com.moshi._admin.common;

import com.jfinal.config.Routes;
import com.moshi._admin.auth.AdminAuthInterceptor;
import com.moshi._admin.index.IndexController;

public class AdminRoutes extends Routes {

  @Override
  public void config() {
    addInterceptor(new AdminAuthInterceptor());

    add("/admin", IndexController.class);

  }
}
