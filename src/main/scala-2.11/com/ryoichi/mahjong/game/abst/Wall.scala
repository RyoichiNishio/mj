package com.ryoichi.mahjong.game.abst

import com.ryoichi.mahjong.exception.WallException

import scala.util.Random

abstract class Wall {
    val wallTiles: Seq[Int]
    val deadWallTiles: Seq[Int]
    def rest :Int = wallTiles.length
    def doraIndicatingTile: Int = deadWallTiles(4)
    def uradoraIndicatingTile: Int = deadWallTiles(5)
    def kanDoraIndicatingTile(nthKan: Int): Int = deadWallTiles(2 + 2 * nthKan)
    def kanUraIndicatingTile(nthKan: Int): Int = deadWallTiles(3 + 2 * nthKan)
    def tsumo: (Int, Wall)
    def rinshanTsumo: (Int, Wall)
    def haipai: (Seq[Seq[Int]],Wall)
}
