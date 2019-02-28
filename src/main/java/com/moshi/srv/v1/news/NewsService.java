package com.moshi.srv.v1.news;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedis;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*

interface Page<Model = any> {
  totalRow: number;
  pageNumber: number;
  firstPage: boolean;
  lastPage: boolean;
  totalPage: number;
  pageSize: number;
  list: Model[];
}

*/

@Deprecated
public class NewsService {
  private static final String NEWS_LIST = "newses";
  private static final String NEWS = "news:";

  private JbootRedis redis = Jboot.getRedis();

  public Ret list(long pageNumber, long pageSize) {
    long len = redis.llen(NEWS_LIST);
    long offset = (pageNumber - 1) * pageSize;
    long totalPage = (long) Math.floor(len / pageSize) + (len % pageSize == 0 ? 0 : 1);
    List<Integer> idList = Collections.emptyList();
    List<Map> newses = Collections.emptyList();
    if (offset < len) {
      pageSize = pageSize > len - offset ? len - offset : pageSize;
      idList = redis.lrange(NEWS_LIST, offset, offset + pageSize - 1);
      newses =
          idList
              .parallelStream()
              .map(id -> redis.hgetAll(NEWS + id))
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
    }

    Kv page =
        Kv.by("totalRow", len)
            .set("pageNumber", pageNumber)
            .set("firstPage", pageNumber == 1)
            .set("lastPage", pageNumber == totalPage)
            .set("totalPage", totalPage)
            .set("pageSize", newses.size())
            .set("list", newses);
    return Ret.ok("page",page);
  }
}
