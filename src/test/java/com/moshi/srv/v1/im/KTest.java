package com.moshi.srv.v1.im;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KTest {
  @Test
  public void roomKey() {
    System.out.println(K.roomKey(1, 2));
    System.out.println(K.roomKey(2, 1));
    assertEquals(K.roomKey(1, 2, 3), K.roomKey(1, 3, 2));
    assertEquals(K.roomKey(1, 2, 4, 5, 6, 3), K.roomKey(1, 3, 2, 6, 4, 5));
    assertEquals(K.roomKey(2, 1), K.roomKey(1, 2));
  }
}
