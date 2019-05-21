package com.moshi.srv.v1.course

import cn.hutool.core.lang.Range
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.jfinal.aop.Inject
import com.jfinal.kit.Ret
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.activerecord.Page
import com.jfinal.plugin.activerecord.Record
import com.moshi.common.controller.BaseController
import com.moshi.common.model.Article
import com.moshi.common.model.Course
import com.moshi.srv.v1.article.ArticleService

import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream

open class CourseController : BaseController() {
  @Inject
  lateinit var srv: CourseService
  @Inject
  lateinit var articleSrv: ArticleService

  fun list(type: String, orderBy: String, cending: String, pageNumber: Int, pageSize: Int) {
    if (!typeSet.contains(type)) {
      renderJson(Ret.fail("msg", "Unsupported type"))
    }
    if (!orderBySet.contains(orderBy)) {
      renderJson(Ret.fail("msg", "Unsupported orderBy"))
    }
    if (!cendingSet.contains(cending)) {
      renderJson(Ret.fail("msg", "Unsupported cending"))
    }
    val ret = srv.list(type, orderBy, cending, pageNumber, pageSize, loginAccountId)
    val page = ret["page"] as Page<Record>
    page.list = page.list.stream()
      .filter { x -> x.getInt("id") != Article.COURSE_ID_FOR_NEWS }
      .collect<List<Record>, Any>(Collectors.toList<Any>() as Collector<in Record, Any, List<Record>>?)
    renderJson(ret)
  }

  fun index(id: Int) {
    if (id == Article.COURSE_ID_FOR_NEWS) renderError(404)
    val ret = srv.take(id, loginAccountId)
    renderJson(ret)
  }

  fun search(q: String) {
    val list = srv.search(q, loginAccountId)
    renderJson(list)
  }

  fun simpleCourseListByIdList() {
    val rawData = rawData
    val idList = JSON.parseArray(rawData)
    val list = srv.simpleCourseListByIdList(idList.map { it as Int }, loginAccountId)
    renderJson(list)
  }

  fun intro(id: Int) {
    if (id == Article.COURSE_ID_FOR_NEWS) renderError(404)
    val ret = srv.intro(id, loginAccountId)
    renderJson(ret)
  }

  fun allCourseType() {
    val list = srv.allCourseType()
    renderJson(list)
  }

  fun clear() {
    srv.clearCache()
    renderText("")
  }

  companion object {

    private val typeSet = Stream.of("column", "video")
      .collect<Set<String>, Any>(Collectors.toSet<Any>() as Collector<in String, Any, Set<String>>?)
    private val orderBySet =
      Stream.of("publishAt", "subscribedCount", "price")
        .collect<Set<String>, Any>(Collectors.toSet<Any>() as Collector<in String, Any, Set<String>>?)
    // ascending 升序 descending 降序
    private val cendingSet = Stream.of("asc", "desc")
      .collect<Set<String>, Any>(Collectors.toSet<Any>() as Collector<in String, Any, Set<String>>?)
  }
}
