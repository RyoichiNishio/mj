package com.ryoichi.mahjong.io.serialization

import java.io.File

import com.ryoichi.mahjong.io.model.{Phase, Player, MjGame}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable
import scala.xml.{Node, XML, Elem}

/**
 * Created by ryoichi on 12/29/15.
 */
object MjlogSerializer extends LazyLogging {

	def deserialize(mjlogFile: File): MjGame = {
		val id = mjlogFile.getName
		val game = MjGame(id)
		val xml: Elem = XML.loadFile(mjlogFile)
		setGameFromXml(game, xml)
		game
	}

	def getUniqueTags(mjlogFile: File): Set[String] = {
		val xml: Elem = XML.loadFile(mjlogFile)
		xml.child.map(_.label).filter( label => !label.matches("""(D|E|F|G|T|U|V|W)([0-9]|[1-9][0-9]|1[0-2][0-9]|13[0-5])""")).toSet
	}

	private def setGameFromXml(game: MjGame, xml: Elem): Unit = {
		game.mjlogVersion = (xml \ "@ver").text
		val phaseNodesBuffer: mutable.Buffer[Node] = mutable.Buffer.empty[Node]
		xml.child.foreach(node => {
			val tag: String = node.label
			val attrs: Map[String, String] = node.attributes.asAttrMap
			tag match {
				case "SHUFFLE" => {
					game.shuffleSeed = attrs("seed")
					game.shuffleRef = attrs("ref")
				}
				case "GO" => {
					game.gameType = attrs("type").toInt
					game.lobby = attrs("lobby").toInt
				}
				case "UN" => {
					val players = Seq[Player](Player(attrs("n0")), Player(attrs("n1")), Player(attrs("n2")), Player(attrs("n3")))
					val dans: Array[Int] = attrs("dan").split(",").map(dan => dan.toInt)
					val rates: Array[Double] = attrs("rate").split(",").map(rate => rate.toDouble)
					val sexes: Array[String] = attrs("sx").split(",")
					players.zipWithIndex.map(pair => {
						val (player: Player, index: Int) = pair
						player.dan = dans(index)
						player.rate = rates(index)
						player.sex = sexes(index)
					})
					game.players = players
				}
				case "TAIKYOKU" => {
					game.oya = attrs("oya").toInt
				}
				case _ => {
					phaseNodesBuffer += node
				}
			}
		})
		val phaseNodes = phaseNodesBuffer.toSeq
		val phases: Seq[Phase] = createPhasesFromNodes(phaseNodes)
		game.phases = phases
	}

	private def createPhasesFromNodes(phaseNodes: Seq[Node]): Seq[Phase] = {
		val splittedPhaseNodes: Seq[Seq[Node]] = splitPhaseNodes(phaseNodes)
		splittedPhaseNodes.map(singlePhaseNodes => {
			buildPhase(singlePhaseNodes)
		})
	}

	private def splitPhaseNodes(phaseNodes: Seq[Node]): Seq[Seq[Node]] = {
		val result = mutable.ListBuffer.empty[Seq[Node]]
		var singlePhaseNodes = mutable.ListBuffer.empty[Node]
		phaseNodes.foreach(node => {
			val tag: String = node.label
			if (tag == "INIT") {
				result += singlePhaseNodes.toSeq
				singlePhaseNodes = mutable.ListBuffer.empty[Node]
			}
			singlePhaseNodes += node
		})
		result += singlePhaseNodes.toSeq
		result.takeRight(result.length - 1)
	}

	private def buildPhase(singlePhaseNodes: Seq[Node]): Phase = {
//		val phase = {
//			val phase = Phase()
//			val initNode = singlePhaseNodes.head
//			val attrs = initNode.attributes.asAttrMap
//			phase.initialScores = attrs("ten").split(",").map(_.toInt)
//			phase.oya = attrs("oya").toInt
//			phase.initialHands = {
//				val hai0: Seq[Int] = attrs("hai0").split(",").map(_.toInt)
//				val hai1: Seq[Int] = attrs("hai1").split(",").map(_.toInt)
//				val hai2: Seq[Int] = attrs("hai2").split(",").map(_.toInt)
//				val hai3: Seq[Int] = attrs("hai3").split(",").map(_.toInt)
//				Seq(hai0, hai1, hai2, hai3)
//			}
//			val seeds = attrs("seed").split(",").map(_.toInt)
//			phase.id = seeds(0)
//			phase.honba = seeds(1)
//			phase.initialDeposit = seeds(2)
//			phase.dices = Seq(seeds(3), seeds(4))
//			phase.obverseDoras += seeds(5)
//			phase
//		}
		Phase()
	}
}
