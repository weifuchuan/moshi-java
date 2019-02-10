package com.moshi.login;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
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
      Account account = (Account) ret.get(LoginService.loginAccountCacheName);
      setAttr(LoginService.loginAccountCacheName, account);
      ret.set("account", account.copyModel());
      ret.delete(LoginService.sessionIdName);
    }
    renderJson(ret);
  }

  public void probe() {
    Account account = getAttr(LoginService.loginAccountCacheName);
    if (account != null) {
      renderJson(Ret.ok("account", account.copyModel()));
    } else {
      renderJson(Ret.fail());
    }
  }

  /** 退出登录 */
  @Clear
  @ActionKey("/logout")
  public void logout() {
    srv.logout(getCookie(LoginService.sessionIdName));
    removeCookie(LoginService.sessionIdName);
    renderText("");
  }

  public void captcha() {
    renderCaptcha();
  }
}
