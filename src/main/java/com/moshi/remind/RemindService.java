package com.moshi.remind;

import cn.hutool.core.lang.Assert;
import com.jfinal.plugin.activerecord.Db;
import com.moshi.common.model.Remind;
import com.moshi.common.plugin.Letture;
import io.jboot.Jboot;
import io.jboot.components.mq.Jbootmq;
import io.jboot.support.redis.JbootRedis;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RemindService {
  public static final RemindService me = new RemindService();

  public void send(Remind remind, int accountId) {
    Letture.async().rpush("remind:" + accountId, remind);
  }

  public List<Remind> take(int accountId, long rstart, long rend) {
    RedisCommands<String, Object> sync = Letture.sync();
    Assert.isTrue(rend <= rstart);
    String key = "remind:" + accountId;
    if (len(accountId) == 0) {
      return Collections.emptyList();
    }
    long len = sync.llen(key) - 1;
    rstart = len < rstart ? len : rstart;
    rend = rend < 0 ? 0 : rend;
    List<Remind> reminds = (List<Remind>) (Object) sync.lrange(key, rend, rstart);
    return reminds;
  }

  public long len(int accountId) {
    return Letture.sync().llen("remind:" + accountId);
  }

}
