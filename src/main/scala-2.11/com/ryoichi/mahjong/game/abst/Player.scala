package com.ryoichi.mahjong.game.abst

import com.ryoichi.mahjong.basis.tile.{Tile, TileKind}
import com.ryoichi.mahjong.game.OpenSet
import com.ryoichi.mahjong.game.abst.PlayerStatus.PlayerStatus

/**
 * Created by ryoichi on 8/13/15.
 */

abstract class Player {
    val hand: Set[Tile]
    val openSets: Seq[OpenSet]
    val tsumoTile :Option[Tile]
    val status: PlayerStatus
    val isTenpai: Boolean
    val winningTileKinds: Set[TileKind.Value]
    val isFuriten: Boolean
}

// tilesは長さが3または4のTileIDの配列
// 0番目が鳴いた牌。1,2番目が手牌から晒した牌である。
// meldTypeがsmall kangの場合、stolenTileでもdisclosedTilesにもない4つ目のtileがカカンした牌であるとする。
// from: 下家1, 対面2, 上家3, アンカンの場合0, カカンの場合、ポンしたplayerの場所を指す。


object PlayerStatus extends Enumeration {
    type PlayerStatus = Value
    val ClOSED, MELDED, REACH_DECLARED, REACH, DOUBLE_REACH = Value
}

