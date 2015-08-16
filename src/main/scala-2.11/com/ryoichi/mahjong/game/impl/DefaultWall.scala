package com.ryoichi.mahjong.game.impl

import com.ryoichi.mahjong.exception.WallException
import com.ryoichi.mahjong.game.abst.Wall

import scala.util.Random

/**
 * Created by ryoichi on 8/15/15.
 */
case class DefaultWall(
    wallTiles: Seq[Int],
    deadWallTiles: Seq[Int]
) extends Wall {

    override def tsumo: (Int, Wall) = {
        val tsumo: Int = wallTiles.head
        val nextWall: Wall = this.copy(wallTiles = this.wallTiles.tail)
        (tsumo, nextWall)
    }

    override def rinshanTsumo: (Int, Wall) = {
        val rinshanTsumo: Int = deadWallTiles.head
        val nextWallTiles: Seq[Int] = wallTiles.init
        val nextDeadWallTiles: Seq[Int] = (deadWallTiles.slice(1, 4) :+ wallTiles.last) ++ deadWallTiles.takeRight(10)
        (rinshanTsumo, DefaultWall(nextWallTiles, nextDeadWallTiles))
    }

    override def haipai: (Seq[Seq[Int]], Wall) = {
        if (rest != 122) throw new WallException("Haipai is not allowed when rest != 122.")
        val oya: Seq[Int] = Seq(0, 1, 2, 3, 16, 17, 18, 19, 32, 33, 34, 35, 48, 49, 50, 51, 64, 68).map(i => wallTiles(i))
        val ko1: Seq[Int] = Seq(4, 5, 6, 7, 20, 21, 22, 23, 36, 37, 38, 39, 52, 53, 54, 55, 65).map(i => wallTiles(i))
        val ko2: Seq[Int] = Seq(8, 9, 10, 11, 24, 25, 26, 27, 40, 41, 42, 43, 56, 57, 58, 59, 66).map(i => wallTiles(i))
        val ko3: Seq[Int] = Seq(12, 13, 14, 15, 28, 29, 30, 31, 44, 45, 46, 47, 60, 61, 62, 63).map(i => wallTiles(i))
        val haipai: Seq[Seq[Int]] = Seq(oya, ko1, ko2, ko3)
        val wallAfterHaipai: Wall = copy(wallTiles = wallTiles.takeRight(69))
        (haipai, wallAfterHaipai)
    }
}


