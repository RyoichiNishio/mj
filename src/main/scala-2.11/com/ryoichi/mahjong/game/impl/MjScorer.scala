package com.ryoichi.mahjong.game.impl

import com.ryoichi.mahjong.basis.hand.{TileKinds, Part, Node}
import com.ryoichi.mahjong.basis.tile.{TileKind, Tile}
import com.ryoichi.mahjong.game.OpenSet

/**
 * Created by ryoichi on 8/15/15.
 */
object MjScorer {
    def calcTenpai(hand :Set[Tile], tsumo :Option[Tile], openSets :Seq[OpenSet]) :Set[TileKind.Value] = {
        val kinds: Seq[TileKind.Value] = hand.map(t => t.kind).toList
        val completedSets :Seq[Part] = openSets.map( openSet => openSet.mentsu )
        val node = Node(TileKinds(kinds), completedSets, (0, TileKind.M1))
        node.getWinningTiles
    }
}
