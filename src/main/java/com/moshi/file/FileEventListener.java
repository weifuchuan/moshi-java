package com.moshi.file;

import cn.hutool.core.io.FileUtil;
import com.jfinal.kit.Kv;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.event.annotation.EventConfig;
import io.reactivex.Observable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@EventConfig(action = {"file/upload"})
public class FileEventListener implements JbootEventListener {
  public static final ConcurrentHashMap<String, Boolean> removableUploadedPath =
      new ConcurrentHashMap<>();

  @Override
  public void onEvent(JbootEvent event) {
    switch (event.getAction()) {
      case "file/upload":
        Kv kv = event.getData();
        removableUploadedPath.put(kv.getStr("uploadPath"), true);
//        Observable<Long> timer = Observable.timer(24, TimeUnit.HOURS);
//        timer.subscribe(
//            (v) -> {
//              if (removableUploadedPath.containsKey(kv.getStr("uploadPath"))
//                  && removableUploadedPath.get(kv.getStr("uploadPath"))) {
//                System.out.println("delete: " + kv.getStr("absPath"));
//                FileUtil.del(kv.getStr("absPath"));
//              }
//              removableUploadedPath.remove(kv.getStr("uploadPath"));
//            });
        break;
    }
  }
}
