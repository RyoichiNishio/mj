package com.ryoichi.mahjong.game.abst

import com.ryoichi.mahjong.basis.tile.Tile
import com.ryoichi.mahjong.game.GameType.GameType
import com.ryoichi.mahjong.game.{Round, GameType}

/**
 * Created by ryoichi on 8/14/15.
 */
abstract class Field {
    val gameType: GameType
    val scores: Seq[Int]
    val round: Round
    val deposit: Int
    val pond: Seq[FieldEvent]
    val doraIndicatingTiles: Seq[Tile]
    val isFirstTurnWithoutMeld: Boolean
    val nKang: Int
}