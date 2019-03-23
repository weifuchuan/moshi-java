package com.moshi.srv.v1.course;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.moshi.common.model.Article;
import com.moshi.common.model.Course;
import com.moshi.common.model.Subscription;
import com.moshi.common.plugin.Letture;
import com.moshi.srv.v1.article.ArticleService;
import com.moshi.srv.v1.statistics.StatisticsService;
import com.moshi.subscription.SubscriptionService;
import io.jboot.Jboot;
import io.jboot.components.cache.annotation.Cacheable;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.async.RedisAsyncCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CourseService {
  public static final CourseService me = new CourseService();

  private static final String cacheName = "course";

  private Course dao = new Course().dao();

  private SubscriptionService subsSrv = new SubscriptionService();
  private ArticleService articleService = new ArticleService();
  private StatisticsService statisticsService = new StatisticsService();

  private static int compareScore(Record a, Record b) {
    if (a.getDouble("score") > b.getDouble("score")) {
      return -1;
    } else if (a.getDouble("score") < b.getDouble("score")) {
      return 1;
    } else {
      return 0;
    }
  }

  public List<Record> hot(int maxCount, int courseType)
      throws ExecutionException, InterruptedException {
    SqlPara sqlPara = Db.getSqlPara("course.allSimpleByType", courseType);
    List<Record> list =
        new ArrayList<>(
            Db.findByCache(cacheName, "hot:" + courseType, sqlPara.getSql(), sqlPara.getPara()));
    TransactionResult result =
        Letture.multi(
                redis -> list.forEach(item -> redis.zscore("visit:course", item.getInt("id"))))
            .get();
    for (int i = 0; i < list.size(); i++) {
      Record item = list.get(i);
      Double score = result.get(i);
      score = score == null ? 0 : score;
      item.set("score", score + 5 * item.getInt("buyerCount") + 0.5 * item.getInt("lectureCount"));
    }
    list.sort(CourseService::compareScore);
    if (list.size() < maxCount) {
      maxCount = list.size();
    }
    return new ArrayList<>(list.subList(0, maxCount));
  }

  public List<Record> hot(int maxCount, int courseType, Integer accountId)
      throws ExecutionException, InterruptedException {
    if (accountId == null) return hot(maxCount, courseType);
    List<Subscription> subscriptionList = subsSrv.findByAccountId(accountId);
    SqlPara sqlPara = Db.getSqlPara("course.allSimpleByType", courseType);
    List<Record> list =
        new ArrayList<>(
            Db.findByCache(cacheName, "hot:" + courseType, sqlPara.getSql(), sqlPara.getPara()));
    Set<Integer> subscribed =
        subscriptionList.stream()
            .filter(
                s ->
                    s.getSubscribeType().equals("course")
                        && s.getStatus() == Subscription.STATUS_SUCCESS)
            .map(Subscription::getRefId)
            .collect(Collectors.toSet());
    TransactionResult result =
        Letture.multi(
                redis -> list.forEach(item -> redis.zscore("visit:course", item.getInt("id"))))
            .get();
    for (int i = 0; i < list.size(); i++) {
      Record item = list.get(i);
      int id = item.getInt("id");
      Double score = result.get(i);
      score = score == null ? 0 : score;
      if (subscribed.contains(id)) {
        item.set(
            "score", -(score + 5 * item.getInt("buyerCount") + 0.5 * item.getInt("lectureCount")));
      } else
        item.set(
            "score", score + 5 * item.getInt("buyerCount") + 0.5 * item.getInt("lectureCount"));
      item.set("subscribed", subscribed.contains(item.getInt("id")));
    }
    list.sort(CourseService::compareScore);
    if (list.size() < maxCount) {
      maxCount = list.size();
    }
    return new ArrayList<>(list.subList(0, maxCount));
  }

  // TODO: cache result by better way
  public Ret list(
      String type,
      String orderBy,
      String cending,
      int pageNumber,
      int pageSize,
      Integer accountId) {
    String additionalCondition =
        accountId == null
            ? ""
            : " IF(c.id in (select refId from subscription s where s.accountId = "
                + accountId
                + " and subscribeType = '"
                + Subscription.SUB_TYPE_COURSE
                + "'), 1, 0) asc, ";
    int type2 = Course.TYPE_COLUMN;
    if (type.equals("column")) {
      type2 = Course.TYPE_COLUMN;
    } else {
      type2 = Course.TYPE_VIDEO;
    }
    switch (orderBy) {
      case "publishAt":
        orderBy = " order by " + additionalCondition + " publishAt " + cending;
        break;
      case "subscribedCount":
        orderBy = " order by " + additionalCondition + " buyerCount " + cending;
        break;
      case "price":
        orderBy =
            " order by "
                + additionalCondition
                + "  IF(c.`offerTo` > UNIX_TIMESTAMP()*1000 AND NOT(c.`discountedPrice` = NULL), c.`discountedPrice`, c.`price`) "
                + cending;
        break;
    }
    String select =
        "select c.id,\n"
            + "  c.accountId,\n"
            + "  c.name,\n"
            + "  c.shortIntro,"
            + "  c.introduceImage,\n"
            + "  c.publishAt,\n"
            + "  c.buyerCount,\n"
            + "  c.courseType,\n"
            + "  c.price,\n"
            + "  c.discountedPrice,\n"
            + "  c.offerTo,\n"
            + "  c.status,\n"
            + "  c.lectureCount,\n"
            + "  a.nickName,\n"
            + "  a.avatar,\n"
            + "  a.realPicture\n ";
    String fromWhere =
        "from course c,\n"
            + "  account a\n"
            + "where c.accountId = a.id\n"
            + "  and (c.status & (1 << 0)) = 0\n"
            + "  and (c.status & (1 << 2)) != 0\n"
            + "  and c.courseType = ?\n";
    Page<Record> coursePage =
        Db.paginateByFullSql(
            pageNumber,
            pageSize,
            "select count(*) count " + fromWhere,
            select + fromWhere + orderBy,
            type2);
    if (accountId != null) {
      List<Subscription> subscriptionList = subsSrv.findByAccountId(accountId);
      Set<Integer> subscribed =
          subscriptionList.stream()
              .filter(
                  s ->
                      s.getSubscribeType().equals("course")
                          && s.getStatus() == Subscription.STATUS_SUCCESS)
              .map(Subscription::getRefId)
              .collect(Collectors.toSet());
      coursePage
          .getList()
          .forEach((Record c) -> c.set("subscribed", subscribed.contains(c.getInt("id"))));
      {
        List<Record> list = coursePage.getList();
        Stream<Record> unsubscribed$ = list.stream().filter(x -> !x.getBoolean("subscribed"));
        Stream<Record> subscribed$ = list.stream().filter(x -> x.getBoolean("subscribed"));
        coursePage.setList(Stream.concat(unsubscribed$, subscribed$).collect(Collectors.toList()));
      }
    }
    Ret ret = Ret.ok("page", coursePage);
    return ret;
  }

  public Ret intro(int id, Integer accountId) {
    SqlPara sqlPara = Db.getSqlPara("course.intro", id);
    Record course = Db.findFirst(sqlPara);
    if (course == null) {
      return Ret.fail("msg", "not exists");
    }
    if (accountId != null) {
      course.set(
          "subscribed", SubscriptionService.me.subscribedCourse(course.getInt("id"), accountId));
    }
    return Ret.ok("course", course);
  }

  public Ret take(int id, Integer accountId) {
    Course course =
        dao.findByIdLoadColumns(
            id,
            "id, name, introduceImage, publishAt, courseType, lectureCount, buyerCount, status");
    if (course.isLock()) {
      return Ret.fail("msg", "课程被锁定");
    }
    if (course.isPublish()) {
      List<Article> articleList;
      Page<Article> articlePage = null;
      if (accountId != null && subsSrv.subscribedCourse(id, accountId)) {
        articlePage = articleService.list(id, "desc", 1, 10, accountId);
        articleList = articlePage.getList();
      } else {
        articleList = articleService.trialReading(id);
        articleList.forEach(a -> a.put("trialReading", true));
        if (accountId != null) {
          articleList.forEach(
              article -> {
                long visitAt = statisticsService.lastVisitAt(accountId, article.getId(), "article");
                article.put("visitAt", visitAt);
                article.put("readed", visitAt != 0);
              });
        }
      }
      return Ret.ok("course", course)
          .set("articleList", articleList)
          .set("articlePage", articlePage)
          .set("cending", "desc");
    } else return Ret.fail("msg", "课程未发布");
  }

  public List<Record> subscribedCourses(int accountId) {
    SqlPara sqlPara = Db.getSqlPara("course.subscribedCourses", accountId);
    List<Record> subscribedCourses =
        new ArrayList<>(
            Db.findByCache(
                cacheName, "subscribedCourses:" + accountId, sqlPara.getSql(), sqlPara.getPara()));
    if (subscribedCourses.size() > 0) {
      List<Record> idList =
          Db.find(
              Db.getSqlPara(
                  "course.allIdWithCourses",
                  Kv.by(
                      "courseIdList",
                      subscribedCourses.stream()
                          .map(x -> x.getInt("id"))
                          .collect(Collectors.toList()))));
      Map<Integer, List<Record>> courseIdToArticleIdList =
          idList.stream()
              .collect(Collectors.groupingBy((Record item) -> item.getInt(("courseId"))));
      subscribedCourses
          .parallelStream()
          .forEach(
              course -> {
                course.set("subscribed", true);
                if (courseIdToArticleIdList.get(course.getInt("id")) != null) {
                  AtomicInteger count = new AtomicInteger(0);

                  courseIdToArticleIdList
                      .get(course.getInt("id"))
                      .parallelStream()
                      .forEach(
                          id -> {
                            if (statisticsService.visitCount(
                                    id.getInt("id"),
                                    course.getInt("courseType") == Course.TYPE_COLUMN
                                        ? "article"
                                        : "paragraph")
                                > 0) {
                              count.addAndGet(1);
                            }
                          });
                  course.set("learnedCount", count.intValue());
                }
              });
    }
    return subscribedCourses;
  }

  public void clearHotCache(int courseType) {
    Jboot.getCache().remove(cacheName, "hot:" + courseType);
  }

  public void clearSubscribedCoursesCache(int accountId) {
    Jboot.getCache().remove(cacheName, "subscribedCourses:" + accountId);
  }

  public void clearCache() {
    Jboot.getCache().removeAll(cacheName);
  }
}
