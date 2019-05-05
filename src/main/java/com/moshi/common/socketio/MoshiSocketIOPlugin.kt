package com.moshi.common.socketio

import com.corundumstudio.socketio.SocketIOServer
import com.jfinal.plugin.IPlugin
import kotlinx.coroutines.GlobalScope

class MoshiSocketIOPlugin(val endPoints: List<SocketIOServerEndPoint<Action>>) : IPlugin {
  private var server: MoshiSocketIOServer? = null


  override fun start(): Boolean {
    GlobalScope.run {
      server = MoshiSocketIOServer()
      server!!.start(endPoints)
    }
    return true
  }

  override fun stop(): Boolean {
    server!!.stop()
    return true
  }
}
