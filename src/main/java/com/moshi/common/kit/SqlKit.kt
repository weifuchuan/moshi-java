package com.moshi.common.kit

class SqlKit {
  companion object Me {
    fun is0At(field: String, pos: Int): String {
      return " ($field & (1 << $pos)) = 0 "
    }

    fun is1At(field: String, pos: Int): String {
      return " ($field & (1 << $pos)) != 0 "
    }

    fun set0At(field:String, pos:Int ):String{
      return " $field = ($field & ~(1 << $pos)) "
    }

    fun set1At(field:String, pos:Int ):String{
      return " $field = ($field | (1 << $pos)) "
    }
  }
}

