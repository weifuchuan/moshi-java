package com.moshi.common.mq

import com.moshi.common.plugin.FstRedisCodec
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisFuture
import io.lettuce.core.RedisURI
import java.io.Serializable

class RedisMQ(uri: RedisURI) {
  constructor(uri: RedisURI, prefix: String) : this(uri) {
    this.prefix = prefix
  }

  private var prefix: String = "redismq:"
  private val defaultTopic = "__DEFAULT_TOPIC__"
  private val pubCli = RedisClient.create(uri)
  private val subCli = RedisClient.create(uri)
  private val pub = pubCli.connectPubSub(codec)
  private val sub = subCli.connectPubSub(codec)

  fun <M : Serializable> publish(message: M) =
    publish(defaultTopic, message)

  fun <M : Serializable> publish(topic: String, message: M): RedisFuture<Long> {
    val async = pub.async()
    return async.publish(prefix + topic, message)
  }

  fun <M : Serializable> onMessage(listener: (msg: M) -> Unit): () -> Unit =
    onMessage(defaultTopic, listener)

  fun <M : Serializable> onMessage(topic: String, listener: (msg: M) -> Unit): () -> Unit {
    val reactive = sub.reactive()
    reactive.subscribe(prefix + topic).subscribe()
    val disposable = reactive.observeChannels().doOnNext { msg ->
      listener(msg.message as M)
    }.subscribe()
    return {
      disposable.dispose()
    }
  }

  fun stop() {
    pubCli.shutdown()
    subCli.shutdown()
  }

  companion object {
    private val codec = FstRedisCodec()
  }

}
