package com.moshi.common.interceptor

import com.jfinal.aop.Interceptor
import com.jfinal.aop.Invocation
import com.jfinal.log.Log

import java.util.Date

class TimingInterceptor : Interceptor {


  override fun intercept(inv: Invocation) {
    val start = System.currentTimeMillis()
    inv.invoke()
    val end = System.currentTimeMillis()
    log.info(
      "Timing " + inv.controller.request.requestURI + ": " + (end - start) + "ms"
    )
  }

  companion object {
    val log = Log.getLog("timing")
  }
}
