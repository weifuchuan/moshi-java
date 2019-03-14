package com.moshi.avatar;

import cn.hutool.core.io.FileUtil;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Audio;
import com.moshi.common.plugin.Letture;
import io.jboot.Jboot;
import io.jboot.web.controller.annotation.RequestMapping;

import java.io.File;
import java.util.Date;
import java.util.function.Function;

@RequestMapping("/avatar")
public class AvatarController extends BaseController {
  private static long getNextId() {
    return Letture.sync().incr("file:nextId");
  }

  public void upload() {
    String uploadPath = "avatar/";
    Function<UploadFile, Boolean> filter = file -> file.getContentType().startsWith("image");
    UploadFile file = getFile("file", uploadPath);
    if (!filter.apply(file)) {
      renderJson(Ret.fail("msg", "格式错误：不是图片"));
      return;
    }
    String id = HashKit.md5(getNextId() + "");
    String newName = id + "." + FileKit.getFileExtension(file.getFile());
    try {
      FileUtil.move(file.getFile(), new File(file.getUploadPath() + "/" + newName), true);
      renderJson(Ret.ok("uri", "/static/media/avatar/" + newName));
    } catch (Exception exp) {
      renderJson(Ret.fail("msg", "保存失败"));
    }
  }
}
