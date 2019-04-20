package com.moshi.im.common;

import com.moshi.im.common.model._ModelGenerator;
import com.moshi.im.common.payload._PayloadModelGenerator;

public class _Generator {
  public static void main(String[] args){
    _PayloadModelGenerator.main(args);
    _ModelGenerator.main(args);
  }
}
