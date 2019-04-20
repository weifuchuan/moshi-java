package com.moshi.im.grpc

import com.jfinal.plugin.IPlugin
import io.grpc.Server
import io.grpc.ServerBuilder
import org.tio.utils.jfinal.Prop

class ImGrpcPlugin : IPlugin {
  private val server: Server

  init {
    val prop = Prop("im/config.properties")

    server = ServerBuilder
      .forPort(prop["grpc.port"].toInt())
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
