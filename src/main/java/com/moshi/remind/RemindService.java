package com.moshi.remind;

import cn.hutool.core.lang.Assert;
import com.jfinal.plugin.activerecord.Db;
import com.moshi.common.model.Remind;
import io.jboot.Jboot;
import io.jboot.components.mq.Jbootmq;
import io.jboot.support.redis.JbootRedis;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RemindService {
  public static final RemindService me = new RemindService();
  private final JbootRedis redis = Jboot.getRedis();

  public void send(Remind remind, int accountId) {
    redis.rpush("remind:" + accountId, remind);
  }

  public List<Remind> take(int accountId, long rstart, long rend) {
    Assert.isTrue(rend <= rstart);
    String key = "remind:" + accountId;
    if (len(accountId) == 0) {
      return Collections.emptyList();
    }
    long len = redis.llen(key) - 1;
    rstart = len < rstart ? len : rstart;
    rend = rend < 0 ? 0 : rend;
    List<Remind> reminds = redis.lrange(key, rend, rstart);
    return reminds;
  }

  public long len(int accountId) {
    return redis.llen("remind:" + accountId);
  }

  public static void main(String[] args) {

    Remind remind = new Remind();
    remind.setAccountId(1);
    remind.setCreateAt(new Date().getTime());
    remind.setRefId(0);
    remind.setType(0);
    remind.setStatus(0);
    RemindService.me.send(remind, 1);
    List<Remind> list = RemindService.me.take(1, 10, 0);
    System.out.println(list);
  }
}
