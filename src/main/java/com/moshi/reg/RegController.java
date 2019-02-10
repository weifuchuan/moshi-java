package com.moshi.reg;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;

import com.moshi.common.model.Account;
import com.moshi.login.LoginService;
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
    if (ret.isOk()) {
      String sessionId = ret.getStr(LoginService.sessionIdName);
      int maxAgeInSeconds = ret.getInt("maxAgeInSeconds");
      setCookie(LoginService.sessionIdName, sessionId, maxAgeInSeconds, true);
      Account account = (Account) ret.get(LoginService.loginAccountCacheName);
      setAttr(LoginService.loginAccountCacheName, account);
      ret.set(LoginService.loginAccountCacheName, account.copyModel());
    }
    renderJson(ret);
  }

  public void activate(String authcode) {
    Ret ret = srv.activate(authcode);
    if (ret.isOk()) {
      Account account = getAttr(LoginService.loginAccountCacheName);
      LoginService.me.reloadLoginAccount(account);
    }
    renderJson(ret);
  }

  /** 重发激活邮件 */
  public void reSendActivateEmail() {
    Account account = getAttr(LoginService.loginAccountCacheName);
    Ret ret = srv.reSendActivateEmail(account.getEmail());
    renderJson(ret);
  }

  public void captcha() {
    renderCaptcha();
  }
}
