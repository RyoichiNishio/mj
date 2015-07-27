import com.ryoichi.mahjong.basis.hand.Hand
import com.ryoichi.mahjong.basis.tile.{Tile, TileKind}
import scala.util.Random
import java.util.Date
/**
 * Created by ryoichi on 7/27/15.
 */
object Main2 {
  def main(args: Array[String]): Unit = {
    val r = new Random((new Date).getTime)
    val yama = r.shuffle(Range(0, 133).toList)
    val (haipai, tsumo) = yama.splitAt(14)
    var hand = Hand(haipai.map( i => Tile(i)))
    var count = 0
    tsumo.foreach(t => {
      val s = hand.minShanten
      println(hand.fString + " : " + s)
      if (s <= 0) {println("TENPAI!!" + " : " + count); sys.exit(0)}
      val candidates = hand.candidate
      println(candidates)
      val discard = candidates(r.nextInt(candidates.length))
      val oldKinds = hand.kinds.remove(discard)
      hand = Hand(Tile(t) +: oldKinds.seq.map(k => Tile(k.id*4)) )
      count += 1
    })



  }
}