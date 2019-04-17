package com.moshi.srv.v1.explore;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.TimingInterceptor;
import com.moshi.common.model.Course;
import com.moshi.srv.v1.article.ArticleService;
import com.moshi.srv.v1.course.CourseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.moshi.common.model.Article.COURSE_ID_FOR_NEWS;

public class ExploreController extends BaseController {

  @Inject ExploreService srv;
  @Inject CourseService csrv;
  @Inject ArticleService asrv;

  @Before(TimingInterceptor.class)
  public void index() throws ExecutionException, InterruptedException {

    List<List> retList =
        Stream.of(
                getHotCourseList(Course.TYPE_COLUMN),
                getHotCourseList(Course.TYPE_VIDEO),
                getNewsList(),
                getSubscribedCourses())
            .parallel()
            .map(
                cb -> {
                  try {
                    return cb.call();
                  } catch (Exception e) {
                    return Collections.emptyList();
                  }
                })
            .collect(Collectors.toList());

    List hotColumnList, hotVideoList, newsList, subscribedCourseList;

    hotColumnList = retList.get(0);
    hotVideoList = retList.get(1);
    newsList = retList.get(2);
    subscribedCourseList = retList.get(3);

    Ret ret =
        Ret.ok("hotColumnList", hotColumnList)
            .set("hotVideoList", hotVideoList)
            .set("newsList", newsList)
            .set("subscribedCourseList", subscribedCourseList);

    renderJsonThreadSafe(ret);
  }

  public void newsList() throws Exception {
    List list = getNewsList().call();
    renderJsonThreadSafe(list);
  }

  public void subscribedCourses() throws Exception {
    List list = getSubscribedCourses().call();
    renderJsonThreadSafe(list);
  }

  public void hotCourseList() {

    List<List> retList =
      Stream.of(
        getHotCourseList(Course.TYPE_COLUMN),
        getHotCourseList(Course.TYPE_VIDEO))
        .parallel()
        .map(
          cb -> {
            try {
              return cb.call();
            } catch (Exception e) {
              return Collections.emptyList();
            }
          })
        .collect(Collectors.toList());

    List hotColumnList, hotVideoList;

    hotColumnList = retList.get(0);
    hotVideoList = retList.get(1);

    Ret ret =
      Ret.ok("hotColumnList", hotColumnList)
        .set("hotVideoList", hotVideoList);

    renderJsonThreadSafe(ret);
  }

  private Callable<List> getSubscribedCourses() {
    return () -> {
      if (isLogin()) {
        List<Record> list = csrv.subscribedCourses(getLoginAccountId());
        return list;
      } else {
        return new ArrayList<>();
      }
    };
  }

  private Callable<List> getHotCourseList(int typeColumn) {
    return () -> csrv.hot(3, typeColumn, getLoginAccountId());
  }

  private Callable<List> getNewsList() {
    return () -> {
      try {
        return asrv.list(COURSE_ID_FOR_NEWS, "desc", 1, 6, null).getList();
      } catch (Exception ex) {
        return Collections.emptyList();
      }
    };
  }
}
