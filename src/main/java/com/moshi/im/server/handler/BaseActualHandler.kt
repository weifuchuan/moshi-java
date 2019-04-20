package com.moshi.im.server.handler

import com.moshi.im.ImPlugin
import com.moshi.im.db.IDao
import org.codejargon.feather.Feather

abstract class BaseActualHandler(protected val dao: IDao) : IActualHandler {
  companion object {
    val feather = ImPlugin.feather
  }
}
