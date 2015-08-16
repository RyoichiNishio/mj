package com.ryoichi.mahjong.game.impl

import com.ryoichi.mahjong.basis.tile.{TileKind, Tile}
import com.ryoichi.mahjong.game.OpenSet
import com.ryoichi.mahjong.game.abst.PlayerStatus.PlayerStatus
import com.ryoichi.mahjong.game.abst.{PlayerStatus, Player}
import com.ryoichi.mahjong.game.abst.PlayerStatus.PlayerStatus

/**
 * Created by ryoichi on 8/15/15.
 */
case class DefaultPlayer(
    hand: Set[Tile],
    tsumoTile: Option[Tile],
    openSets: Seq[OpenSet],
    status: PlayerStatus,
    isFuriten: Boolean
) extends Player {
    def this(hand: Set[Tile]) = this(hand, None, Seq[OpenSet](), PlayerStatus.ClOSED, false)
    override val winningTileKinds: Set[TileKind.Value] = MjScorer.calcTenpai(hand, tsumoTile, openSets)
    override val isTenpai: Boolean = !winningTileKinds.isEmpty

}
