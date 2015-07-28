package com.ryoichi.mahjong.basis.hand

import com.ryoichi.mahjong.basis.tile.{TileKind, TileCategory, Tile}

import scala.collection.mutable.ListBuffer

/**
 * Created by ryoichi on 7/15/15.
 */
case class Hand(tiles: Seq[Tile]) {
  val kinds: TileKinds = TileKinds(tiles.map(tile => tile.kind).sorted)

  def fString = kinds.fString

  private val node = Node(kinds, Seq[Part](), (0, TileKind.M1))

  def getTenpai: Seq[ReadyHand] = node.getTenpaiForms()

  val minShanten: Int = node.minShanten()

  def minShantenForms: Seq[Seq[Part]] = node.minShantenForms()

  def acceptableKinds: Seq[TileKind.Value] = {
    if (tiles.length != 13) throw new RuntimeException
    val currentS = minShanten
    TileKind.values.toList.filter(k => {
      val nextHand = Hand(Tile(k.id * 4) +: tiles)
      currentS > nextHand.minShanten
    })
  }

  def candidate: Seq[TileKind.Value] = {
    if (tiles.length != 14) throw new RuntimeException
    val currentS = minShanten
    val primalCandidate = Range(0, 14).toList.map(i => {
      val nextHand = Hand(tiles.patch(i, Seq(), 1))
      val isMinShanten = (currentS == nextHand.minShanten)
      val acceptable = isMinShanten match {
        case true => nextHand.acceptableKinds.map(k => 4 - kinds.kindCount.get(k).getOrElse(0)).reduce(_ + _)
        case false => 0
      }
      (tiles(i).kind, isMinShanten, acceptable)
    })
    val maxAcceptable = primalCandidate.filter(data => data._2).map(data => data._3).max
    primalCandidate.filter(data => data._2 && data._3 == maxAcceptable).map(data => data._1).distinct
  }
}
