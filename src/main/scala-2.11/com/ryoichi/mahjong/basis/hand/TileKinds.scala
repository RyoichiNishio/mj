package com.ryoichi.mahjong.basis.hand

import com.ryoichi.mahjong.basis.tile.{TileCategory, TileKind}

/**
 * Created by ryoichi on 7/28/15.
 */


case class TileKinds(seq: Seq[TileKind.Value]) {
  val kindCount = seq.groupBy(identity).mapValues(_.size)

  def length = seq.length

  def getFirst = seq(0)

  def remove(k: TileKind.Value): TileKinds = {
    val id = seq.indexOf(k)
    id match {
      case -1 => return this
      case _ => return TileKinds(seq.take(id) ++ seq.drop(id + 1))
    }
  }

  def getKotsu(kind: TileKind.Value): Option[(TileKinds, Part)] = {
    if (kindCount.get(kind).getOrElse(0)>=3) Option((this.remove(kind).remove(kind).remove(kind), Kotsu(kind, false)))
    else None
  }

  def searchKotsu(kind: TileKind.Value): Seq[(TileKinds, Part)] = {
    kindCount.keys.toList.filter(k => k >= kind).map(k => getKotsu(k)).collect({ case Some(f) => f })
  }

  def searchKotsu(): Seq[(TileKinds, Part)] = {
    searchKotsu(TileKind.M1)
  }

  def getShuntsu(kind: TileKind.Value): Option[(TileKinds, Part)] = {
    if (kind.isHonor) return None
    if (kind.rank >= 8) return None
    val id = kind.id
    val (kind2, kind3) = (TileKind(id + 1), TileKind(id + 2))
    if (kindCount.get(kind).getOrElse(0) >= 1 && kindCount.get(kind2).getOrElse(0) >= 1 && kindCount.get(kind3).getOrElse(0) >= 1 ) Option((this.remove(kind).remove(kind2).remove(kind3), Shuntsu(kind, false)))
    else None
  }

  def searchShuntsu(kind: TileKind.Value): Seq[(TileKinds, Part)] = {
    kindCount.keys.toList.filter(k => k >= kind).map(k => getShuntsu(k)).collect({ case Some(f) => f })
  }

  def searchShuntsu(): Seq[(TileKinds, Part)] = {
    searchShuntsu(TileKind.M1)
  }

  def getToitsu(kind: TileKind.Value): Option[(TileKinds, Part)] = {
    if (kindCount.get(kind).getOrElse(0)>=2) Option((this.remove(kind).remove(kind), Toitsu(kind)))
    else None
  }

  def searchToitsu(kind: TileKind.Value): Seq[(TileKinds, Part)] = {
    kindCount.keys.toList.filter(k => k >= kind).map(k => getToitsu(k)).collect({ case Some(f) => f })
  }

  def searchToitsu(): Seq[(TileKinds, Part)] = {
    searchToitsu(TileKind.M1)
  }

  def getPenchan(kind: TileKind.Value): Option[(TileKinds, Part)] = {
    if (kind.isHonor) return None
    if (kind.rank != 3 && kind.rank != 7) return None
    val id = kind.id
    val (kind2, kind3) = kind.rank match {
      case 3 => (TileKind(id - 1), TileKind(id - 2))
      case 7 => (TileKind(id + 1), TileKind(id + 2))
    }
    if (kindCount.get(kind2).getOrElse(0) >= 1 && kindCount.get(kind3).getOrElse(0) >= 1) Option((this.remove(kind2).remove(kind3), Penchan(kind)))
    else None
  }

  def searchPenchan(kind: TileKind.Value): Seq[(TileKinds, Part)] = {
    List(TileKind.M3,TileKind.M7,TileKind.S3,TileKind.S7,TileKind.P3,TileKind.P7).filter(k => k >= kind).map(k => getPenchan(k)).collect({ case Some(f) => f })
  }

  def searchPenchan(): Seq[(TileKinds, Part)] = {
    searchPenchan(TileKind.M1)
  }

  def getRyanmen(kind: TileKind.Value): Option[(TileKinds, Part)] = {
    if (kind.isHonor) return None
    if (kind.rank == 7 || kind.rank == 8 || kind.rank == 9) return None
    val id = kind.id
    val (kind2, kind3) = (TileKind(id + 1), TileKind(id + 2))
    if (kindCount.get(kind2).getOrElse(0) >= 1 && kindCount.get(kind3).getOrElse(0) >= 1) Option((this.remove(kind2).remove(kind3), Ryanmen(kind)))
    else None
  }

  def searchRyanmen(kind: TileKind.Value): Seq[(TileKinds, Part)] = {
    TileKind.values.toList.filter(k => k >= kind).map(k => getRyanmen(k)).collect({ case Some(f) => f })
  }

  def searchRyanmen(): Seq[(TileKinds, Part)] = {
    searchRyanmen(TileKind.M1)
  }

  def getKanchan(kind: TileKind.Value): Option[(TileKinds, Part)] = {
    if (kind.isHonor) return None
    if (kind.rank == 1 || kind.rank == 9) return None
    val id = kind.id
    val (kind2, kind3) = (TileKind(id - 1), TileKind(id + 1))
    if (kindCount.get(kind2).getOrElse(0) >= 1 && kindCount.get(kind3).getOrElse(0) >= 1) Option((this.remove(kind2).remove(kind3), Kanchan(kind)))
    else None
  }

  def searchKanchan(kind: TileKind.Value): Seq[(TileKinds, Part)] = {
    TileKind.values.toList.filter(k => k >= kind).map(k => getKanchan(k)).collect({ case Some(f) => f })
  }

  def searchKanchan(): Seq[(TileKinds, Part)] = {
    searchKanchan(TileKind.M1)
  }

  def getUki(kind: TileKind.Value): Option[(TileKinds, Part)] = {
    if (seq.count(k => {
      k == kind
    }) >= 1) Option((this.remove(kind), Uki(kind)))
    else None
  }

  def searchUki(kind: TileKind.Value): Seq[(TileKinds, Part)] = {
    TileKind.values.toList.filter(k => k >= kind).map(k => getUki(k)).collect({ case Some(f) => f })
  }

  def searchUki(): Seq[(TileKinds, Part)] = {
    searchUki(TileKind.M1)
  }

  def searchMentsu(): Seq[(TileKinds, Part)] = searchKotsu ++ searchShuntsu

  def searchTaatsu(): Seq[(TileKinds, Part)] = searchPenchan ++ searchRyanmen ++ searchKanchan

  def fString = {
    val manzuSeq = seq.filter(kind => {
      kind.category == TileCategory.MANZU
    }).map(kind => kind.rank)
    val manzuString = manzuSeq.length match {
      case 0 => "";
      case _ => manzuSeq.mkString("") + "m"
    }
    val sozuSeq = seq.filter(kind => {
      kind.category == TileCategory.SOZU
    }).map(kind => kind.rank)
    val sozuString = sozuSeq.length match {
      case 0 => "";
      case _ => sozuSeq.mkString("") + "s"
    }
    val pinzuSeq = seq.filter(kind => {
      kind.category == TileCategory.PINZU
    }).map(kind => kind.rank)
    val pinzuString = pinzuSeq.length match {
      case 0 => "";
      case _ => pinzuSeq.mkString("") + "p"
    }
    val honorString = seq.filter(kind => kind.isHonor).mkString("")
    manzuString + sozuString + pinzuString + honorString
  }

}
