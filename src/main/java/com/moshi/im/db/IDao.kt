package com.moshi.im.db

import com.moshi.im.common.model.MessageDetailModel
import com.moshi.im.common.model.RoomInfoModel
import com.moshi.im.common.payload.ChatReqPayload
import com.jfinal.kit.Kv
import org.tio.utils.page.Page
import java.util.concurrent.ExecutionException

interface IDao {
  @Throws(Exception::class)
  fun sendChatMsg(payload: ChatReqPayload, from: String): Kv

  @Throws(Exception::class)
  fun saveMessage(msg: MessageDetailModel, roomKey: String)

  @Throws(Exception::class)
  fun saveRoomInfo(room: RoomInfoModel, roomKey: String, members: Set<String>)

  @Throws(Exception::class)
  fun addRemind(msgKey: String, roomKey: String, accountId: String)

  @Throws(Exception::class)
  fun clearRemind(roomKey: String, accountId: String)

  @Throws(Exception::class)
  fun joinedRoomList(id: String): List<Map<*, *>>

  @Throws(Exception::class)
  fun joinRoom(id: String, roomKey: String)

  @Throws(Exception::class)
  fun joinRoom(idToRoomKey: Map<String, String>)

  @Throws(Exception::class)
  fun getRoomInfo(roomKey: String, accountId: String): RoomInfoModel

  @Throws(Exception::class)
  fun messagePage(roomKey: String, pageNumber: Int?, pageSize: Int?): Page<MessageDetailModel>

  fun nextId(): String

   fun onlineCountForUserId(userId: String): Int

   fun incrOnlineCount(userId: String)

   fun decrOnlineCount(userId: String)

   fun isOnline(userId: String): Boolean
}
