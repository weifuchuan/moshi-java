package com.moshi.srv.v1.im;

import com.jfinal.aop.Before;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.UnlockInterceptor;
import com.moshi.common.plugin.Letture;
import io.jboot.Jboot;
import io.lettuce.core.SetArgs;

import java.util.Date;

public class ImController extends BaseController {
  @Before(UnlockInterceptor.class)
  public void index() {
    String key = HashKit.sha256(new Date().getTime() + "" + Math.random()).substring(0,12);
    Letture.sync().set(key, getLoginAccount(), SetArgs.Builder.ex(60 * 5));
    renderJson(
        Ret.ok("key", key)
            .set(
                "url",
                Jboot.configValue("host").trim()
                    + ":"
                    + Jboot.configValue("socketio.port").trim()
                    + "/srv/v1/im"));
  }
}
