package com.moshi.index;


import com.jfinal.core.Controller;

public class IndexController extends Controller {
  public void index() {
    render("learner.html");    
  }
  
  public void teacher() {
    render("teacher.html");    
  }


}
