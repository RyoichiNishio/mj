package com.ryoichi.mahjong.game.impl

import com.ryoichi.mahjong.basis.tile.Tile
import com.ryoichi.mahjong.game.GameType.GameType
import com.ryoichi.mahjong.game._
import com.ryoichi.mahjong.game.abst._

/**
 * Created by ryoichi on 8/15/15.
 */

class DefaultActionPhase(
    val field: Field,
    val players: Seq[Player],
    val wall: Wall,
    val turnPlayer: Int,
    val phaseContext: ActionPhaseContext
) extends ActionPhase {
    override def possibleActions: Seq[Action] = Seq()
    override def nextPhase(action: Action): Phase = null
}


class DefaultReactionPhase(
    val field: Field,
    val players: Seq[Player],
    val wall: Wall,
    val turnPlayer: Int,
    val phaseContext: ReactionPhaseContext
) extends ReactionPhase {
    override def possibleActions(player: Int): Seq[Reaction] = Seq()
    override def nextPhase(reactions: Seq[Reaction]): Phase = null
}

object DefaultPhase {
    def startNewGame(seed: Long, gameType: GameType): ActionPhase = {
        val (hands: Seq[Seq[Int]], wall: Wall) = Wall.createAtRandom(seed).haipai
        val field = DefaultField(gameType, Seq(2500, 2500, 2500, 2500), new Round(), 0, Seq[FieldEvent](), Seq(Tile(wall.doraIndicatingTile)), true, 0)
        val hands2 :Seq[Set[Int]] = hands.map(seq => seq.toSet)
        val hands3: Seq[Set[Tile]] = hands2.map(set => set.map(i => Tile(i)))
        val players: Seq[Player] = hands3.map( x => new DefaultPlayer(x))
        val turnPlayer = 0
        val phaseContext = AfterTsumo
        new DefaultActionPhase(field, players, wall, turnPlayer, phaseContext)
    }
}