package com.moshi.common.authcode;

import com.jfinal.kit.HashKit;
import org.junit.jupiter.api.Test;

public class AuthCodeServiceTests {
  @Test
  public void seeAuthCodeStyle(){
    System.out.println(HashKit.sha256("url977782"));
  }
}
