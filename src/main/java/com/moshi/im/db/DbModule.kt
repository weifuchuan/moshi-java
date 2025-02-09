package com.moshi.im.db

import com.moshi.im.common.CourseServiceGrpc
import com.moshi.im.kit.ConfigKit
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.RedisCodec
import org.codejargon.feather.Provides
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import com.jfinal.kit.Prop

import javax.inject.Singleton

class DbModule {

  @Provides
  @Singleton
  fun redisURI(config: Prop): RedisURI {
    return ConfigKit.createConfigObject(config.properties, RedisURI::class.java, "im.redis")
  }

  @Provides
  @Singleton
  fun redisClient(config: Prop): RedisClient {
    val uri = ConfigKit.createConfigObject(config.properties, RedisURI::class.java, "im.redis")
    val client = RedisClient.create(uri)
    R.init(client)
    return client
  }

  @Provides
  @Singleton
  fun redisConnection(
    redisClient: RedisClient, redisCodec: RedisCodec<String, Any>
  ): StatefulRedisConnection<String, Any> {
    return redisClient.connect(redisCodec)
  }

  @Provides
  @Singleton
  fun redisCodec(): RedisCodec<String, Any> {
    return FstRedisCodec()
  }

  @Provides
  @Singleton
  fun dao(
    redisClient: RedisClient /* for exec R.init() */,
    courseService: CourseServiceGrpc.CourseServiceBlockingStub,
    redissonClient: RedissonClient
  ): IDao {
    return DaoByRedis(courseService, redissonClient)
  }

  @Provides
  @Singleton
  fun redissonClient(uri: RedisURI): RedissonClient {
    val config = Config()
    val singleServerConfig = config.useSingleServer()
    singleServerConfig.setAddress("redis://" + uri.host + ":" + uri.port)
    if (uri.password != null) singleServerConfig.password = String(uri.password)
    return Redisson.create(config)
  }
}
