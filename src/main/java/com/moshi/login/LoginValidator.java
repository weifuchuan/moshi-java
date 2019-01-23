package com.moshi.login;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class LoginValidator extends Validator {

  @Override
  protected void validate(Controller c) {
    setShortCircuit(true);

    validateEmail("email", "msg", "email格式错误");
    validateRequiredString("password", "msg", "必须填写密码");
    validateCaptcha("captcha", "msg", "验证码错误");
  }

  @Override
  protected void handleError(Controller c) {
    c.renderJson();
  }

}
