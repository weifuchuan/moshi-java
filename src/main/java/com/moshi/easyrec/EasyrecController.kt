package com.moshi.easyrec

import com.moshi.common.controller.BaseController

open class EasyrecController : BaseController() {
  fun index() {
    val config = EasyrecService.config.clone()
    config.sessionid = session.id
    renderJson(EasyrecService.config)
  }
}
