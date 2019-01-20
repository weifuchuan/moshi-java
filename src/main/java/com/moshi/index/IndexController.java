package com.moshi.index;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/")
public class IndexController extends JbootController {
  public void index() {
    render("learner.html");    
  }
  
  public void teacher() {
    render("teacher.html");    
  }

}
