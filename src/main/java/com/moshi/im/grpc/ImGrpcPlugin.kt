package com.moshi.im.grpc

import com.jfinal.plugin.IPlugin
import io.grpc.Server
import io.grpc.ServerBuilder
import com.jfinal.kit.Prop
import com.moshi.common.MainConfig

class ImGrpcPlugin : IPlugin {
  private val server: Server

  init {
    val prop = MainConfig.loadConfig()

    server = ServerBuilder
      .forPort(prop["im.grpc.port"].toInt())
      .addService(AccountServiceImpl())
      .addService(AuthServiceImpl())
      .addService(CourseServiceImpl())
      .build()
  }

  override fun start(): Boolean {
    server.start()
    return true
  }

  override fun stop(): Boolean {
    server.shutdownNow()
    return true
  }
}
