package com.moshi.im.server.handler

import com.alibaba.fastjson.JSON
import com.moshi.common.mq.RedisMQ
import com.moshi.im.ImPlugin
import com.moshi.im.common.AccountBaseInfo
import io.lettuce.core.RedisURI
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.cliffc.high_scale_lib.NonBlockingHashMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tio.core.ChannelContext

object C {
  // except unmodifiableMap
  // val bus: Subject<Map<*, *>> = PublishSubject.create<Map<*, *>>().toSerialized()
  val mq: RedisMQ = ImPlugin.feather.instance(RedisMQ::class.java)
  val accountIdToAccount = NonBlockingHashMap<String, AccountBaseInfo>()
}
