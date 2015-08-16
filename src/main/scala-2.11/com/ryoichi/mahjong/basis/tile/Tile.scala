package com.ryoichi.mahjong.basis.tile

import com.ryoichi.mahjong.exception.TileException

/**
 * Created by ryoichi on 7/15/15.
 */
case class Tile(id: Int) extends Ordered[Tile] {
  if (!(id >= 0 && id <= 135)) { throw new TileException("Tile ID must be between 0 and 135") }
  val kind = TileKind(id/4)

  override def compare(that: Tile): Int = {
    this.id.compare(that.id)
  }

  override def toString = {
    this.id + ":" + kind
  }
}
