package com.moshi.reg

import cn.hutool.core.io.FileUtil
import com.alibaba.fastjson.JSON
import java.util.concurrent.atomic.AtomicInteger

class AccountGenerator {
  companion object {
    fun gen() {
      val srv = RegService()
      val raw =
        FileUtil.readString("E:\\code\\java\\moshi\\src\\main\\java\\com\\moshi\\reg\\words.json", Charsets.UTF_8)
      val names = JSON.parseArray(raw)
      val index = AtomicInteger(518)
      names.stream().parallel().forEach {
        val ret = srv.regByEmail("test${index.incrementAndGet()}@moshi.com", "abc123456", it.toString())!!
        if (ret.isOk) {
          println("generate account $it ok")
        }
      }
    }
  }
}

