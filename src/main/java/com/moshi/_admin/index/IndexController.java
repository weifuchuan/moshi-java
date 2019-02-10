package com.moshi._admin.index;

import com.jfinal.aop.Before;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.IsManagerInterceptor;
import com.moshi.common.interceptor.UnlockInterceptor;

public class IndexController extends BaseController {
  public void index(){
    render("admin.html");
  }
}
