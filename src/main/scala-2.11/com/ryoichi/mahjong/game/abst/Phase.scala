package com.ryoichi.mahjong.game.abst

import com.ryoichi.mahjong.game.{Reaction, ReactionPhaseContext, ActionPhaseContext, Action}

/**
 * Created by ryoichi on 8/14/15.
 */
abstract class Phase {
    val field: Field
    val players: Seq[Player]
    val wall: Wall
    val turnPlayer: Int
}

abstract class ActionPhase extends Phase {
    val phaseContext: ActionPhaseContext
    def possibleActions: Seq[Action]
    def nextPhase(action: Action): Phase
}

abstract class ReactionPhase extends Phase {
    val phaseContext: ReactionPhaseContext
    def possibleActions(player: Int): Seq[Reaction]
    def nextPhase(reactions: Seq[Reaction]): Phase
}
