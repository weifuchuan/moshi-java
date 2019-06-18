package com.moshi.im.server.handler

import com.moshi.im.common.Command
import com.moshi.im.common.ImPacket
import com.moshi.im.db.IDao
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tio.core.ChannelContext
import org.tio.websocket.common.WsRequest

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.util.HashMap

class ActualHandlerMapper(dao: IDao) : HashMap<Command, IActualHandler>(), IActualHandler {

  init {
    val reflections = Reflections(javaClass.getPackage().name)
    reflections
      .getSubTypesOf(BaseActualHandler::class.java)
      .forEach { clz ->
        val annotation = clz.getAnnotation(HandlerForCommand::class.java)
        if (annotation != null) {
          val command = annotation.value
          try {
            val constructor = clz.getConstructor(IDao::class.java)
            put(command, constructor.newInstance(dao))
          } catch (e: InstantiationException) {
            e.printStackTrace()
          } catch (e: NoSuchMethodException) {
            e.printStackTrace()
          } catch (e: InvocationTargetException) {
            e.printStackTrace()
          } catch (e: IllegalAccessException) {
            e.printStackTrace()
          }

        }
      }
  }

  @Throws(Exception::class)
  override fun handle(packet: ImPacket<*>, req: WsRequest, ctx: ChannelContext): Any? {
    return if (containsKey(packet.command)) {
      get(packet.command)!!.handle(packet, req, ctx)
    } else {
      log.warn("Unsupported command", packet.command)
      null
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(ActualHandlerMapper::class.java)
  }
}
