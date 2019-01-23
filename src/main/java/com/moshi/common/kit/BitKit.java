package com.moshi.common.kit;

public class BitKit {
  public static int at(int num, int i) {
    return (num & (1 << i)) == 0 ? 0 : 1;
  }
}
