package com.moshi

import com.jfinal.server.undertow.UndertowConfig
import com.jfinal.server.undertow.UndertowServer
import com.moshi.common.MainConfig
import com.moshi.common.kit.ConfigKit

object App {

  @JvmStatic
  fun main(args: Array<String>) {
    MainConfig.loadConfig()
    val undertowConfig = ConfigKit.createConfigObject(
      MainConfig.p!!.properties,
      UndertowConfig::class.java,
      "undertow",
      arrayOf<Any>(MainConfig::class.java)
    )
    undertowConfig.setDevMode(MainConfig.p!!.getBoolean("devMode", false)!!)
    val server = UndertowServer.create(undertowConfig)
    server.configWeb{

    }
    server.start()
  }
}
