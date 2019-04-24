package com.moshi.im

import com.jfinal.kit.Ret
import com.jfinal.kit.StrKit
import com.moshi.common.controller.BaseController
import com.moshi.common.plugin.Letture
import io.jboot.web.controller.annotation.RequestMapping
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

@RequestMapping("/im")
class ImController : BaseController() {

  fun token() {
    if (isLogin) {
      val sync = Letture.sync()
      val token = StrKit.getRandomUUID()
      sync.sadd(IM_AUTH + loginAccountId, token)
      renderJson(Ret.ok("token", token))
      Observable.timer(5, TimeUnit.SECONDS).subscribe {
        sync.srem(IM_AUTH + loginAccountId, token)
      }
    } else {
      renderJson(Ret.fail("msg", "未登录"))
    }
  }

  companion object {
    val IM_AUTH = "moshi:im:auth:"
  }
}
