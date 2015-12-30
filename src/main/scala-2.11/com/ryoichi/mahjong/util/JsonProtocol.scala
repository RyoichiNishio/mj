package com.ryoichi.mahjong.util

import com.ryoichi.mahjong.io.model._
import spray.json.DefaultJsonProtocol

/**
 * Created by ryoichi on 12/29/15.
 */

object JsonProtocol extends DefaultJsonProtocol {

	implicit val playerFormat = jsonFormat4(Player)
	implicit val phaseFormat = jsonFormat11(Phase)
	implicit val mjGameFormat = jsonFormat9(MjGame)

}

