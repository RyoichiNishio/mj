package com.ryoichi.mahjong.io.model

import scala.collection.mutable


/**
 * Created by ryoichi on 12/29/15.
 */
case class Phase(
	// 東1で0, 東2で1, ...
	var id: Int = 0,
	var honba: Int = 0,
	// 局開始時の供託数
	var initialDeposit: Int = 0,
	// サイコロの目
	var dices: Seq[Int] = Seq(0,0),
	var oya: Int = 0,
	var initialScores : Seq[Int] = Seq(),
	var initialHands : Seq[Seq[Int]] = Seq(),
	//val obverseDoras: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer.empty[Int],
	//var reverseDoras: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer.empty[Int],
	//var actions: mutable.ArrayBuffer[String] = mutable.ArrayBuffer.empty[String],
	var finalDeposit: Int = 0,
	// RYUKYOKU, AGARI, ...
	var result: String = "",
	var finalScores: Seq[Int] = Seq(),
	// これは、リーチ時に供託する出費を含まない。あくまで終局時の点数移動のみ
	var scoreMoves: Seq[Int] = Seq()
)
