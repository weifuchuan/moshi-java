package com.moshi.im

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

fun main() = runBlocking<Unit> {
  repeat(100_000){
    launch{
      delay(1000)
      println(".")
    }
  }
}
