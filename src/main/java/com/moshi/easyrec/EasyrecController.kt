package com.moshi.easyrec

import com.moshi.common.controller.BaseController
import io.jboot.web.controller.annotation.RequestMapping

@RequestMapping("/easyrec")
class EasyrecController : BaseController() {
  fun index() {
    val config = EasyrecService.config.clone()
    config.sessionid = session.id
    renderJson(EasyrecService.config)
  }
}
