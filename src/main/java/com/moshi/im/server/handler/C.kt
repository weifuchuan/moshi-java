package com.moshi.im.server.handler

import com.alibaba.fastjson.JSON
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tio.core.ChannelContext

object C {
  // except unmodifiableMap
  val bus: Subject<Map<*, *>> = PublishSubject.create<Map<*, *>>().toSerialized()
}
