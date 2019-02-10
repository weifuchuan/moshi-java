package com.moshi.srv.v1.statistics;

import com.jfinal.kit.Ret;
import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StatisticsService {
  private JbootRedis redis = Jboot.getRedis();

  private Set<String> filterVisitTypeSet = new HashSet<String>();

  public StatisticsService() {
    filterVisitTypeSet.add("article");
    filterVisitTypeSet.add("course");
    filterVisitTypeSet.add("video");
    filterVisitTypeSet.add("news");
    filterVisitTypeSet.add("account");
    filterVisitTypeSet.add("issue");
  }

  /**
   * @param accountId
   * @param refId
   * @param type article, course, video, news, account, issue
   */
  public Ret visit(int accountId, int refId, String type) {
    if (!filterVisitTypeSet.contains(type)) {
      return Ret.fail("msg", "Unsupport type: " + type);
    }
    redis.zadd("visit:" + type, 1, refId);
    redis.sadd("visit:" + type + ":" + refId, accountId);
    return Ret.ok();
  }


}
