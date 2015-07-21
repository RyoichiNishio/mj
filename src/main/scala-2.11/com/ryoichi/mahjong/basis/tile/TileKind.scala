package com.ryoichi.mahjong.basis.tile

import com.ryoichi.mahjong.basis.tile.TileCategory.TileCategory

/**
 * Created by ryoichi on 7/15/15.
 */


object TileKind extends Enumeration {
  // Suit Tiles
  // manzu
  val M1, M2, M3, M4, M5, M6, M7, M8, M9 = Value
  // sozu
  val S1, S2, S3, S4, S5, S6, S7, S8, S9 = Value
  // pinzu
  val P1, P2, P3, P4, P5, P6, P7, P8, P9 = Value
  // Honor Tiles
  // wind
  val E, S, W, N = Value
  // dragon
  val P, F, C = Value // haku, hatsu, chun

  case class TileKindValue(kind :Value){
    val category :TileCategory = kind.id match {
      case id if id <= 8 => TileCategory.MANZU
      case id if id <= 17 => TileCategory.SOZU
      case id if id <= 26 => TileCategory.PINZU
      case id if id <= 30 => TileCategory.WIND
      case _ => TileCategory.DRAGON
    }
    def isSuit = category.isSuit
    def isHonor = category.isHonor
    def rank :Int = kind.id match {
      case id if id <= 26 => id % 9 + 1
      case _ => throw new RuntimeException("rank error")
    }
    def isYaochu = {
      if (isHonor) true
      else if (rank == 1 || rank == 9) true
      else false
    }
    def isChunchan = !isYaochu
  }
  implicit def value2TileKindValue(kind: Value) = new TileKindValue(kind)
}
