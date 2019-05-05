package com.moshi.srv.v1.explore

import com.jfinal.aop.Before
import com.jfinal.aop.Inject
import com.jfinal.kit.Ret
import com.jfinal.plugin.activerecord.Record
import com.moshi.common.controller.BaseController
import com.moshi.common.interceptor.TimingInterceptor
import com.moshi.common.model.Article
import com.moshi.common.model.Course
import com.moshi.srv.v1.article.ArticleService
import com.moshi.srv.v1.course.CourseService

import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.stream.Collectors
import java.util.stream.Stream

import com.moshi.common.model.Article.COURSE_ID_FOR_NEWS
import java.util.stream.Collector

open class ExploreController : BaseController() {

  @Inject
  internal var csrv: CourseService? = null
  @Inject
  internal var asrv: ArticleService? = null

  private val getSubscribedCourses = {
    if (isLogin) {
      csrv!!.subscribedCourses(loginAccountId!!)
    } else {
      ArrayList<Record>()
    }
  }

  private val getNewsList = fun(): List<Article> {
    try {
      return asrv!!.list(COURSE_ID_FOR_NEWS, "desc", 1, 6, null).getList()
    } catch (ex: Exception) {
      ex.printStackTrace()
      return emptyList<Article>()
    }
  }

  @Before(TimingInterceptor::class)
  fun index() {

    val retList = Stream.of<() -> List<*>>(
      getHotCourseList(Course.TYPE_COLUMN),
      getHotCourseList(Course.TYPE_VIDEO),
      getNewsList,
      getSubscribedCourses
    )
      .parallel()
      .map<List<*>> { cb ->
        try {
          return@map cb()
        } catch (e: Exception) {
          e.printStackTrace()
          return@map emptyList<Any>()
        }
      }
      .collect<List<List<*>>, Any>(Collectors.toList<Any>() as Collector<in List<*>, Any, List<List<*>>>?)

    val hotColumnList: List<*>
    val hotVideoList: List<*>
    val newsList: List<*>
    val subscribedCourseList: List<*>

    hotColumnList = retList[0]
    hotVideoList = retList[1]
    newsList = retList[2]
    subscribedCourseList = retList[3]

    val ret = Ret.ok("hotColumnList", hotColumnList)
      .set("hotVideoList", hotVideoList)
      .set("newsList", newsList)
      .set("subscribedCourseList", subscribedCourseList)

    renderJsonThreadSafe(ret)
  }

  @Before(TimingInterceptor::class)
  @Throws(Exception::class)
  fun newsList() {
    val list = getNewsList()
    renderJsonThreadSafe(list)
  }

  @Before(TimingInterceptor::class)
  @Throws(Exception::class)
  fun subscribedCourseList() {
    val list = getSubscribedCourses()
    renderJsonThreadSafe(list)
  }

  @Before(TimingInterceptor::class)
  fun hotCourseList() {
    val retList = Stream.of<() -> List<*>>(
      getHotCourseList(Course.TYPE_COLUMN),
      getHotCourseList(Course.TYPE_VIDEO)
    )
      .parallel()
      .map { cb ->
        try {
          return@map cb()
        } catch (e: Exception) {
          e.printStackTrace()
          return@map emptyList<Any>()
        }
      }
      .collect<List<List<*>>, Any>(Collectors.toList<Any>() as Collector<in List<Any?>, Any, List<List<*>>>?)

    val hotColumnList: List<*>
    val hotVideoList: List<*>

    hotColumnList = retList[0]
    hotVideoList = retList[1]

    val ret = Ret.ok("hotColumnList", hotColumnList)
      .set("hotVideoList", hotVideoList)

    renderJsonThreadSafe(ret)
  }

  private fun getHotCourseList(typeColumn: Int): () -> List<Record> {
    return { csrv!!.hot(3, typeColumn, loginAccountId) }
  }
}
