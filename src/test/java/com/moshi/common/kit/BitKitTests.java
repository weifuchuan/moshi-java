package com.moshi.common.kit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BitKitTests {
  @Test
  public void at() {
    Assertions.assertEquals(1, BitKit.at(1 << 0, 0));
    Assertions.assertEquals(0, BitKit.at(1 << 1, 0));
    Assertions.assertEquals(1, BitKit.at((1 << 0) + (1 << 1), 0));
  }
}
