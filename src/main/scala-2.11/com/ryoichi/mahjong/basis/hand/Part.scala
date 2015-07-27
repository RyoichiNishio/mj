package com.ryoichi.mahjong.basis.hand

import com.ryoichi.mahjong.basis.tile.TileKind

/**
 * Created by ryoichi on 7/15/15.
 */

abstract class Part extends Ordered[Part]{
  val kind :TileKind.Value
  def isMentsu  = false
  def isTaatsu  = false
  def isKotsu   = false
  def isShuntsu = false
  def isHead    = false
  def isPenchan = false
  def isKanchan = false
  def isRyanmen = false
  def isUki   = false

  override def compare(that: Part): Int = {
    this.toString.compare(that.toString)
  }

}

abstract class Mentsu extends Part {
  val isMelded :Boolean
  override def isMentsu = true
}
abstract class Taatsu extends Part {
  override def isTaatsu = true
}

case class Kotsu(kind :TileKind.Value, isMelded: Boolean) extends Mentsu {
  override def isKotsu = true
  override def toString = kind.isSuit match {
    case ture => "(" + kind.rank + kind.rank + kind.rank + kind.category.char + ")"
    case false => "(" + kind + kind + kind + ")"
  }
}


case class Shuntsu(kind :TileKind.Value, isMelded: Boolean) extends Mentsu {
  if (kind.isHonor) throw new RuntimeException
  if (kind.rank >= 8) throw new RuntimeException
  override def isShuntsu = true
  override def toString = {
    val char :String = kind.category.char
    val leftRank :Int = kind.rank
    val (middleRank, rightRank) = (leftRank + 1, leftRank + 2)
    "(" + leftRank + middleRank + rightRank + char + ")"
  }
}
case class Head(kind :TileKind.Value) extends Part {
  override def isHead = true
  override def toString = kind.isSuit match {
    case ture => "<" + kind.rank + kind.rank + kind.category.char + ">"
    case false => "<" + kind + kind + ">"
  }
}
case class Penchan(kind :TileKind.Value) extends Taatsu {
  if (kind.isHonor) throw new RuntimeException
  if (kind.rank != 3 && kind.rank != 7 ) throw new RuntimeException
  override def isPenchan = true
  override def toString = kind.rank match {
    case 3 => "[" + 1 + 2 + kind.category.char + "]"
    case 7 => "[" + 8 + 9 + kind.category.char + "]"
  }
}
case class Ryanmen(kind :TileKind.Value) extends Taatsu {
  if (kind.isHonor) throw new RuntimeException
  if (kind.rank == 7 || kind.rank == 8 || kind.rank == 9 ) throw new RuntimeException
  override def isRyanmen = true
  override def toString = {
    val ( firstRank, secondRank ) = (kind.rank+1, kind.rank+2)
    "[" + firstRank + secondRank + kind.category.char + "]"
  }
}
case class Kanchan(kind :TileKind.Value) extends Taatsu {
  if (kind.isHonor) throw new RuntimeException
  if (kind.rank == 1 || kind.rank == 9) throw new RuntimeException

  override def isKanchan = true

  override def toString = {
    val (firstRank, secondRank) = (kind.rank - 1, kind.rank + 1)
    "[" + firstRank + secondRank + kind.category.char + "]"
  }
}
case class Uki(kind :TileKind.Value) extends Part {
  override def isUki = true
  override def toString = kind.isSuit match {
    case ture => "[" + kind.rank + kind.category.char + "]"
    case false => "[" + kind + "]"
  }
}


