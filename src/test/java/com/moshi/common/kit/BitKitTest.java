package com.moshi.common.kit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitKitTest {

  @Test
  void at() {}

  @Test
  void set() {
    assertEquals(1, BitKit.set(0, 0, 1));
    assertEquals(1 << 2, BitKit.set(0, 2, 1));
    assertEquals(0, BitKit.set(1, 0, 0));
    assertEquals(1 << 1, BitKit.set(0, 1, 1));
  }
}
