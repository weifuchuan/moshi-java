package com.moshi.common.kit;

public class BitKit {
  public static int at(int num, int i) {
    return (num & (1 << i)) == 0 ? 0 : 1;
  }

  public static int set(int num, int i, int bit) {
    if (bit == 0) {
      return num & ~(1 << i);
    } else {
      return num | (1 << i);
    }
  }
}
