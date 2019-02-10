package com.moshi.srv.v1;

import com.jfinal.config.Routes;
import com.moshi.srv.v1.course.CourseController;
import com.moshi.srv.v1.explore.ExploreController;
import com.moshi.srv.v1.statistics.StatisticsController;

public class SrvV1Routes extends Routes {
  @Override
  public void config() {
    add("/srv/v1/explore", ExploreController.class);
    add("/srv/v1/statistics", StatisticsController.class);
    add("/srv/v1/course", CourseController.class);
  }
}
