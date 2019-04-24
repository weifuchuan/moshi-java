package com.moshi.im.server

import com.jfinal.kit.Kv
import com.moshi.common.model.Subscription
import com.moshi.common.mq.RedisMQ
import com.moshi.im.common.Command
import com.moshi.im.common.ImPacket
import com.moshi.im.common.payload.JoinGroupPayload
import com.moshi.im.db.IDao
import com.moshi.im.kit.TioKit
import com.moshi.im.server.config.ImServerConfig
import org.tio.core.stat.IpStatListener
import org.tio.server.ServerGroupContext
import com.moshi.im.server.websocket.server.WsServerAioListener
import com.moshi.im.server.websocket.server.WsServerStarter
import com.moshi.im.server.websocket.server.handler.IWsMsgHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.tio.cluster.TioClusterConfig
import org.tio.core.GroupContext

import java.io.IOException

class ImServer @Throws(Exception::class)
constructor(
  config: ImServerConfig,
  handler: IWsMsgHandler,
  listener: WsServerAioListener,
  ipStatListener: IpStatListener,
  tioClusterConfig: TioClusterConfig,
  private val mq: RedisMQ,
  private val dao: IDao
) {
  private val starter: WsServerStarter = WsServerStarter(config, handler)
  private val serverGroupContext: ServerGroupContext = starter.serverGroupContext

  init {
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

    // TODO: Maybe we need a way to handle system kill signal?
  }

  @Throws(IOException::class)
  fun start() {
    onStart()
    starter.start()
  }

  fun stop() {
    onStop()
    starter.tioServer.stop()
  }

  private fun onStart() {
    mq.onMessage<Subscription>("subscribed") {
      if (it.subscribeType == Subscription.SUB_TYPE_COURSE)
        TioKit.notifyClusterForAll(
          serverGroupContext,
          ImPacket<JoinGroupPayload>().setCommand(Command.COMMAND_JOIN_GROUP_REQ).setPayload(
            JoinGroupPayload().setAccountId(it.accountId).setGroupId(it.refId)
          )
        )
    }
    dao.incrClusterCount()
  }

  private fun onStop() {
    dao.decrClusterCount()
  }
}
