package com.moshi;

import cn.hutool.core.util.ReflectUtil;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.template.Engine;
import com.moshi._admin.common.AdminRoutes;
import com.moshi.common.interceptor.LoginSessionInterceptor;

import com.moshi.common.model._MappingKit;
import com.moshi.srv.v1.SrvV1Routes;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.app.JbootApplication;
import io.jboot.core.listener.JbootAppListenerBase;

import java.util.List;

public class App extends JbootAppListenerBase {
  public static void main(String[] args) {
    JbootApplication.run(args);
  }

  @Override
  public void onJfinalConstantConfig(Constants me) {
    me.setJsonFactory(MixedJsonFactory.me());
    me.setBaseUploadPath("static/media");
  }

  @Override
  public void onJfinalRouteConfig(Routes routes) {
    routes.add(new AdminRoutes());
    routes.add(new SrvV1Routes());
  }

  @Override
  public void onJfinalEngineConfig(Engine me) {}

  @Override
  public void onInterceptorConfig(Interceptors interceptors) {
    interceptors.add(new LoginSessionInterceptor());
  }

  @Override
  public void onJfinalPluginConfig(JfinalPlugins plugins) {
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
  }
}
