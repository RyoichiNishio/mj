import com.ryoichi.mahjong.basis.hand.{Node, Part, Hand}
import com.ryoichi.mahjong.basis.tile.{Tile, TileKind}
import com.typesafe.scalalogging.LazyLogging

/**
 * Created by ryoichi on 7/15/15.
 */
object Main extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("hello")
    val handKinds = Seq(TileKind.M6,TileKind.M6,TileKind.S4,TileKind.S4,TileKind.S4,TileKind.S4,TileKind.S8,TileKind.S9,TileKind.P5,TileKind.P6,TileKind.P7,TileKind.P7,TileKind.P8)
    val x = handKinds.map( kind => Tile(kind.id*4) )
    val hand = new Hand(handKinds.map( kind => Tile(kind.id*4) ))
    println(hand.fString)
    //hand.minShantenForms.foreach(println)
    //println(hand.minShanten)
    println(hand.acceptableKinds)
    println("END")
  }
}