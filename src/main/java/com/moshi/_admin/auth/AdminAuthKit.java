package com.moshi._admin.auth;

import com.jfinal.aop.Aop;
import com.moshi.common.model.Account;

/**
 * 权限管理的 shared method 扩展
 * <p>
 * 作为 #role、#permission 指令的补充，支持 #else 块
 * <p>
 * <p>
 * 使用示例：
 * #if (hasRole("权限管理员", "CEO", "CTO"))
 * ...
 * #else
 * ...
 * #end
 * <p>
 * #if (hasPermission("/admin/project/edit"))
 * ...
 * #else
 * ...
 * #end
 */
public class AdminAuthKit {

  /**
   * 注意这里与控制器和拦截器不同，不能使用 @Inject 注入
   * 但可以使用 Aop.get(...) 实现同样的功能，代码稍多点而已
   * <p>
   * 不能使用 @Inject 注入的原因是 AdminAuthKit 工具对象
   * 的创建并不是由 jfinal 接管的，而 controller、interceptor
   * 的创建是由 jfinal 接管的，在接管后会自动进行注入动作
   * <p>
   * 所以，这里需要手动 Aop.get(...)
   */
  static AdminAuthService adminAuthSrv = Aop.get(AdminAuthService.class);

  /**
   * 当前账号是否拥有某些角色
   */
  public boolean hasRole(String... roleNameArray) {
    Account account = AdminAuthInterceptor.getThreadLocalAccount();
    if (account != null && !account.isStatusLock()) {
      if (adminAuthSrv.isSuperAdmin(account.getId()) ||
          adminAuthSrv.hasRole(account.getId(), roleNameArray)) {
        return true;
      }
    }

    return false;
  }

  /**
   * 是否拥有具体某个权限
   */
  public boolean hasPermission(String actionKey) {
    Account account = AdminAuthInterceptor.getThreadLocalAccount();
    if (account != null && !account.isStatusLock()) {
      if (adminAuthSrv.isSuperAdmin(account.getId()) ||
          adminAuthSrv.hasPermission(account.getId(), actionKey)) {
        return true;
      }
    }

    return false;
  }
}


