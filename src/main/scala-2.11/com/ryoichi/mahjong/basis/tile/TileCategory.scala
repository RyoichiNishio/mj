package com.ryoichi.mahjong.basis.tile

/**
 * Created by ryoichi on 7/15/15.
 */

object TileCategory extends Enumeration {
  type TileCategory = Value
  val MANZU, SOZU, PINZU, WIND, DRAGON = Value
  case class TileCategoryValue(category : Value){
    def char = category match {
      case MANZU => "m"
      case SOZU => "s"
      case PINZU => "p"
      case _ => ""
    }
    def isSuit = category match {
      case WIND => false
      case DRAGON => false
      case _ => true
    }
    def isHonor = !isSuit
  }
  implicit def value2TileCategoryValue(category: Value) = new TileCategoryValue(category)
}

