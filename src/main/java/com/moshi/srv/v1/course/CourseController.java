package com.moshi.srv.v1.course;

import cn.hutool.core.lang.Range;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Article;
import com.moshi.common.model.Course;
import com.moshi.srv.v1.article.ArticleService;
import io.jboot.Jboot;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CourseController extends BaseController {
  @Inject CourseService srv;
  @Inject ArticleService articleSrv;

  private static final Set<String> typeSet =
      Stream.of("column", "video").collect(Collectors.toSet());
  private static final Set<String> orderBySet =
      Stream.of("publishAt", "subscribedCount", "price").collect(Collectors.toSet());
  // ascending 升序 descending 降序
  private static final Set<String> cendingSet =
      Stream.of("asc", "desc").collect(Collectors.toSet());

  public void list(String type, String orderBy, String cending, int pageNumber, int pageSize) {
    if (!typeSet.contains(type)) {
      renderJson(Ret.fail("msg", "Unsupported type"));
    }
    if (!orderBySet.contains(orderBy)) {
      renderJson(Ret.fail("msg", "Unsupported orderBy"));
    }
    if (!cendingSet.contains(cending)) {
      renderJson(Ret.fail("msg", "Unsupported cending"));
    }
    Ret ret = srv.list(type, orderBy, cending, pageNumber, pageSize, getLoginAccountId());
    Page<Record> page = (Page<Record>) ret.get("page");
    page.setList(
        page.getList().stream()
            .filter(x -> x.getInt("id") != Article.COURSE_ID_FOR_NEWS)
            .collect(Collectors.toList()));
    renderJson(ret);
  }

  public void index(int id) {
    if (id == Article.COURSE_ID_FOR_NEWS) renderError(404);
    Ret ret = srv.take(id, getLoginAccountId());
    renderJson(ret);
  }

  public void intro(int id) {
    if (id == Article.COURSE_ID_FOR_NEWS) renderError(404);
    Ret ret = srv.intro(id, getLoginAccountId());
    renderJson(ret);
  }

  public void clear() {
    srv.clearCache();
    renderText("");
  }
}
