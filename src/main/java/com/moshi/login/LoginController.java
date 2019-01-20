package com.moshi.login;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.moshi.common.model.Account;

import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/login")
public class LoginController extends JbootController {
  private LoginService srv = LoginService.me;

  @Before(LoginValidator.class)
  public void index() {
    String email = getPara("email");
    String password = getPara("password");
    Ret ret = srv.loginByEmail(email, password);
    if (ret.isOk()) {
      String sessionId = ret.getStr(LoginService.sessionIdName);
      int maxAgeInSeconds = ret.getInt("maxAgeInSeconds");
      setCookie(LoginService.sessionIdName, sessionId, maxAgeInSeconds, true);
      setAttr(LoginService.loginAccountCacheName, ret.get(LoginService.loginAccountCacheName));
    }
    renderJson(ret);
  }

  public void probe() {
    Account account = getAttr(srv.loginAccountCacheName);
    if (account != null) {
      renderJson(Ret.ok("account", account));
    } else {
      renderJson(Ret.fail());
    }
  }
}
