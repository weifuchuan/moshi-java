package com.moshi.srv.v1.course;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.moshi.common.model.Course;
import io.jboot.Jboot;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.support.redis.JbootRedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseService {
  public static final CourseService me = new CourseService();

  private static final String courseCacheName = "course";
  private JbootRedis redis = Jboot.getRedis();

  public List<Record> hot(int maxCount, int courseType) {
    SqlPara sqlPara = Db.getSqlPara("course.allSimpleByType", courseType);
    List<Record> list =
        Db.findByCache(courseCacheName, "hot:" + courseType, sqlPara.getSql(), sqlPara.getPara());
    for (Record item : list) {
      int id = item.getInt("id");
      Double score = redis.zscore("visit:course", id);
      score = score == null ? 0 : score;
      item.set("score", score + 5 * item.getInt("buyerCount")+0.5*item.getInt("lectureCount"));
    }
    list.sort(
        (a, b) -> {
          if (a.getDouble("score") > b.getDouble("score")) {
            return -1;
          } else if (a.getDouble("score") < b.getDouble("score")) {
            return 1;
          } else {
            return 0;
          }
        });
    if (list.size() < maxCount) {
      maxCount = list.size();
    }
    return list.subList(0, maxCount);
  }

  public void clearHotCache(int courseType) {
    Jboot.getCache().remove(courseCacheName, "hot:" + courseType);
  }
}
