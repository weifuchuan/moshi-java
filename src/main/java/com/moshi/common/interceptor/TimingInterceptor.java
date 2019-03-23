package com.moshi.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.log.Log;

import java.util.Date;

public class TimingInterceptor implements Interceptor {
  @Override
  public void intercept(Invocation inv) {
    long start = new Date().getTime();
    inv.invoke();
    long end = new Date().getTime();
    Log log = Log.getLog("timing");
    log.info("Timing " + inv.getControllerKey() + ": " + (end - start) + "ms");
  }
}
