package com.moshi.section;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.IsTeacherOrManagerInterceptor;
import com.moshi.common.interceptor.UnlockInterceptor;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/section")
public class SectionController extends BaseController {
  @Inject private SectionService srv;

  @Before({UnlockInterceptor.class, IsTeacherOrManagerInterceptor.class})
  public void create() {
    Ret ret = srv.create(getParaToInt("courseId"), getLoginAccountId(), getPara("title"));
    renderJson(ret);
  }

  @Before({UnlockInterceptor.class, IsTeacherOrManagerInterceptor.class})
  public void delete() {
    Ret ret=srv.delete(getParaToInt("id"), getLoginAccountId());
    renderJson(ret);
  }

  @Before({UnlockInterceptor.class, IsTeacherOrManagerInterceptor.class})
  public void update() {

  }

  public void find() {}
}
