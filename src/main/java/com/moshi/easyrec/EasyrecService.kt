package com.moshi.easyrec

import cn.hutool.core.math.MathUtil
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import com.jfinal.kit.Kv
import com.jfinal.kit.StrKit
import com.moshi.common.MainConfig
import com.moshi.common.kit.ConfigKit
import com.moshi.common.model.Account
import com.moshi.common.model.Course
import kotlinx.coroutines.*

class EasyrecService {


  // https://sourceforge.net/p/easyrec/wiki/ActionAPI/#view
  fun view(
    sessionid: String, itemid: String, itemdescription: String, itemurl: String, optional: Kv
  ): HttpResponse {
    val response = get(
      "/api/1.1/view",
      Kv.by("sessionid", sessionid)
        .set("itemid", itemid)
        .set("itemdescription", itemdescription)
        .set("itemurl", itemurl)
        .set(optional)
    )
    return response
  }

  // https://sourceforge.net/p/easyrec/wiki/ActionAPI/#buy
  fun buy(
    sessionid: String, itemid: String, itemdescription: String, itemurl: String, optional: Kv
  ): HttpResponse {
    val response = get(
      "/api/1.1/buy",
      Kv.by("sessionid", sessionid)
        .set("itemid", itemid)
        .set("itemdescription", itemdescription)
        .set("itemurl", itemurl)
        .set(optional)
    )
    return response
  }

  // https://sourceforge.net/p/easyrec/wiki/ActionAPI/#rate
  fun rate(
    sessionid: String, itemid: String, itemdescription: String, itemurl: String, ratingvalue: Int, optional: Kv
  ) {
    val response = get(
      "/api/1.1/rate",
      Kv.by("sessionid", sessionid)
        .set("itemid", itemid)
        .set("itemdescription", itemdescription)
        .set("itemurl", itemurl)
        .set("ratingvalue", ratingvalue)
        .set(optional)
    )
  }

  // https://sourceforge.net/p/easyrec/wiki/ActionAPI/#sendaction
  // 虽然view，buy和rate调用是针对典型的电子商务商店应用程序量身定制的，但easyrec允许根据特定用例的要求定义任意新的操作类型。您可以使用通用sendaction API调用将任何类型的操作发送到easyrec。使用actiontype参数指定操作。注意：必须先使用Admin GUI创建操作类型，然后才能将其接受为有效的API调用。
  fun sendaction(
    sessionid: String, itemid: String, itemdescription: String, itemurl: String, actiontype: String, optional: Kv
  ) {
    val response = get(
      "/api/1.1/sendaction",
      Kv.by("sessionid", sessionid)
        .set("itemid", itemid)
        .set("itemdescription", itemdescription)
        .set("itemurl", itemurl)
        .set("actiontype", actiontype)
        .set(optional)
    )
  }

  // https://sourceforge.net/p/easyrec/wiki/ActionAPI/#track
  // 跟踪操作可用于跟踪建议的点击次数。当用户点击推荐的项目时应该调用它。这样，如果向用户显示的推荐被欣赏，则可以跟踪它。
  fun track(
    sessionid: String, itemtoid: String, rectype: String, optional: Kv
  ) {
    val response = get(
      "/api/1.1/json/track",
      Kv.by("sessionid", sessionid)
        .set("itemtoid", itemtoid)
        .set("rectype", rectype)
        .set(optional)
    )
  }

  private fun get(uri: String, params: Kv): HttpResponse {
    return HttpRequest.get(config.serverUrl.trim { it <= ' ' } + uri.trim { it <= ' ' })
      .form(params.set("apikey", config.apikey).set("tenantid", config.tenantid) as Map<String, Any>)
      .execute()
  }

  private fun post(uri: String, params: Kv): HttpResponse {
    return HttpRequest.post(config.serverUrl.trim { it <= ' ' } + uri.trim { it <= ' ' })
      .form(params.set("apikey", config.apikey).set("tenantid", config.tenantid) as Map<String, Any>)
      .execute()
  }

  companion object {
    val config = ConfigKit.createConfigObject(MainConfig.p!!.properties, EasyrecConfig::class.java, "easyrec")

    fun mock() {
      val srv = EasyrecService()
      val accountDao = Account().dao()
      val courseDao = Course().dao()
      val accountList = accountDao.findAll()
      val courseList = courseDao.findAll()

      runBlocking {
        for (c in courseList) {
          if (c.id < 1) continue
          println("for course ${c.name}")
          val sid = StrKit.getRandomUUID()
          GlobalScope.launch {
            for (i in 1..10000) {
              val j = Math.floor(Math.random() * accountList.size).toInt()
              val a = accountList[j]
              val resp=srv.view(sid, c.id.toString(), c.id.toString(), "course/${c.id}", Kv.by("userid", a.id.toString()).set("itemtype","COURSE"))
              println("${a.nickName} view ${c.name} by ${resp.status}")
            }
          }
          GlobalScope.launch {
            for (i in 1..1000) {
              val j = Math.floor(Math.random() * accountList.size).toInt()
              val a = accountList[j]
              val resp=srv.buy(sid, c.id.toString(), c.id.toString(), "course/${c.id}", Kv.by("userid", a.id.toString()).set("itemtype","COURSE"))
              println("${a.nickName} buy ${c.name} by ${resp.status}")
            }
          }
        }


      }

    }
  }
}
