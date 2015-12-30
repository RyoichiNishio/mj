package com.ryoichi.mahjong.main

import java.io.File
import com.ryoichi.mahjong.io.serialization.MjlogSerializer
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable.ArrayBuffer
import scala.xml._
import com.ryoichi.mahjong.util.JsonProtocol._
import spray.json._


/**
 * Created by ryoichi on 12/29/15.
 */

object DeserializeMjlog extends LazyLogging {

	def main(args: Array[String]): Unit = {

		val file = new File("/Users/ryoichi/Projects/private/mahjong/storage/tmp/test.mjlog")
		val game = MjlogSerializer.deserialize(file)
		logger.warn(game.toJson.toString)
		
	}
}
