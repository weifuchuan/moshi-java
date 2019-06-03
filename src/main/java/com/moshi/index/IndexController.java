package com.moshi.index;


import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.moshi.common.interceptor.IsManagerInterceptor;

public class IndexController extends Controller {
  public void index() {
    render("learner.html");    
  }
  
  public void teacher() {
    render("teacher.html");    
  }

  @Before(IsManagerInterceptor.class)
  public void admin(){
    render("admin.html");
  }
}
