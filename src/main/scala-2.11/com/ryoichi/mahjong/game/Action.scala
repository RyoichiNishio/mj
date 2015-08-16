package com.ryoichi.mahjong.game

import com.ryoichi.mahjong.basis.tile.{Tile, TileKind}

/**
 * Created by ryoichi on 8/14/15.
 */

sealed abstract class Action

case class Discard(tile: Tile) extends Action

case class ReachDeclaration() extends Action

case class DeclareClosedKang(kind: TileKind.Value) extends Action

case class WinByTsumo() extends Action

case class DeclareSmallOpenKang(tile: Tile) extends Action

case class KyushuKyuhai() extends Action
