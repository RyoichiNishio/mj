package com.ryoichi.mahjong.game

import com.ryoichi.mahjong.basis.tile.Tile

/**
 * Created by ryoichi on 8/14/15.
 */


sealed abstract class Reaction

case class Skip() extends Reaction

case class WinByDiscard() extends Reaction

case class Pong(disclosedTiles: Set[Tile]) extends Reaction

case class Chow(disclosedTiles: Set[Tile]) extends Reaction

case class DeclareBigOpenKang() extends Reaction