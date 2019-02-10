package com.moshi.srv.v1.explore;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Course;
import com.moshi.srv.v1.course.CourseService;

import java.util.List;

public class ExploreController extends BaseController {
  @Inject private ExploreService srv;
  @Inject CourseService csrv;

  public void index() {
    List<Record> hotColumnList = csrv.hot(3, Course.TYPE_COLUMN);
    List<Record> hotVideoList = csrv.hot(3, Course.TYPE_VIDEO);

    renderJson(Ret.ok("hotColumnList", hotColumnList).set("hotVideoList", hotVideoList));
  }
}
