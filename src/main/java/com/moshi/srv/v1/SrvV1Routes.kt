package com.moshi.srv.v1

import com.jfinal.config.Routes
import com.moshi.srv.v1.article.ArticleController
import com.moshi.srv.v1.course.CourseController
import com.moshi.srv.v1.explore.ExploreController
import com.moshi.srv.v1.issue.IssueController
import com.moshi.srv.v1.news.NewsController
import com.moshi.srv.v1.statistics.StatisticsController
import com.moshi.srv.v1.subscribe.SubscribeController

class SrvV1Routes : Routes() {
  override fun config() {
    add("/srv/v1/explore", ExploreController::class.java)
    add("/srv/v1/statistics", StatisticsController::class.java)
    add("/srv/v1/course", CourseController::class.java)
    add("/srv/v1/subscribe", SubscribeController::class.java)
    add("/srv/v1/article", ArticleController::class.java)
    add("/srv/v1/issue", IssueController::class.java)
    add("/srv/v1/news", NewsController::class.java)
  }
}
