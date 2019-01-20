package com.moshi.reg;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/reg")
public class RegController extends JbootController {

  private RegService srv = RegService.me;

  @Before(RegValidator.class)
  public void index() {
    String email = getPara("email");
    // 暂时只支持邮箱注册登录
    // String phone = getPara("phone");
    String nickName = getPara("nickName");
    String password = getPara("password");

    Ret ret = srv.regByEmail(email, password, nickName);
    renderJson(ret);
    
  }
  
  public void activate() {
    String authcode = getPara("authcode");
    Ret ret = srv.activate(authcode);
    renderJson(ret);
  }

  public void captcha() {
    renderCaptcha();
  }
}
