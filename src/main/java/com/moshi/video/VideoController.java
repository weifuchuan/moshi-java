package com.moshi.video;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.IsTeacherOrManagerInterceptor;
import com.moshi.common.interceptor.UnlockInterceptor;
import com.moshi.common.model.Video;
import com.moshi.common.plugin.Letture;
import io.jboot.Jboot;
import io.jboot.web.controller.annotation.RequestMapping;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@RequestMapping("/video")
public class VideoController extends BaseController {
  private static long getNextId() {
    return Letture.sync().incr("file:nextId");
  }

  public void index() {
    String id = getPara();
    Video video = Video.dao.findById(id);
    if (video != null) {
      renderJson(Ret.ok("recorder", video.getRecorder()).set("resource", video.getResource()));
    } else {
      renderError(404);
    }
  }

  @Before({UnlockInterceptor.class, IsTeacherOrManagerInterceptor.class})
  public void upload() {
    String uploadPath = "video/";
    Function<UploadFile, Boolean> filter =
        file -> file.getContentType().startsWith("video");
    UploadFile file = getFile("file", uploadPath);
    String recorder = getPara("recorder");
    String name = getPara("name");
    if (filter.apply(file)) {
      String id = HashKit.md5(getNextId() + "");
      String newName = id + "." + FileKit.getFileExtension(file.getFile());
      FileUtil.move(file.getFile(), new File(file.getUploadPath() + "/" + newName), true);
      Video video = new Video();
      video.setAccountId(getLoginAccountId());
      video.setResource("/static/media/video/" + newName);
      video.setStatus(0);
      video.setRecorder(recorder);
      video.setName(name);
      video.setUploadAt(new Date().getTime());
      if (video.save()) {
        renderJson(Ret.ok("video", video));
      } else {
        FileUtil.del(file.getUploadPath() + "/" + newName);
        renderJson(Ret.fail("msg", "保存失败"));
      }
    } else {
      renderJson(Ret.fail("msg", "格式错误：不是视频"));
    }
  }

  @Before({IsTeacherOrManagerInterceptor.class})
  public void myUploaded() {
    List<Video> videos =
        Video.dao.find("select * from video where accountId = ?", getLoginAccountId());
    renderJson(videos);
  }

  @Before({IsTeacherOrManagerInterceptor.class})
  public void delete() {
    int id = getParaToInt("id");
    Video video = Video.dao.findById(id);
    if (video.getAccountId() == getLoginAccountId()) {
      if (video.delete()) renderJson(Ret.ok());
      else renderJson(Ret.fail("msg", "删除失败"));
    } else renderJson(Ret.fail("msg", "你不是此视频的上传者"));
  }
}
