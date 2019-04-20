package com.moshi.im

import com.jfinal.plugin.IPlugin
import com.moshi.im.db.DbModule
import com.moshi.im.server.ImServer
import com.moshi.im.server.ServerModule
import org.codejargon.feather.Feather

import java.io.IOException

class ImPlugin : IPlugin {

  var server: ImServer? = null

  override fun start(): Boolean {
    server = feather.instance(ImServer::class.java)
    return try {
      server!!.start()
      true
    } catch (e: IOException) {
      e.printStackTrace()
      false
    }
  }

  override fun stop(): Boolean {
    return server!!.starter.tioServer.stop()
  }

  companion object Me {

    val feather: Feather = Feather.with(CustomerServiceModule(), DbModule(), ServerModule())
  }


}
