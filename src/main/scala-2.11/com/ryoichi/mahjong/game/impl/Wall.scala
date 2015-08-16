package com.ryoichi.mahjong.game.impl

import com.ryoichi.mahjong.game.abst.Wall

import scala.util.Random

/**
 * Created by ryoichi on 8/15/15.
 */
object Wall {
    def createAtRandom(seed: Long): Wall = {
        Random.setSeed(seed)
        val randomSeq: Seq[Int] = List.fill(136)(Random.nextInt(136))
        val wallTiles: Seq[Int] = randomSeq.take(136 - 14)
        val deadWallTiles: Seq[Int] = randomSeq.takeRight(14)
        DefaultWall(wallTiles, deadWallTiles)
    }
}
