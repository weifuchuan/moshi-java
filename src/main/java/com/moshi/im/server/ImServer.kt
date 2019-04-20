package com.moshi.im.server

import com.moshi.im.server.config.ImServerConfig
import org.tio.core.stat.IpStatListener
import org.tio.server.ServerGroupContext
import com.moshi.im.server.websocket.server.WsServerAioListener
import com.moshi.im.server.websocket.server.WsServerStarter
import com.moshi.im.server.websocket.server.handler.IWsMsgHandler
import org.tio.cluster.TioClusterConfig

import java.io.IOException

class ImServer @Throws(Exception::class)
constructor(
  config: ImServerConfig,
  handler: IWsMsgHandler,
  listener: WsServerAioListener,
  ipStatListener: IpStatListener,
  tioClusterConfig: TioClusterConfig
) {
  val starter: WsServerStarter = WsServerStarter(config, handler)

  init {
    val serverGroupContext = starter.serverGroupContext
    serverGroupContext.name = config.protocolName
    serverGroupContext.serverAioListener = listener

    serverGroupContext.ipStatListener = ipStatListener
    serverGroupContext.ipStats.addDurations(ImServerConfig.IpStatDuration.IPSTAT_DURATIONS)

    serverGroupContext.tioClusterConfig = tioClusterConfig

    // SSL
    if (config.isUseSsl) {
      val keyStoreFile = config.sslKeystore
      val trustStoreFile = config.sslTruststore
      val keyStorePwd = config.sslpPwd
      serverGroupContext.useSsl(keyStoreFile, trustStoreFile, keyStorePwd)
    }
  }

  @Throws(IOException::class)
  fun start() {
    starter.start()
  }
}
