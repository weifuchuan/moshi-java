package com.moshi.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.moshi.common.model.Account;
import com.moshi.login.LoginService;

public class IsTeacherOrManagerInterceptor implements Interceptor {
  @Override
  public void intercept(Invocation inv) {
    Controller controller = inv.getController();
    Account account = controller.getAttr(LoginService.loginAccountCacheName);
    if (account != null && (account.isTeacher() || account.isManager())) {
      inv.invoke();
    } else {
      controller.renderError(404);
    }
  }
}
