package com.moshi.file;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import com.cloudinary.Cloudinary;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.UnlockInterceptor;
import com.moshi.common.plugin.Letture;
import io.jboot.Jboot;
import io.jboot.web.controller.annotation.RequestMapping;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequestMapping("/file")
@Before(UnlockInterceptor.class)
public class FileController extends BaseController {
  private static final Supplier<Map<String, Object>> configGetter =
      () -> {
        String raw = FileUtil.readUtf8String("cloudinary.yml");
        Yaml yaml = new Yaml();
        Map<String, Map<String, Object>> config = yaml.load(raw);
        if (Jboot.isDevMode()) {
          return config.get("development");
        } else {
          return config.get("production");
        }
      };
  private static final Cloudinary cloudinary = new Cloudinary(FileController.configGetter.get());

  private static long getNextId() {
    return Letture.sync().incr("file:nextId");
  }

  @Clear
  @Before({POST.class})
  public void upload() {
    String uploadPath = "temp/" + getNextId();
    List<UploadFile> files = getFiles(uploadPath);
    Function<UploadFile, Boolean> filter =
        file -> {
          switch (FileTypeUtil.getType(file.getFile())) {
            case "jpg":
              return true;
            case "png":
              return true;
            case "gif":
              return true;
            case "tif":
              return true;
            case "bmp":
              return true;
            case "rmvb":
              return true;
            case "flv":
              return true;
            case "mp4":
              return true;
            case "mpg":
              return true;
            case "wav":
              return true;
            case "wmv":
              return true;
            case "avi":
              return true;
            case "docx":
              return true;
            case "gz":
              return true;
            case "log":
              return true;
            case "pdf":
              return true;
            case "pptx":
              return true;
            case "txt":
              return true;
            case "xlsx":
              return true;
            case "doc":
              return true;
            case "xls":
              return true;
            case "zip":
              return true;
          }
          return false;
        };
    List<UploadFile> validFiles = new ArrayList<>();
    Kv kv = Kv.create();
    for (UploadFile file : files) {
      if (filter.apply(file)) {
        validFiles.add(file);
        kv.set(file.getFileName(), "/static/media/" + uploadPath + "/" + file.getFileName());
      } else {
        try {
          file.getFile().delete();
        } catch (Exception e) {
        }
      }
    }
    if (validFiles.size() > 0) {
      Jboot.sendEvent(
          "file/upload",
          Kv.by("uploadPath", uploadPath)
              .set("absPath", validFiles.get(0).getFile().getAbsolutePath() + "/.."));
    }
    renderJson(kv);
  }

}
