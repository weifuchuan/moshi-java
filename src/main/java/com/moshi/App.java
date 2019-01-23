package com.moshi;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.template.Engine;
import com.moshi._admin.common.AdminRoutes;
import com.moshi.common.interceptor.LoginSessionInterceptor;

import com.moshi.service.GraphQLService;
import io.jboot.Jboot;
import io.jboot.app.JbootApplication;
import io.jboot.core.listener.JbootAppListenerBase;

public class App extends JbootAppListenerBase {
  public static void main(String[] args) {
    JbootApplication.run(args);
  }

  @Override
  public void onJfinalConstantConfig(Constants me) {
    me.setJsonFactory(MixedJsonFactory.me());
  }

  @Override
  public void onJfinalRouteConfig(Routes routes) {
    routes.add(new AdminRoutes());
  }

  @Override
  public void onJfinalEngineConfig(Engine me) {

  }

  @Override
  public void onInterceptorConfig(Interceptors interceptors) {
    interceptors.add(new LoginSessionInterceptor());
  }

  @Override
  public void onJFinalStarted() {
//    GraphQLService.me.start(Jboot.configValue("graphql.host"), Integer.valueOf(Jboot.configValue("graphql.port").trim()));
  }

  @Override
  public void onJFinalStop() {
//    GraphQLService.me.stop();
  }
}
