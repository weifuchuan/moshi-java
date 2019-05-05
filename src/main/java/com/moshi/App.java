package com.moshi;

import cn.hutool.core.util.ReflectUtil;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.template.Engine;
import com.moshi._admin.common.AdminRoutes;
import com.moshi.common.interceptor.LoginSessionInterceptor;
import com.moshi.common.kit.ConfigKit;
import com.moshi.common.model._MappingKit;
import com.moshi.common.plugin.LetturePlugin;
import com.moshi.common.plugin.RedisMQPlugin;
import com.moshi.common.socketio.MoshiSocketIOPlugin;
import com.moshi.common.socketio.MoshiSocketIOServer;
import com.moshi.easyrec.EasyrecService;
import com.moshi.im.ImPlugin;
import com.moshi.im.grpc.ImGrpcPlugin;
import com.moshi.reg.AccountGenerator;
import com.moshi.srv.v1.SrvV1Routes;
import io.jboot.Jboot;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.app.JbootApplication;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jboot.utils.ClassScanner;
import io.lettuce.core.RedisURI;
import io.reactivex.Observable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class App extends JbootAppListenerBase {

  public static void main(String[] args) {
    JbootApplication.run(args);
  }

  @Override
  public void onConstantConfig(Constants me) {
    me.setJsonFactory(MixedJsonFactory.me());
    me.setBaseUploadPath("static/media");
  }

  @Override
  public void onRouteConfig(Routes routes) {
    routes.add(new AdminRoutes());
    routes.add(new SrvV1Routes());
  }

  @Override
  public void onEngineConfig(Engine me) {}

  @Override
  public void onInterceptorConfig(Interceptors interceptors) {
    interceptors.add(new LoginSessionInterceptor());
  }

  @Override
  public void onPluginConfig(JfinalPlugins plugins) {
    Plugins plugins1 = (Plugins) ReflectUtil.getFieldValue(plugins, "plugins");
    plugins1
        .getPluginList()
        .forEach(
            p -> {
              if (p.getClass().getName().equals(ActiveRecordPlugin.class.getName())) {
                ActiveRecordPlugin arp = (ActiveRecordPlugin) p;
                List<Table> tableList = (List<Table>) ReflectUtil.getFieldValue(arp, "tableList");
                tableList.clear();
                _MappingKit.mapping(arp);
              }
            });
    RedisURI uri =
        ConfigKit.createConfigObject(
            new Prop("jboot.properties").getProperties(), RedisURI.class, "jboot.redis");
    plugins.add(new LetturePlugin(uri));
    plugins.add(new MoshiSocketIOPlugin());
    plugins.add(new RedisMQPlugin(uri));
    plugins.add(new ImGrpcPlugin());

    if (!Jboot.isDevMode()) {
      plugins.add(new ImPlugin());
    }
  }

  @Override
  public void onStart() {
//    EasyrecService.Companion.mock();
  }
}
