package com.moshi.common

import com.jfinal.config.*
import com.jfinal.json.MixedJsonFactory
import com.jfinal.kit.Prop
import com.jfinal.kit.PropKit
import com.jfinal.plugin.activerecord.ActiveRecordPlugin
import com.jfinal.plugin.cron4j.Cron4jPlugin
import com.jfinal.plugin.druid.DruidPlugin
import com.jfinal.plugin.ehcache.EhCachePlugin
import com.jfinal.template.Engine
import com.moshi._admin.common.AdminRoutes
import com.moshi.common.interceptor.LoginSessionInterceptor
import com.moshi.common.kit.ConfigKit
import com.moshi.common.model._MappingKit
import com.moshi.common.plugin.LetturePlugin
import com.moshi.common.plugin.RedisMQPlugin
import com.moshi.common.socketio.MoshiSocketIOPlugin
import com.moshi.easyrec.EasyrecService
import com.moshi.im.ImPlugin
import com.moshi.im.grpc.ImGrpcPlugin
import com.moshi.srv.v1.SrvV1Routes
import com.moshi.srv.v1.subscribe.SubscribeConfirmServerEndPoint
import io.lettuce.core.RedisURI
import java.sql.Connection
import java.util.*

class MainConfig : JFinalConfig() {
  override fun configConstant(me: Constants) {
    loadConfig()

    me.devMode = p!!.getBoolean("devMode", false)
    me.setJsonFactory(MixedJsonFactory.me())
    me.injectDependency = true
    me.injectSuperClass = true
    me.baseUploadPath = "static/media"
  }

  override fun configPlugin(me: Plugins) {
    val druidPlugin = getDruidPlugin()
    me.add(druidPlugin)

    val arp = ActiveRecordPlugin(druidPlugin)
    arp.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED)
    _MappingKit.mapping(arp)
    arp.setShowSql(p!!.getBoolean("devMode", false)!!)

    arp.engine.setToClassPathSourceFactory()
    arp.addSqlTemplate("/sql/all_sqls.sql")

    me.add(arp)
    me.add(EhCachePlugin())
    me.add(Cron4jPlugin())

    val uri = ConfigKit.createConfigObject(
      p!!.properties, RedisURI::class.java, "redis"
    )
    me.add(LetturePlugin(uri))
    me.add(MoshiSocketIOPlugin(Arrays.asList(SubscribeConfirmServerEndPoint())))
    me.add(RedisMQPlugin(uri))
    me.add(ImGrpcPlugin())

    if (!p!!.getBoolean("devMode", false)) {
      me.add(ImPlugin())
    }
  }

  override fun configInterceptor(me: Interceptors) {
    me.add(LoginSessionInterceptor())
  }

  override fun configHandler(me: Handlers) {
  }

  override fun configRoute(me: Routes) {
    me.add(FrontRoutes())
    me.add(AdminRoutes())
    me.add(SrvV1Routes())
  }

  override fun configEngine(me: Engine) {
    me.devMode = p!!.getBoolean("dev", false)
  }

  override fun onStart() {
    // EasyrecService.mock()
  }

  companion object {
    var p: Prop? = null

    fun loadConfig():Prop {
      p = PropKit.useFirstFound("config-pro.properties", "config.properties", "config-dev.properties")
      return p!!
    }

    fun getDruidPlugin(): DruidPlugin {
      loadConfig()
      return DruidPlugin(
        p!!.get("db.datasource.url").trim(),
        p!!.get("db.datasource.user").trim(),
        p!!.get("db.datasource.password").trim())
    }
  }
}