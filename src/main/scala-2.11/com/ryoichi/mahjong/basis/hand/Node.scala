package com.ryoichi.mahjong.basis.hand

import com.ryoichi.mahjong.basis.tile.TileKind

import scala.collection.mutable.ListBuffer

/**
 * Created by ryoichi on 7/28/15.
 */

case class Node(remainingTileKinds: TileKinds, parts: Seq[Part], searchedPart: (Int, TileKind.Value)) {
  def this(remainingTileKinds: TileKinds) = this(remainingTileKinds, Seq[Part](), (0, TileKind.M1))

  val childNodes: Seq[Node] = {
    if (remainingTileKinds.length == 0) Seq[Node]()
    else {
      val kotsuNodes = searchedPart match {
        case (0, kind) =>
          remainingTileKinds.searchKotsu(kind).map(pair => {
            val (nextTileKinds, nextPart) = pair
            Node(nextTileKinds, nextPart +: parts, (0, nextPart.kind))
          })
        case _ => Seq[Node]()
      }
      val shuntsuNodes = searchedPart match {
        case (0, kind) => remainingTileKinds.searchShuntsu().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, (1, nextPart.kind))
        })
        case (1, kind) =>
          remainingTileKinds.searchShuntsu(kind).map(pair => {
            val (nextTileKinds, nextPart) = pair
            Node(nextTileKinds, nextPart +: parts, (1, nextPart.kind))
          })
        case _ => Seq[Node]()
      }
      val toitsuNodes = searchedPart match {
        case (i, kind) if i <= 1 => remainingTileKinds.searchToitsu().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, (2, nextPart.kind))
        })
        case (2, kind) => remainingTileKinds.searchToitsu(kind).map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, (2, nextPart.kind))
        })
        case _ => Seq[Node]()
      }
      val penchanNodes = searchedPart match {
        case (i, kind) if i <= 2 => remainingTileKinds.searchPenchan().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, (3, nextPart.kind))
        })
        case (3, kind) => remainingTileKinds.searchPenchan(kind).map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, (3, nextPart.kind))
        })
        case _ => Seq[Node]()
      }
      val ryanmenNodes = searchedPart match {
        case (i, kind) if i <= 3 => remainingTileKinds.searchRyanmen().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, (4, nextPart.kind))
        })
        case (4, kind) => remainingTileKinds.searchRyanmen(kind).map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, (4, nextPart.kind))
        })
        case _ => Seq[Node]()
      }
      val kanchanNodes = searchedPart match {
        case (i, kind) if i <= 4 => remainingTileKinds.searchKanchan().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, (5, nextPart.kind))
        })
        case (5, kind) => remainingTileKinds.searchKanchan(kind).map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, (5, nextPart.kind))
        })
        case _ => Seq[Node]()
      }
      val ukiNodes = Seq[Node](Node(TileKinds(Seq[TileKind.Value]()), remainingTileKinds.seq.map(k => Uki(k)) ++ parts, (6, TileKind.C)))
      kotsuNodes ++ shuntsuNodes ++ toitsuNodes ++ penchanNodes ++ ryanmenNodes ++ kanchanNodes ++ ukiNodes
    }
  }

  def countParts(): Option[(Int, Int, Int)] = {
    if (remainingTileKinds.length > 0) return None
    else {
      val mentsu = parts.count(p => p.isMentsu)
      val toitsu = parts.count(p => p.isToitsu)
      val taatsu = parts.count(p => p.isTaatsu)
      return Option(mentsu, toitsu, taatsu)
    }
  }

  def isTenpai(): Boolean = {
    if (countParts().isEmpty) return false
    if (shanten.get == 0) true else false
  }

  def shanten(): Option[Int] = {
    if (countParts().isEmpty) return None
    val (mentsu, toitsu, taatsu) = countParts().get
    val primitiveShanten = if (toitsu >= 1) {
      List(0, 5 - mentsu - toitsu - taatsu).max + 3 - mentsu
    } else {
      List(0, 4 - mentsu - taatsu).max + 4 - mentsu
    }
//    val shantenSu = if (primitiveShanten > 0) {
//      primitiveShanten
//    }
//    else if (parts.filter(p => p.isToitsu || p.isUki || p.isKotsu).map(p => p.kind).groupBy(identity).mapValues(_.size).values.count(x => x >= 2) >= 1) {
//      primitiveShanten + 1
//    }
//    else primitiveShanten
    return Option(primitiveShanten)
  }

  private def traverseShanten(x: ListBuffer[Int]): ListBuffer[Int] = {
    childNodes.foreach(c => c.traverseShanten(x))
    if (shanten.nonEmpty) {
      x += shanten().get
    }
    x
  }

  private def traverseShantenForms(s: Int, x: ListBuffer[Seq[Part]]): ListBuffer[Seq[Part]] = {
    childNodes.foreach(c => c.traverseShantenForms(s, x))
    if (shanten.nonEmpty && shanten.get == s) {
      x += parts.toList
    }
    x
  }

  private def traverseTenpai(x: ListBuffer[Seq[Part]]): ListBuffer[Seq[Part]] = {
    traverseShantenForms(0, x)
  }


  def getTenpaiForms(): Seq[ReadyHand] = {
    traverseTenpai(ListBuffer[Seq[Part]]()).toList.map(parts => new ReadyHand(parts))
  }

  def minShanten(): Int = {
    traverseShanten(ListBuffer[Int]()).min
  }

  def minShantenForms(): List[Seq[Part]] = {
    val ms = minShanten()
    traverseShantenForms(ms, ListBuffer[Seq[Part]]()).toList
  }
}