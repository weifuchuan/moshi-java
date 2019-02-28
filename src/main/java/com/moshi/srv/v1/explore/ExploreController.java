package com.moshi.srv.v1.explore;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Course;
import com.moshi.srv.v1.course.CourseService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExploreController extends BaseController {
  private static final int MAX_TASK_COUNT = 100;
  private static final ExecutorService executor = Executors.newCachedThreadPool();

  @Inject ExploreService srv;
  @Inject CourseService csrv;

  public void index() throws ExecutionException, InterruptedException {
    long taskCount = ((ThreadPoolExecutor) executor).getTaskCount();
    if (taskCount > MAX_TASK_COUNT) {
      List<Record> hotColumnList = csrv.hot(3, Course.TYPE_COLUMN, getLoginAccountId());
      List<Record> hotVideoList = csrv.hot(3, Course.TYPE_VIDEO, getLoginAccountId());
      Ret ret = Ret.ok("hotColumnList", hotColumnList).set("hotVideoList", hotVideoList);
      if (isLogin()) {
        List<Record> subscribedCourseList = csrv.subscribedCourses(getLoginAccountId());
        ret.set("subscribedCourseList", subscribedCourseList);
      }
      renderJson(ret);
    } else {
      FutureTask<List<Record>> hotColumnList =
          new FutureTask<>(() -> csrv.hot(3, Course.TYPE_COLUMN, getLoginAccountId()));
      FutureTask<List<Record>> hotVideoList =
          new FutureTask<>(() -> csrv.hot(3, Course.TYPE_VIDEO, getLoginAccountId()));
      FutureTask<List<Record>> subscribedCourseList =
          new FutureTask<>(
              () -> {
                if (isLogin()) {
                  List<Record> list = csrv.subscribedCourses(getLoginAccountId());
                  return list;
                } else {
                  return new ArrayList<>();
                }
              });
      executor.submit(hotColumnList);
      executor.submit(hotVideoList);
      executor.submit(subscribedCourseList);

      Ret ret =
          Ret.ok("hotColumnList", hotColumnList.get())
              .set("hotVideoList", hotVideoList.get())
              .set("subscribedCourseList", subscribedCourseList.get());

      renderJson(ret);
    }
  }
}
