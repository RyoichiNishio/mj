import com.ryoichi.mahjong.basis.hand.{Node, Part, Hand}
import com.ryoichi.mahjong.basis.tile.{Tile, TileKind}

/**
 * Created by ryoichi on 7/15/15.
 */
object Main {
  def main(args: Array[String]): Unit = {
    val handKinds = Seq(TileKind.M1,TileKind.M1,TileKind.M1,TileKind.M2,TileKind.M3,TileKind.M4,TileKind.M5,TileKind.M6,TileKind.M7,TileKind.M8,TileKind.M9,TileKind.M9,TileKind.M9)
    val x = handKinds.map( kind => Tile(kind.id*4) )
    val hand = new Hand(handKinds.map( kind => Tile(kind.id*4) ))
    println(hand.fString)
    hand.getTenpai.foreach( rh =>{print(rh.parts); println(rh.category);})
  }
}