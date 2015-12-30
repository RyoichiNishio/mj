package com.ryoichi.mahjong.io.model

/**
 * Created by ryoichi on 12/29/15.
 */
case class MjGame(
	id: String,
	var mjlogVersion: String = "",
	var shuffleSeed: String = "",
	var shuffleRef: String = "",
	var gameType: Int = 0,
	var lobby: Int = 0,
	var players: Seq[com.ryoichi.mahjong.io.model.Player] = Seq[com.ryoichi.mahjong.io.model.Player](),
	var oya: Int = 0,
	var phases: Seq[com.ryoichi.mahjong.io.model.Phase] = Seq[com.ryoichi.mahjong.io.model.Phase]()
)
