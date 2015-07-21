package com.ryoichi.mahjong.basis.hand

import com.ryoichi.mahjong.basis.tile.{TileKind, TileCategory, Tile}

import scala.collection.mutable.ListBuffer

/**
 * Created by ryoichi on 7/15/15.
 */
case class Hand(tiles :Seq[Tile]) {
  val kinds :TileKinds= TileKinds(tiles.map( tile => tile.kind ).sorted)
  def fString = kinds.fString
  def getTenpai :Seq[ReadyHand] = Node(kinds, Seq[Part](), false).getTenpaiForms()

}

case class TileKinds(seq :Seq[TileKind.Value]) {
  def length = seq.length
  def getFirst = seq(0)
  def remove( k :TileKind.Value ) :TileKinds = {
    val id = seq.indexOf(k)
    id match {
      case -1 => return this
      case _ => return TileKinds(seq.take(id) ++ seq.drop(id + 1))
    }
  }

  def getKotsu( kind :TileKind.Value ) :Option[(TileKinds,Part)] = {
    if (seq.count(k=>{k==kind})>=3) Option((this.remove(kind).remove(kind).remove(kind), Kotsu(kind,false)))
    else None
  }

  def getKotsu() :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.map( k => getKotsu(k) ).collect({ case Some(f) => f })
  }
  def getShuntsu( kind :TileKind.Value) :Option[(TileKinds,Part)] = {
    if (kind.isHonor) return None
    if (kind.rank >= 8) return None
    val id = kind.id
    val (kind2, kind3) = ( TileKind(id+1), TileKind(id+2) )
    if (seq.contains(kind) && seq.contains(kind2) && seq.contains(kind3)) Option((this.remove(kind).remove(kind2).remove(kind3), Shuntsu(kind,false)))
    else None
  }
  def getShuntsu() :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.map( k => getShuntsu(k) ).collect({ case Some(f) => f })
  }
  def getHead( kind :TileKind.Value ) :Option[(TileKinds,Part)] = {
    if (seq.count(k=>{k==kind})>=2) Option((this.remove(kind).remove(kind), Head(kind)))
    else None
  }

  def getHead() :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.map( k => getHead(k) ).collect({ case Some(f) => f })
  }
  def getPenchan( kind :TileKind.Value) :Option[(TileKinds,Part)] = {
    if (kind.isHonor) return None
    if (kind.rank != 3 && kind.rank != 7 ) return None
    val id = kind.id
    val (kind2, kind3) = kind.rank match {
      case 3 => (TileKind(id - 1), TileKind(id - 2))
      case 7 => (TileKind(id + 1), TileKind(id + 2))
    }
    if (seq.contains(kind2) && seq.contains(kind3)) Option((this.remove(kind2).remove(kind3), Penchan(kind)))
    else None
  }
  def getPenchan() :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.map( k => getPenchan(k) ).collect({ case Some(f) => f })
  }
  def getRyanmen( kind :TileKind.Value) :Option[(TileKinds,Part)] = {
    if (kind.isHonor) return None
    if (kind.rank == 7 || kind.rank == 8 ) return None
    val id = kind.id
    val (kind2, kind3) = (TileKind(id + 1), TileKind(id + 2))
    if (seq.contains(kind2) && seq.contains(kind3)) Option((this.remove(kind2).remove(kind3), Ryanmen(kind)))
    else None
  }
  def getRyanmen() :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.map( k => getRyanmen(k) ).collect({ case Some(f) => f })
  }
  def getKanchan( kind :TileKind.Value) :Option[(TileKinds,Part)] = {
    if (kind.isHonor) return None
    if (kind.rank == 1 || kind.rank != 9 ) return None
    val id = kind.id
    val (kind2, kind3) = (TileKind(id - 1), TileKind(id + 1))
    if (seq.contains(kind2) && seq.contains(kind3)) Option((this.remove(kind2).remove(kind3), Kanchan(kind)))
    else None
  }
  def getKanchan() :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.map( k => getKanchan(k) ).collect({ case Some(f) => f })
  }
  def getTanki( kind :TileKind.Value ) :Option[(TileKinds,Part)] = {
    if (seq.count(k=>{k==kind})>=1) Option((this.remove(kind), Tanki(kind)))
    else None
  }
  def getTanki() :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.map( k => getTanki(k) ).collect({ case Some(f) => f })
  }
  def getMentsu() :Seq[(TileKinds,Part)] = getKotsu ++ getShuntsu
  def getTaatsu() :Seq[(TileKinds,Part)] = getPenchan ++ getRyanmen ++ getKanchan

  def fString = {
    val manzuSeq = seq.filter(kind => { kind.category == TileCategory.MANZU }).map( kind => kind.rank )
    val manzuString = manzuSeq.length match {case 0 => ""; case _ => manzuSeq.mkString("") + "m"}
    val sozuSeq = seq.filter(kind => { kind.category == TileCategory.SOZU }).map( kind => kind.rank )
    val sozuString = sozuSeq.length match {case 0 => ""; case _ => sozuSeq.mkString("") + "s"}
    val pinzuSeq = seq.filter(kind => { kind.category == TileCategory.PINZU }).map( kind => kind.rank )
    val pinzuString = pinzuSeq.length match {case 0 => ""; case _ => pinzuSeq.mkString("") + "p"}
    val honorString = seq.filter(kind => kind.isHonor ).mkString("")
    manzuString + sozuString + pinzuString + honorString
  }

}

case class Node(remainingTileKinds :TileKinds, parts: Seq[Part], isTenpai :Boolean) {
  def this(remainingTileKinds :TileKinds) = this(remainingTileKinds, Seq[Part](),false)
  if (remainingTileKinds.length % 3 != 1 && remainingTileKinds.length != 2 && remainingTileKinds.length != 0) throw new RuntimeException
  val childNodes :Seq[Node] = {
    remainingTileKinds.length match {
      case 0 => Seq[Node]()
      case 1 => {
        remainingTileKinds.getTanki().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, true)
        })
      }
      case 2 => {
        remainingTileKinds.getTaatsu().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, true)
        }) ++ remainingTileKinds.getHead().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, true)
        })
      }
      case 4 => {
        remainingTileKinds.getMentsu().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, false)
        }) ++ remainingTileKinds.getHead().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, false)
        })
      }
      case _ => {
        remainingTileKinds.getMentsu().map(pair => {
          val (nextTileKinds, nextPart) = pair
          Node(nextTileKinds, nextPart +: parts, false)
        })
      }
    }
  }
  def traverse(x :ListBuffer[Seq[Part]]) :ListBuffer[Seq[Part]] = {
    childNodes.foreach( c => c.traverse(x) )
    if (isTenpai)  {
      x += parts.toList
    }
    x
  }
  def getTenpaiForms() :Seq[ReadyHand] = {
    traverse(ListBuffer[Seq[Part]]()).map( parts => parts.sorted ).distinct.toList.map( parts => new ReadyHand(parts))
  }
}