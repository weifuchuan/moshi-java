package com.moshi.srv.v1.im;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyKitTest {
  @Test
  public void roomKey() {
    System.out.println(KeyKit.roomKey(1));
    System.out.println(KeyKit.roomKey(1, 2, 3));
    assertEquals(KeyKit.roomKey(1, 2, 3), KeyKit.roomKey(1, 3, 2));
    assertEquals(KeyKit.roomKey(1, 2, 4, 5, 6, 3), KeyKit.roomKey(1, 3, 2, 6, 4, 5));
  }
}
