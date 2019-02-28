package com.moshi.srv.v1;

import com.jfinal.config.Routes;
import com.moshi.srv.v1.article.ArticleController;
import com.moshi.srv.v1.course.CourseController;
import com.moshi.srv.v1.explore.ExploreController;
import com.moshi.srv.v1.im.ImController;
import com.moshi.srv.v1.issue.IssueController;
import com.moshi.srv.v1.news.NewsController;
import com.moshi.srv.v1.statistics.StatisticsController;
import com.moshi.srv.v1.subscribe.SubscribeController;

public class SrvV1Routes extends Routes {
  @Override
  public void config() {
    add("/srv/v1/explore", ExploreController.class);
    add("/srv/v1/statistics", StatisticsController.class);
    add("/srv/v1/course", CourseController.class);
    add("/srv/v1/subscribe", SubscribeController.class);
    add("/srv/v1/article", ArticleController.class);
    add("/srv/v1/issue", IssueController.class);
    add("/srv/v1/news", NewsController.class);
    add("/srv/v1/im", ImController.class);
  }
}
