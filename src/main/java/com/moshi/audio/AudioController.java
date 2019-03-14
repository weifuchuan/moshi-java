package com.moshi.audio;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.HashUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.IsTeacherOrManagerInterceptor;
import com.moshi.common.interceptor.UnlockInterceptor;
import com.moshi.common.model.Audio;
import com.moshi.common.plugin.Letture;
import io.jboot.Jboot;
import io.jboot.web.controller.annotation.RequestMapping;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@RequestMapping("/audio")
public class AudioController extends BaseController {
  private static long getNextId() {
    return Letture.sync().incr("file:nextId");
  }

  public void index(int id) {
    Audio audio = Audio.dao.findById(id);
    if (audio != null) {
      renderJson(Ret.ok("recorder", audio.getRecorder()).set("resource", audio.getResource()));
    } else {
      renderError(404);
    }
  }

  @Before({UnlockInterceptor.class, IsTeacherOrManagerInterceptor.class})
  public void upload() {
    String uploadPath = "audio/";
    Function<UploadFile, Boolean> filter =
        file -> file.getContentType().startsWith("audio");
    UploadFile file = getFile("file", uploadPath);
    String recorder = getPara("recorder");
    String name = getPara("name");
    if (filter.apply(file)) {
      String id = HashKit.md5(getNextId() + "");
      String newName = id + "." + FileKit.getFileExtension(file.getFile());
      FileUtil.move(file.getFile(), new File(file.getUploadPath() + "/" + newName), true);
      Audio audio = new Audio();
      audio.setAccountId(getLoginAccountId());
      audio.setResource("/static/media/audio/" + newName);
      audio.setStatus(0);
      audio.setRecorder(recorder);
      audio.setName(name);
      audio.setUploadAt(new Date().getTime());
      if (audio.save()) {
        renderJson(Ret.ok("audio", audio));
      } else {
        FileUtil.del(file.getUploadPath() + "/" + newName);
        renderJson(Ret.fail("msg", "保存失败"));
      }
    } else {
      renderJson(Ret.fail("msg", "格式错误：不是音频"));
    }
  }

  @Before({IsTeacherOrManagerInterceptor.class})
  public void myUploaded() {
    List<Audio> audios =
        Audio.dao.find("select * from audio where accountId = ?", getLoginAccountId());
    renderJson(audios);
  }

  @Before({IsTeacherOrManagerInterceptor.class})
  public void delete() {
    int id = getParaToInt("id");
    Audio audio = Audio.dao.findById(id);
    if (audio.getAccountId() == getLoginAccountId()) {
      if (audio.delete()) renderJson(Ret.ok());
      else renderJson(Ret.fail("msg", "删除失败"));
    } else renderJson(Ret.fail("msg", "你不是此音频的上传者"));
  }
}
