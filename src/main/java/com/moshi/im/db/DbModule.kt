package com.moshi.im.db

import com.moshi.im.kit.ConfigKit
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.RedisCodec
import org.codejargon.feather.Provides
import org.tio.utils.jfinal.Prop

import javax.inject.Singleton

class DbModule {

  @Provides
  @Singleton
  fun redisClient(config: Prop): RedisClient {
    val uri = ConfigKit.createConfigObject(config.properties, RedisURI::class.java, "redis")
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
  fun dao(redisClient: RedisClient /* for exec R.init() */): IDao {
    return DaoByRedis()
  }
}
