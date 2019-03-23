package com.moshi.srv.v1.explore;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.serializer.FstSerializer;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.TimingInterceptor;
import com.moshi.common.model.Article;
import com.moshi.common.model.Course;
import com.moshi.srv.v1.article.ArticleService;
import com.moshi.srv.v1.course.CourseService;
import io.jboot.Jboot;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.serializer.JbootSerializer;
import javafx.util.Callback;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.moshi.common.model.Article.COURSE_ID_FOR_NEWS;

public class ExploreController extends BaseController {
  private static final int MAX_TASK_COUNT = 1000;
  private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

  private static final JbootSerializer serializer =
      new io.jboot.components.serializer.FstSerializer();

  @Inject ExploreService srv;
  @Inject CourseService csrv;
  @Inject ArticleService asrv;

  @Before(TimingInterceptor.class)
  public void index() throws ExecutionException, InterruptedException {

    //    List<Future<List>> futureList =
    //        invokeAll(
    //            Stream.of(
    //                    getHotCourseList(Course.TYPE_COLUMN),
    //                    getHotCourseList(Course.TYPE_VIDEO),
    //                    getNewsList(),
    //                    getSubscribedCourses())
    //                .collect(Collectors.toList()));

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

    //    hotColumnList = futureList.get(0).get();
    //    hotVideoList = futureList.get(1).get();
    //    newsList = futureList.get(2).get();
    //    subscribedCourseList = futureList.get(3).get();

    hotColumnList = retList.get(0);
    hotVideoList = retList.get(1);
    newsList = retList.get(2);
    subscribedCourseList = retList.get(3);

    Ret ret =
        Ret.ok("hotColumnList", hotColumnList)
            .set("hotVideoList", hotVideoList)
            .set("newsList", newsList)
            .set("subscribedCourseList", subscribedCourseList);

    try {
      renderJson(ret);
    } catch (ConcurrentModificationException ex) {
      renderJson(serializer.deserialize(serializer.serialize(ret)));
    }
  }

  private List<Future<List>> invokeAll(Collection<Callable<List>> tasks)
      throws InterruptedException {
    if (((ThreadPoolExecutor) EXECUTOR).getActiveCount() > MAX_TASK_COUNT) {
      return tasks.stream()
          .map(
              task -> {
                try {
                  return task.call();
                } catch (Exception e) {
                  return Collections.emptyList();
                }
              })
          .map(ret -> (Future<List>) (new FutureTask(() -> ret)))
          .collect(Collectors.toList());
    } else {
      return EXECUTOR.invokeAll(tasks);
    }
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
