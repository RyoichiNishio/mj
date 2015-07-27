import com.ryoichi.mahjong.basis.hand.Hand
import com.ryoichi.mahjong.basis.tile.{Tile, TileKind}
import scala.util.Random
import java.util.Date
/**
 * Created by ryoichi on 7/27/15.
 */
object Main2 {
  val r = new Random((new Date).getTime)
  def main(args: Array[String]): Unit = {
    val results = Range(0,100).toList.map( c => count )
    println(results)
    println(results.reduce(_+_).toDouble/100)
  }

  def tsumo(yama :Seq[Int], hand : Hand, count :Int) :Int = {
    println(hand.fString)
    if ( yama.length == 0 ) return count
    val s = hand.minShanten
    if ( s <= 0 ) count
    else {
      val candidates = hand.candidate
      val discard = candidates(r.nextInt(candidates.length))
      val oldKinds = hand.kinds.remove(discard)
      val newHand = Hand(Tile(yama.head) +: oldKinds.seq.map(k => Tile(k.id * 4)))
      val newCount = count + 1
      tsumo(yama.tail, newHand, newCount)
    }

  }

  def count() :Int = {
    val (haipai, yama) = r.shuffle(Range(0, 133).toList).splitAt(14)
    val hand = Hand(haipai.map( i => Tile(i)))
    val count = 0
    tsumo(yama, hand, count)
  }
}