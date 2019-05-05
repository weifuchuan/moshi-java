package com.moshi.srv.v1.course

import com.jfinal.kit.Kv
import com.jfinal.kit.Ret
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.activerecord.Page
import com.jfinal.plugin.activerecord.Record
import com.jfinal.plugin.activerecord.SqlPara
import com.jfinal.plugin.ehcache.CacheKit
import com.moshi.common.model.Article
import com.moshi.common.model.Course
import com.moshi.common.model.Subscription
import com.moshi.common.plugin.Letture
import com.moshi.srv.v1.article.ArticleService
import com.moshi.srv.v1.statistics.StatisticsService
import com.moshi.subscription.SubscriptionService

import java.util.*

import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.Comparator

open class CourseService {

  private val dao = Course().dao()

  private val subsSrv = SubscriptionService()
  private val articleService = ArticleService()
  private val statisticsService = StatisticsService()

  @Throws(ExecutionException::class, InterruptedException::class)
  fun hot(maxCount: Int, courseType: Int): List<Record> {
    var maxCount = maxCount
    val sqlPara = Db.getSqlPara("course.allSimpleByType", courseType)
    val list = ArrayList(
      Db.findByCache(cacheName, "hot:$courseType", sqlPara.sql, *sqlPara.para)
    )
    val result = Letture.pipe { redis -> list.forEach { item -> redis.zscore("visit:course", item.getInt("id")) } }
    for (i in list.indices) {
      val item = list[i]
      var score: Double? = (result[i] ?: 0.0) as Double
      score = score ?: 0.0
      item.set("score", score!! + (5 * item.getInt("buyerCount")!!).toDouble() + 0.5 * item.getInt("lectureCount")!!)
    }
    list.sortWith(Comparator { a, b -> compareScore(a, b) })
    if (list.size < maxCount) {
      maxCount = list.size
    }
    return ArrayList(list.subList(0, maxCount))
  }

  @Throws(ExecutionException::class, InterruptedException::class)
  fun hot(maxCount: Int, courseType: Int, accountId: Int?): List<Record> {
    var maxCount = maxCount
    if (accountId == null) return hot(maxCount, courseType)
    val subscriptionList = subsSrv.findByAccountId(accountId)
    val sqlPara = Db.getSqlPara("course.allSimpleByType", courseType)
    val list = ArrayList(
      Db.findByCache(cacheName, "hot:$courseType", sqlPara.sql, *sqlPara.para)
    )
    val subscribed = subscriptionList.stream()
      .filter { s -> s.subscribeType == "course" && s.status == Subscription.STATUS_SUCCESS }
      .map<Int> { it.getRefId() }
      .collect<Set<Int>, Any>(Collectors.toSet<Any>() as Collector<in Int, Any, Set<Int>>?)
    val result = Letture.pipe { redis -> list.forEach { item -> redis.zscore("visit:course", item.getInt("id")) } }
    for (i in list.indices) {
      val item = list[i]
      val id = item.getInt("id")!!
      var score: Double? = (result[i] ?: 0.0) as Double
      score = score ?: 0.0
      if (subscribed.contains(id)) {
        item.set(
          "score", -(score!! + (5 * item.getInt("buyerCount")!!).toDouble() + 0.5 * item.getInt("lectureCount")!!)
        )
      } else
        item.set(
          "score", score!! + (5 * item.getInt("buyerCount")!!).toDouble() + 0.5 * item.getInt("lectureCount")!!
        )
      item.set("subscribed", subscribed.contains(item.getInt("id")))
    }
    list.sortWith(Comparator { a, b -> compareScore(a, b) })
    if (list.size < maxCount) {
      maxCount = list.size
    }
    return ArrayList(list.subList(0, maxCount))
  }

  // TODO: cache result by better way
  fun list(
    type: String,
    orderBy: String,
    cending: String,
    pageNumber: Int,
    pageSize: Int,
    accountId: Int?
  ): Ret {
    var orderBy = orderBy
    val additionalCondition = if (accountId == null)
      ""
    else
      """
        IF(c.id in (
            select refId
            from subscription s
            where s.accountId = $accountId
              and subscribeType = '${Subscription.SUB_TYPE_COURSE}'
          ), 1, 0
        ) asc,
      """
    val type2 = if (type == "column") {
      Course.TYPE_COLUMN
    } else {
      Course.TYPE_VIDEO
    }
    when (orderBy) {
      "publishAt" -> orderBy = " order by $additionalCondition publishAt $cending"
      "subscribedCount" -> orderBy = " order by $additionalCondition buyerCount $cending"
      "price" -> orderBy = (" order by "
          + additionalCondition
          + "  IF(c.`offerTo` > UNIX_TIMESTAMP()*1000 AND NOT(c.`discountedPrice` = NULL), c.`discountedPrice`, c.`price`) "
          + cending)
    }
    val select = ("select c.id,\n"
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
        + "  a.realPicture\n ")
    val fromWhere = ("from course c,\n"
        + "  account a\n"
        + "where c.accountId = a.id\n"
        + "  and (c.status & (1 << 0)) = 0\n"
        + "  and (c.status & (1 << 2)) != 0\n"
        + "  and c.courseType = ?\n")
    val coursePage = Db.paginateByFullSql(
      pageNumber,
      pageSize,
      "select count(*) count $fromWhere",
      select + fromWhere + orderBy,
      type2
    )
    if (accountId != null) {
      val subscriptionList = subsSrv.findByAccountId(accountId)
      val subscribed = subscriptionList.stream()
        .filter { s -> s.subscribeType == "course" && s.status == Subscription.STATUS_SUCCESS }
        .map<Int> { it.getRefId() }
        .collect<Set<Int>, Any>(Collectors.toSet<Any>() as Collector<in Int, Any, Set<Int>>?)
      coursePage
        .list
        .forEach { c: Record -> c.set("subscribed", subscribed.contains(c.getInt("id"))) }
      run {
        val list = coursePage.list
        val `unsubscribed$` = list.stream().filter { x -> !x.getBoolean("subscribed") }
        val `subscribed$` = list.stream().filter { x -> x.getBoolean("subscribed") }
        coursePage.setList(
          Stream.concat(
            `unsubscribed$`,
            `subscribed$`
          ).collect<List<Record>, Any>(Collectors.toList<Any>() as Collector<in Record, Any, List<Record>>?)
        )
      }
    }
    return Ret.ok("page", coursePage)
  }

  fun intro(id: Int, accountId: Int?): Ret {
    val sqlPara = Db.getSqlPara("course.intro", id)
    val course = Db.findFirst(sqlPara) ?: return Ret.fail("msg", "not exists")
    if (accountId != null) {
      course.set(
        "subscribed", SubscriptionService.me.subscribedCourse(course.getInt("id")!!, accountId)
      )
    }
    return Ret.ok("course", course)
  }

  fun take(id: Int, accountId: Int?): Ret {
    val course = dao.findByIdLoadColumns(
      id,
      "id, name, introduceImage, publishAt, courseType, lectureCount, buyerCount, status"
    )
    if (course.isLock) {
      return Ret.fail("msg", "课程被锁定")
    }
    if (course.isPublish) {
      val articleList: List<Article>
      var articlePage: Page<Article>? = null
      if (accountId != null && subsSrv.subscribedCourse(id, accountId)) {
        articlePage = articleService.list(id, "desc", 1, 10, accountId)
        articleList = articlePage!!.list
      } else {
        articleList = articleService.trialReading(id)
        articleList.forEach { a -> a.put("trialReading", true) }
        if (accountId != null) {
          articleList.forEach { article ->
            val visitAt = statisticsService.lastVisitAt(accountId, article.id!!, "article")
            article.put("visitAt", visitAt)
            article.put("readed", visitAt != 0L)
          }
        }
      }
      return Ret.ok("course", course)
        .set("articleList", articleList)
        .set("articlePage", articlePage)
        .set("cending", "desc")
    } else
      return Ret.fail("msg", "课程未发布")
  }

  fun subscribedCourses(accountId: Int): List<Record> {
    val sqlPara = Db.getSqlPara("course.subscribedCourses", accountId)
    val subscribedCourses = ArrayList(
      Db.findByCache(
        cacheName, "subscribedCourses:$accountId", sqlPara.sql, *sqlPara.para
      )
    )
    if (subscribedCourses.size > 0) {
      val idList = Db.find(
        Db.getSqlPara(
          "course.allIdWithCourses",
          Kv.by(
            "courseIdList",
            subscribedCourses.stream()
              .map { x -> x.getInt("id") }
              .collect<List<Int>, Any>(Collectors.toList<Any>() as Collector<in Int, Any, List<Int>>?))))
      val courseIdToArticleIdList = idList.stream()
        .collect<Map<Int, List<Record>>, Any>(Collectors.groupingBy<Record, Int> { item: Record -> item.getInt("courseId") } as Collector<in Record, Any, Map<Int, List<Record>>>)
      subscribedCourses
        .parallelStream()
        .forEach { course ->
          course.set("subscribed", true)
          if (courseIdToArticleIdList[course.getInt("id")] != null) {
            val count = AtomicInteger(0)

            courseIdToArticleIdList[course.getInt("id")]!!
              .parallelStream()
              .forEach { id ->
                if (statisticsService.visitCount(
                    id.getInt("id")!!,
                    if (course.getInt("courseType") == Course.TYPE_COLUMN)
                      "article"
                    else
                      "paragraph"
                  ) > 0
                ) {
                  count.addAndGet(1)
                }
              }
            course.set("learnedCount", count.toInt())
          }
        }
    }
    return subscribedCourses
  }

  fun simpleCourseListByIdList(idList: List<Int>): List<Course> {
    if (idList.isEmpty()) {
      return Collections.emptyList()
    }

    val list = dao.find("""
      select
         c.id,
         c.accountId,
         c.name,
         c.shortIntro,
         c.introduceImage,
         c.publishAt,
         c.buyerCount,
         c.courseType,
         c.price,
         c.discountedPrice,
         c.offerTo,
         c.status,
         c.lectureCount
      from course c
      where ${idList.map { id -> " id=$id " }.reduce { prev, curr -> " $prev or $curr " }}
    """.trimIndent()
    )

    val id2Course = list.groupBy { it.id }

    return idList.map { id2Course[it]!![0]!! }
  }

  fun clearHotCache(courseType: Int) {
    CacheKit.remove(cacheName, "hot:$courseType")
  }

  fun clearSubscribedCoursesCache(accountId: Int) {
    CacheKit.remove(cacheName, "subscribedCourses:$accountId")
  }

  fun clearCache() {
    CacheKit.removeAll(cacheName)
  }

  companion object {
    val me = CourseService()

    val cacheName = "course"

    private fun compareScore(a: Record, b: Record): Int {
      return if (a.getDouble("score") > b.getDouble("score")) {
        -1
      } else if (a.getDouble("score") < b.getDouble("score")) {
        1
      } else {
        0
      }
    }
  }
}

fun main() {
  val idList = arrayOf(1)
  println("""
      select
      from course
      where ${idList.map { id -> " id=$id " }.reduce { prev, curr -> " $prev or $curr " }}
    """
  )
}