package com.moshi.im.server

import com.moshi.common.mq.RedisMQ
import com.moshi.im.common.AccountServiceGrpc
import com.moshi.im.common.AuthServiceGrpc
import com.moshi.im.db.IDao
import com.moshi.im.kit.ConfigKit
import com.moshi.im.server.config.ImServerConfig
import com.moshi.im.server.handler.IActualHandler
import com.moshi.im.server.handler.MainHandler
import com.moshi.im.server.handler.ActualHandlerMapper
import com.moshi.im.server.listener.ImIpStatListener
import com.moshi.im.server.listener.MainListener
import org.codejargon.feather.Provides
import org.tio.core.stat.IpStatListener
import org.tio.utils.jfinal.Prop
import com.moshi.im.server.websocket.server.WsServerAioListener
import com.moshi.im.server.websocket.server.handler.IWsMsgHandler
import io.lettuce.core.RedisURI
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.tio.cluster.TioClusterConfig
import org.tio.cluster.redisson.RedissonTioClusterTopic

import javax.inject.Singleton

class ServerModule {
  @Provides
  @Singleton
  fun config(config: Prop): ImServerConfig {
    return ConfigKit.createConfigObject(config.properties, ImServerConfig::class.java, "ws")
  }

  @Provides
  @Singleton
  @Throws(Exception::class)
  fun imServer(
    config: ImServerConfig,
    handler: IWsMsgHandler,
    listener: WsServerAioListener,
    ipStatListener: IpStatListener,
    tioClusterConfig: TioClusterConfig,
    mq: RedisMQ,
    dao:IDao
  ): ImServer {
    return ImServer(config, handler, listener, ipStatListener, tioClusterConfig, mq,dao)
  }

  @Provides
  @Singleton
  fun mainHandler(
    accountService: AccountServiceGrpc.AccountServiceBlockingStub,
    authService: AuthServiceGrpc.AuthServiceBlockingStub,
    realHandler: IActualHandler,
    dao: IDao
  ): IWsMsgHandler {
    return MainHandler(accountService, authService, realHandler, dao)
  }

  @Provides
  @Singleton
  fun mainListener(): WsServerAioListener {
    return MainListener()
  }

  @Provides
  @Singleton
  fun ipStatListener(): IpStatListener {
    return ImIpStatListener()
  }

  @Provides
  @Singleton
  fun realHandler(dao: IDao): IActualHandler {
    return ActualHandlerMapper(dao)
  }


  @Provides
  @Singleton
  fun tioClusterConfig(client: RedissonClient): TioClusterConfig {
    val topic = RedissonTioClusterTopic("moshi-im", client)
    return TioClusterConfig(topic)
  }

  @Provides
  @Singleton
  fun mq(uri: RedisURI): RedisMQ {
    return RedisMQ(uri)
  }
}
