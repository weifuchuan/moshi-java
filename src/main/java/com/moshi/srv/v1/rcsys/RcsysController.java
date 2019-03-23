package com.moshi.srv.v1.rcsys;

import com.moshi.common.controller.BaseController;
import com.moshi.easyrec.EasyrecConfig;
import io.jboot.Jboot;

public class RcsysController extends BaseController {
  private EasyrecConfig config = Jboot.config(EasyrecConfig.class, "easyrec");

  public void index(){
    if(isLogin()){

    }else{

    }
  }

  public void config(){
    renderJson(config);
  }
}
