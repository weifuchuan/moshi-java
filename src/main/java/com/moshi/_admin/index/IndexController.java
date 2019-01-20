package com.moshi._admin.index;

import io.jboot.web.controller.JbootController;

public class IndexController extends JbootController {
  public void index(){
    render("admin.html");
  }
}
