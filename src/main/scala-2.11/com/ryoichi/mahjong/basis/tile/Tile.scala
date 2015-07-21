package com.ryoichi.mahjong.basis.tile

/**
 * Created by ryoichi on 7/15/15.
 */
case class Tile(id: Int) extends Ordered[Tile] {
  if (!(id >= 0 && id <= 135)) { throw new RuntimeException }
  val kind = TileKind(id/4)

  override def compare(that: Tile): Int = {
    this.id.compare(that.id)
  }

  override def toString = {
    this.id + ":" + kind
  }
}
