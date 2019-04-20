package com.moshi.im.db

import com.moshi.im.kit.FstKit
import io.lettuce.core.codec.RedisCodec

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class FstRedisCodec : RedisCodec<String, Any> {

  override fun decodeKey(bytes: ByteBuffer): String {
    return StandardCharsets.UTF_8.decode(bytes).toString()
  }

  override fun decodeValue(buf: ByteBuffer): Any {
    val array = ByteArray(buf.remaining())
    buf.get(array)
    return FstKit.deserialize(array)
  }

  override fun encodeKey(key: String): ByteBuffer {
    return StandardCharsets.UTF_8.encode(key)
  }

  override fun encodeValue(value: Any): ByteBuffer {
    return ByteBuffer.wrap(FstKit.serialize(value))
  }
}
