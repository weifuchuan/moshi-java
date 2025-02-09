package com.moshi.common.controller;

import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import com.moshi.common.kit.FstKit;
import com.moshi.common.model.Account;
import com.moshi.login.LoginService;

import java.util.ConcurrentModificationException;

/**
 * 基础控制器，方便获取登录信息
 *
 * <p>警告：由于 BaseController 中声明了 Account loginAccount 属性， 所以不能使用 FastControllerFactory 这类回收
 * Controller 的用法 在 jfinal 3.5 发布以后，可以通过继承 _clear_() 方法来清除属性中的值 才能使用 FastControllerFactory 用户自己的
 * Controller 也是同样的道理
 *
 * <p>注意： 需要 LoginSessionInterceptor 配合，该拦截器使用 setAttr("loginAccount", ...) 事先注入了登录账户
 * 否则即便已经登录，该控制器也会认为没有登录
 */
public class BaseController extends Controller {

  /**
   * 警告：由于这个属性的存在，不能直接使用 FastControllerFactory，除非使用 jfinal 3.5 并覆盖父类中的 _clear_()
   * 方法清除本类中与父类中的属性值，详情见本类中的 protected void _clear_() 方法
   *
   * <p>原因是 FastControllerFactory 是回收使用 controller 对象的，所以要在 _clear()_ 中清除上次使用时的属性值
   */
  private Account loginAccount = null;

  protected void _clear_() {
    this.loginAccount = null;
    super._clear_();
  }

  @NotAction
  public Account getLoginAccount() {
    if (loginAccount == null) {
      loginAccount = getAttr(LoginService.loginAccountCacheName);
      if (loginAccount != null && loginAccount.isStatusLock()) {
        throw new IllegalStateException("当前用户状态不允许登录，status = " + loginAccount.getStatus());
      }
    }
    return loginAccount;
  }

  @NotAction
  public boolean isLogin() {
    return getLoginAccount() != null;
  }

  @NotAction
  public boolean notLogin() {
    return !isLogin();
  }

  /** 获取登录账户id notLogin -> null */
  @NotAction
  public Integer getLoginAccountId() {
    if (notLogin()) return null;
    return getLoginAccount().getId();
  }

  @NotAction
  public boolean isPjaxRequest() {
    return "true".equalsIgnoreCase(getHeader("X-PJAX"));
  }

  @NotAction
  public boolean isAjaxRequest() {
    return "XMLHttpRequest".equalsIgnoreCase(getHeader("X-Requested-With"));
  }

  public void renderJsonThreadSafe(Object ret) {
    try {
      renderJson(ret);
    } catch (ConcurrentModificationException ex) {
      renderJson(FstKit.deserialize(FstKit.serialize(ret)));
    }
  }
}
