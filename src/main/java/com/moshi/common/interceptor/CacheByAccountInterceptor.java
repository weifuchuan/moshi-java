package com.moshi.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.moshi.common.model.Account;
import com.moshi.login.LoginService;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCache;

public class CacheByAccountInterceptor implements Interceptor {
  private static final String cacheName="CacheByAccountInterceptor";

  @Override
  public void intercept(Invocation inv) {
    String key = "";
    Controller controller = inv.getController();
    key+=controller.getControllerKey();
    Account me = controller.getAttr(LoginService.loginAccountCacheName);
    if(me!=null){
      key+=":"+me.getId();
    }
    JbootCache cache = Jboot.getCache();
    Object cachedValue = cache.get(cacheName, key);

  }
}
