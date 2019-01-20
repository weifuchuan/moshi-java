package com.moshi;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.template.Engine;
import com.moshi._admin.common.AdminRoutes;
import com.moshi.common.interceptor.LoginSessionInterceptor;

import io.jboot.app.JbootApplication;
import io.jboot.core.listener.JbootAppListenerBase;

public class App extends JbootAppListenerBase {
  public static void main(String[] args) {
    JbootApplication.run(args);
  }

  @Override
  public void onJfinalConstantConfig(Constants me) {
    super.onJfinalConstantConfig(me);

    me.setJsonFactory(MixedJsonFactory.me());
  }

  @Override
  public void onJfinalRouteConfig(Routes routes) {
    super.onJfinalRouteConfig(routes);

    routes.add(new AdminRoutes());
  }

  @Override
  public void onJfinalEngineConfig(Engine me) {
    super.onJfinalEngineConfig(me);


  }

  @Override
  public void onInterceptorConfig(Interceptors interceptors) {
    super.onInterceptorConfig(interceptors);

    interceptors.add(new LoginSessionInterceptor());
  }
}
