package com.ryoichi.mahjong.game

/**
 * Created by ryoichi on 8/14/15.
 */

sealed abstract class PhaseContext

sealed abstract class ActionPhaseContext

sealed abstract class ReactionPhaseContext

case object AfterOpenKang extends ActionPhaseContext

case object AfterClosedKang extends ActionPhaseContext

case object AfterPongOrChow extends ActionPhaseContext

case object AfterTsumo extends ActionPhaseContext

case object AfterClosedKangDeclaration extends ReactionPhaseContext

case object AfterSmallOpenKangDeclaration extends ReactionPhaseContext

case object AfterDiscard extends ReactionPhaseContext
