package com.moshi.im

import com.moshi.common.mq.RedisMQ
import com.moshi.im.common.AccountServiceGrpc
import com.moshi.im.common.AuthServiceGrpc
import com.moshi.im.common.CourseServiceGrpc
import io.grpc.ManagedChannelBuilder
import io.lettuce.core.RedisURI
import org.codejargon.feather.Provides
import org.tio.utils.jfinal.Prop

import javax.inject.Singleton

class ImModule {
  @Provides
  @Singleton
  fun config(): Prop {
    return Prop("im/config.properties")
  }

  @Provides
  @Singleton
  fun accountService(config: Prop): AccountServiceGrpc.AccountServiceBlockingStub {
    val host = config.get("grpc.host").trim { it <= ' ' }
    val port = Integer.valueOf(config.get("grpc.port").trim { it <= ' ' })
    return AccountServiceGrpc.newBlockingStub(
      ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build()
    )
  }

  @Provides
  @Singleton
  fun authService(config: Prop): AuthServiceGrpc.AuthServiceBlockingStub {
    val host = config.get("grpc.host").trim { it <= ' ' }
    val port = Integer.valueOf(config.get("grpc.port").trim { it <= ' ' })
    return AuthServiceGrpc.newBlockingStub(
      ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build()
    )
  }

  @Provides
  @Singleton
  fun courseService(config: Prop): CourseServiceGrpc.CourseServiceBlockingStub {
    val host = config.get("grpc.host").trim { it <= ' ' }
    val port = Integer.valueOf(config.get("grpc.port").trim { it <= ' ' })
    return CourseServiceGrpc.newBlockingStub(
      ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build()
    )
  }

  @Provides
  @Singleton
  fun mq(uri: RedisURI): RedisMQ {
    return RedisMQ(uri)
  }
}
