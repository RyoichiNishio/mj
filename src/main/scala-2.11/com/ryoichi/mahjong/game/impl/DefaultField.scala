package com.ryoichi.mahjong.game.impl

import com.ryoichi.mahjong.basis.tile.Tile
import com.ryoichi.mahjong.game.{Round, GameType}
import com.ryoichi.mahjong.game.GameType.GameType
import com.ryoichi.mahjong.game.abst.{FieldEvent, Field}

/**
 * Created by ryoichi on 8/15/15.
 */
case class DefaultField(
    gameType: GameType,
    scores: Seq[Int],
    round: Round,
    deposit: Int,
    pond: Seq[FieldEvent],
    doraIndicatingTiles: Seq[Tile],
    isFirstTurnWithoutMeld: Boolean,
    nKang: Int
) extends Field


