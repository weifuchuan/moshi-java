package com.moshi.easyrec;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jfinal.kit.Kv;
import io.jboot.Jboot;

import java.util.Map;

public class EasyrecService {
  private final EasyrecConfig config = Jboot.config(EasyrecConfig.class, "easyrec");

  // https://sourceforge.net/p/easyrec/wiki/ActionAPI/#view
  public void view(
      String sessionid, String itemid, String itemdescription, String itemurl, Kv optional) {
    HttpResponse response =
        get(
            "/api/1.1/view",
            Kv.by("sessionid", sessionid)
                .set("itemid", itemid)
                .set("itemdescription", itemdescription)
                .set("itemurl", itemurl)
                .set(optional));
  }

  // https://sourceforge.net/p/easyrec/wiki/ActionAPI/#buy
  public void buy(
      String sessionid, String itemid, String itemdescription, String itemurl, Kv optional) {
    HttpResponse response =
        get(
            "/api/1.1/buy",
            Kv.by("sessionid", sessionid)
                .set("itemid", itemid)
                .set("itemdescription", itemdescription)
                .set("itemurl", itemurl)
                .set(optional));
  }

  // https://sourceforge.net/p/easyrec/wiki/ActionAPI/#rate
  public void rate(
    String sessionid, String itemid, String itemdescription, String itemurl, int ratingvalue, Kv optional) {
    HttpResponse response =
      get(
        "/api/1.1/rate",
        Kv.by("sessionid", sessionid)
          .set("itemid", itemid)
          .set("itemdescription", itemdescription)
          .set("itemurl", itemurl)
          .set("ratingvalue", ratingvalue)
          .set(optional));
  }

  // https://sourceforge.net/p/easyrec/wiki/ActionAPI/#sendaction
  // 虽然view，buy和rate调用是针对典型的电子商务商店应用程序量身定制的，但easyrec允许根据特定用例的要求定义任意新的操作类型。您可以使用通用sendaction API调用将任何类型的操作发送到easyrec。使用actiontype参数指定操作。注意：必须先使用Admin GUI创建操作类型，然后才能将其接受为有效的API调用。
  public void sendaction(
    String sessionid, String itemid, String itemdescription, String itemurl,String actiontype, Kv optional) {
    HttpResponse response =
      get(
        "/api/1.1/sendaction",
        Kv.by("sessionid", sessionid)
          .set("itemid", itemid)
          .set("itemdescription", itemdescription)
          .set("itemurl", itemurl)
          .set("actiontype", actiontype)
          .set(optional));
  }

  // https://sourceforge.net/p/easyrec/wiki/ActionAPI/#track
  // 跟踪操作可用于跟踪建议的点击次数。当用户点击推荐的项目时应该调用它。这样，如果向用户显示的推荐被欣赏，则可以跟踪它。
  public void track(
    String sessionid, String itemtoid, String rectype,Kv optional) {
    HttpResponse response =
      get(
        "/api/1.1/json/track",
        Kv.by("sessionid", sessionid)
          .set("itemtoid", itemtoid)
          .set("rectype", rectype)
          .set(optional));
  }

  private HttpResponse get(String uri, Kv params) {
    return HttpRequest.get(config.getServerUrl().trim() + uri.trim())
        .form(params.set("apikey", config.getApikey()).set("tenantid", config.getTenantid()))
        .execute();
  }

  private HttpResponse post(String uri, Kv params) {
    return HttpRequest.post(config.getServerUrl().trim() + uri.trim())
        .form(params.set("apikey", config.getApikey()).set("tenantid", config.getTenantid()))
        .execute();
  }
}
