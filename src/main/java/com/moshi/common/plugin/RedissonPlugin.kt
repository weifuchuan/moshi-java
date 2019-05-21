package com.moshi.common.plugin

import com.jfinal.plugin.IPlugin

class RedissonPlugin () : IPlugin {


  override fun start(): Boolean {
    return true
  }

  override fun stop(): Boolean {
    return true
  }

}
