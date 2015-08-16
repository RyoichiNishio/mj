package com.ryoichi.mahjong.game

import com.ryoichi.mahjong.basis.hand.{Kantsu, Kotsu, Shuntsu, Mentsu}
import com.ryoichi.mahjong.basis.tile.{TileKind, Tile}
import com.ryoichi.mahjong.exception.SetException

/**
 * Created by ryoichi on 8/14/15.
 */
abstract class OpenSet {
    val from: Int
    val mentsu: Mentsu
    val stolenTile: Option[Tile]
    val disclosedTiles: Set[Tile]
    val addedTile: Option[Tile]

    override def toString(): String = {
        from + mentsu.toString
    }

}


class OpenChow(tile: Tile, val disclosedTiles: Set[Tile]) extends OpenSet {
    if (disclosedTiles.size != 2) throw new SetException("At OpenChow: disclosedTiles.size must be 2.")
    val kinds: Seq[TileKind.Value] = (disclosedTiles + tile).map(t => t.kind).toList.sorted
    if (!(kinds(0).id + 1 == kinds(1).id && kinds(1).id + 1 == kinds(2).id)) throw new SetException("At OpenChow : cannot construct a chow.")
    val mentsu: Mentsu = Shuntsu(kinds.min, true)
    val stolenTile = Option(tile)
    val addedTile = None
    val from = 3
}

class OpenPong(tile: Tile, val disclosedTiles: Set[Tile], val from: Int) extends OpenSet {
    if (disclosedTiles.size != 2) throw new SetException("At OpenPong: disclosedTiles.size must be 2.")
    if (from < 1 || from > 3) throw new SetException("At OpenPong: from must be between 1 and 3")
    val kinds: Seq[TileKind.Value] = (disclosedTiles + tile).map(t => t.kind).toList.sorted
    if (!(kinds(0) == kinds(1) && kinds(1) == kinds(2))) throw new SetException("At OpenPong : cannot construct a pong.")
    val mentsu: Mentsu = Kotsu(kinds.min, true)
    val stolenTile = Option(tile)
    val addedTile = None
}

class ClosedKang(kind: TileKind.Value) extends OpenSet {
    val from = 0
    val mentsu: Mentsu = Kantsu(kind, false)
    val stolenTile = None
    val disclosedTiles = Set(Tile(kind.id * 4), Tile(kind.id * 4 + 1), Tile(kind.id * 4 + 2), Tile(kind.id * 4 + 3))
    val addedTile = None
}

class BigOpenKang(tile: Tile, val from: Int) extends OpenSet {
    if (from < 1 || from > 3) throw new SetException("At BigOpenKang: from must be between 1 and 3")
    val mentsu: Mentsu = Kantsu(tile.kind, true)
    val stolenTile = Option(tile)
    val disclosedTiles = {
        val id = tile.kind.id
        Set(Tile(id * 4), Tile(id * 4 + 1), Tile(id * 4 + 2), Tile(id * 4 + 3)) - tile
    }
    val addedTile = None
}

class SmallOpenKang(tile: Tile, val disclosedTiles: Set[Tile], from: Int) {
    if (from < 1 || from > 3) throw new SetException("At SmallOpenKang: from must be between 1 and 3")
    val mentsu: Mentsu = Kantsu(tile.kind, true)
    val stolenTile = Option(tile)
    val addedTile = {
        val id = (tile.id / 4) * 4 + 6 - (disclosedTiles + tile).map(t => t.id % 4).reduce(_ + _)
        Tile(id)
    }
}
