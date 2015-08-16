package com.ryoichi.mahjong.game.abst

import com.ryoichi.mahjong.basis.tile.Tile
import com.ryoichi.mahjong.game.OpenSet

/**
 * Created by ryoichi on 8/14/15.
 */
abstract class FieldEvent {
    val player :Int
}

abstract class DiscardEvent extends FieldEvent {
    val tile :Tile
}

// カンの場合、カンを宣言した場合に発生する。チャンカンで阻止されても、このEventは成立する。
abstract class MeldEvent extends FieldEvent {
    val openSet :OpenSet
}

// リーチ宣言した際に発生する。ロンで阻止されても、このEventは成立する。
abstract class ReachEvent extends FieldEvent

