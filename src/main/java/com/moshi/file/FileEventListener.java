package com.moshi.file;

import com.jfinal.kit.Kv;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.event.annotation.EventConfig;
import org.cliffc.high_scale_lib.NonBlockingHashMap;


@EventConfig(action = {"file/upload"})
public class FileEventListener implements JbootEventListener {
  public static final NonBlockingHashMap<String, Boolean> removableUploadedPath =
    new NonBlockingHashMap<>();

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
