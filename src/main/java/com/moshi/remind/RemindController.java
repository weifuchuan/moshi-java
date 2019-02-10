package com.moshi.remind;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.UnlockInterceptor;
import com.moshi.common.model.Remind;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

@Before(UnlockInterceptor.class)
@RequestMapping("/remind")
public class RemindController extends BaseController {
  @Inject private RemindService srv;

  public void my() {
    Boolean remaining = getParaToBoolean("remaining", false);
    Long rstart;
    if (remaining) {
      rstart = getSessionAttr("lastRStart", srv.len(getLoginAccountId()) - 1);
    } else {
      rstart = srv.len(getLoginAccountId()) - 1;
    }
    List<Remind> reminds = srv.take(getLoginAccountId(), rstart, rstart - 10 + 1);
    renderJson(reminds);
  }
}
