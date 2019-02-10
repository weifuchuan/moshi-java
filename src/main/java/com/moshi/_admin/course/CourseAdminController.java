package com.moshi._admin.course;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Course;
import com.moshi.course.CourseService;

public class CourseAdminController extends BaseController {

  @Inject private CourseAdminService srv;
  @Inject private CourseService courseService;

  public void lock(int id) {
    Ret ret = srv.lock(id);
    renderJson(ret);
  }

  public void unlock(int id) {
    Ret ret = srv.unlock(id);
    renderJson(ret);
  }

  public void update() {
    Kv items = Kv.create();
    // offerTo discountedPrice price note introduceImage introduce name
    String name = getPara("name");
    String introduce = getPara("introduce");
    String introduceImage = getPara("introduceImage");
    String note = getPara("note");
    Integer price = getParaToInt("price");
    Integer discountedPrice = getParaToInt("discountedPrice");
    Long offerTo = getParaToLong("offerTo");

    items
        .set("name", name)
        .set("introduce", introduce)
        .set("introduceImage", introduceImage)
        .set("note", note)
        .set("price", price)
        .set("discountedPrice", discountedPrice)
        .set("offerTo", offerTo);

    Ret ret = courseService.update(getParaToInt("id"), items);
    renderJson(ret);
  }

  public void publish(int id){
    Ret ret = srv.publish(id);
    renderJson(ret);
  }

  public void unpublish(int id){
    Ret ret = srv.unpublish(id);
    renderJson(ret);
  }
}
