package com.ryoichi.mahjong.basis.hand

import com.ryoichi.mahjong.basis.tile.{TileKind, TileCategory, Tile}

import scala.collection.mutable.ListBuffer

/**
 * Created by ryoichi on 7/15/15.
 */
case class Hand(tiles :Seq[Tile]) {
  val kinds :TileKinds= TileKinds(tiles.map( tile => tile.kind ).sorted)
  def fString = kinds.fString
  private val node = Node(kinds, Seq[Part](), (0,TileKind.M1))
  def getTenpai :Seq[ReadyHand] = node.getTenpaiForms()
  def minShanten :Int = node.minShanten()
  def minShantenForms :Seq[Seq[Part]] = node.minShantenForms()
  def acceptableKinds :Seq[TileKind.Value] = {
    if ( tiles.length != 13 ) throw new RuntimeException
    val currentS = minShanten
    println(currentS)
    TileKind.values.toList.filter( k => {
      val nextHand = Hand(Tile(k.id*4) +: tiles )
      println(nextHand.fString)
      currentS > nextHand.minShanten
    })
  }
  def candidate :Seq[TileKind.Value] = {
    if ( tiles.length != 14 ) throw new RuntimeException
    val currentS = minShanten
    val primalCandidate = Range(0,14).toList.map( i => {
      val nextHand = Hand(tiles.patch(i,Seq(),1))
      val isMinShanten = (currentS == nextHand.minShanten)
      val acceptable = isMinShanten match {
        case true => nextHand.acceptableKinds.map( k => 4 - kinds.kindCount.get(k).getOrElse(0) ).reduce(_+_)
        case false => 0
      }
      (tiles(i).kind, isMinShanten, acceptable)
    })
    val maxAcceptable = primalCandidate.filter( data => data._2 ).map( data => data._3 ).max
    primalCandidate.filter( data => data._2 && data._3 == maxAcceptable ).map( data => data._1).distinct
  }
}

case class TileKinds(seq :Seq[TileKind.Value]) {
  val kindCount = seq.groupBy(identity).mapValues(_.size)
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

  def searchKotsu( kind :TileKind.Value) :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.filter( k => k >= kind ).map( k => getKotsu(k) ).collect({ case Some(f) => f })
  }

  def searchKotsu() :Seq[(TileKinds,Part)] = {
    searchKotsu(TileKind.M1)
  }

  def getShuntsu( kind :TileKind.Value) :Option[(TileKinds,Part)] = {
    if (kind.isHonor) return None
    if (kind.rank >= 8) return None
    val id = kind.id
    val (kind2, kind3) = ( TileKind(id+1), TileKind(id+2) )
    if (seq.contains(kind) && seq.contains(kind2) && seq.contains(kind3)) Option((this.remove(kind).remove(kind2).remove(kind3), Shuntsu(kind,false)))
    else None
  }
  def searchShuntsu( kind :TileKind.Value ) :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.filter( k => k >= kind ).map( k => getShuntsu(k) ).collect({ case Some(f) => f })
  }
  def searchShuntsu() :Seq[(TileKinds,Part)] = {
    searchShuntsu( TileKind.M1 )
  }

  def getToitsu( kind :TileKind.Value ) :Option[(TileKinds,Part)] = {
    if (seq.count(k=>{k==kind})>=2) Option((this.remove(kind).remove(kind), Head(kind)))
    else None
  }

  def searchToitsu( kind :TileKind.Value ) :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.filter( k => k >= kind ).map( k => getToitsu(k) ).collect({ case Some(f) => f })
  }
  def searchToitsu( ) :Seq[(TileKinds,Part)] = {
    searchToitsu( TileKind.M1)
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
  def searchPenchan( kind :TileKind.Value) :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.filter(k => k >= kind).map( k => getPenchan(k) ).collect({ case Some(f) => f })
  }
  def searchPenchan() :Seq[(TileKinds,Part)] = {
    searchPenchan( TileKind.M1)
  }

  def getRyanmen( kind :TileKind.Value) :Option[(TileKinds,Part)] = {
    if (kind.isHonor) return None
    if (kind.rank == 7 || kind.rank == 8 || kind.rank == 9 ) return None
    val id = kind.id
    val (kind2, kind3) = (TileKind(id + 1), TileKind(id + 2))
    if (seq.contains(kind2) && seq.contains(kind3)) Option((this.remove(kind2).remove(kind3), Ryanmen(kind)))
    else None
  }
  def searchRyanmen( kind :TileKind.Value ) :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.filter( k => k>=kind ).map( k => getRyanmen(k) ).collect({ case Some(f) => f })
  }
  def searchRyanmen( ) :Seq[(TileKinds,Part)] = {
    searchRyanmen( TileKind.M1 )
  }
  def getKanchan( kind :TileKind.Value) :Option[(TileKinds,Part)] = {
    if (kind.isHonor) return None
    if (kind.rank == 1 || kind.rank == 9 ) return None
    val id = kind.id
    val (kind2, kind3) = (TileKind(id - 1), TileKind(id + 1))
    if (seq.contains(kind2) && seq.contains(kind3)) Option((this.remove(kind2).remove(kind3), Kanchan(kind)))
    else None
  }
  def searchKanchan( kind :TileKind.Value ) :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.filter( k => k >= kind ).map( k => getKanchan(k) ).collect({ case Some(f) => f })
  }
  def searchKanchan() :Seq[(TileKinds,Part)] = {
    searchKanchan( TileKind.M1 )
  }

  def getUki( kind :TileKind.Value ) :Option[(TileKinds,Part)] = {
    if (seq.count(k=>{k==kind})>=1) Option((this.remove(kind), Uki(kind)))
    else None
  }
  def searchUki( kind : TileKind.Value ) :Seq[(TileKinds,Part)] = {
    TileKind.values.toList.filter( k => k >= kind ).map( k => getUki(k) ).collect({ case Some(f) => f })
  }
  def searchUki() :Seq[(TileKinds,Part)] = {
    searchUki( TileKind.M1 )
  }
  def searchMentsu() :Seq[(TileKinds,Part)] = searchKotsu ++ searchShuntsu
  def searchTaatsu() :Seq[(TileKinds,Part)] = searchPenchan ++ searchRyanmen ++ searchKanchan

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

case class Node(remainingTileKinds :TileKinds, parts: Seq[Part], searchedPart : (Int, TileKind.Value)) {
  def this(remainingTileKinds :TileKinds) = this(remainingTileKinds, Seq[Part](), (0, TileKind.M1))
  val childNodes :Seq[Node] = {
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

  def countParts() :Option[(Int,Int,Int)] = {
    if (remainingTileKinds.length > 0) return None
    else {
      val mentsu = parts.count( p => p.isMentsu )
      val toitsu = parts.count( p => p.isHead )
      val taatsu = parts.count( p => p.isTaatsu )
      return Option(mentsu, toitsu, taatsu)
    }
  }
  def isTenpai() : Boolean = {
    if (countParts().isEmpty) return false
    if (shanten.get == 0) true else false
  }
  def shanten() : Option[Int] = {
    if (countParts().isEmpty) return None
    val (mentsu, toitsu, taatsu) = countParts().get
    val primitiveShanten = if (toitsu >= 1) {
      List(0, 5-mentsu-toitsu-taatsu).max + 3-mentsu
    } else {
      List(0,4-mentsu-taatsu).max + 4-mentsu
    }
    val shantenSu = if (primitiveShanten > 0) {primitiveShanten}
    else if (parts.filter( p => p.isHead || p.isUki || p.isKotsu ).map( p => p.kind ).groupBy(identity).mapValues(_.size).values.count(x => x>= 2) >= 1 ) {
      primitiveShanten+1
    }
    else primitiveShanten
    return Option(shantenSu)
  }

  private def traverseShanten(x :ListBuffer[Int]) :ListBuffer[Int] = {
    childNodes.foreach( c => c.traverseShanten(x) )
    if (shanten.nonEmpty) {x += shanten().get }
    x
  }

  private def traverseShantenForms(s :Int, x :ListBuffer[Seq[Part]]) :ListBuffer[Seq[Part]] = {
    childNodes.foreach( c => c.traverseShantenForms(s, x) )
    if (shanten.nonEmpty && shanten.get == s )  {
      x += parts.toList
    }
    x
  }

  private def traverseTenpai(x :ListBuffer[Seq[Part]]) :ListBuffer[Seq[Part]] = {
    traverseShantenForms(0,x)
  }


  def getTenpaiForms() :Seq[ReadyHand] = {
    traverseTenpai(ListBuffer[Seq[Part]]()).toList.map( parts => new ReadyHand(parts))
  }
  def minShanten() :Int = {
    traverseShanten(ListBuffer[Int]()).min
  }
  def minShantenForms() :List[Seq[Part]] = {
    val ms = minShanten()
    traverseShantenForms(ms, ListBuffer[Seq[Part]]()).toList
  }
}