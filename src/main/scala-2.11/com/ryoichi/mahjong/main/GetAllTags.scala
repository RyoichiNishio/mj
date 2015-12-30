package com.ryoichi.mahjong.main

import java.io.File

import com.ryoichi.mahjong.io.serialization.MjlogSerializer
import com.typesafe.scalalogging.LazyLogging

/**
 * Created by ryoichi on 12/30/15.
 */
object GetAllTags extends LazyLogging {

	def main(args: Array[String]): Unit = {

		val tags = ls4("/Users/ryoichi/Projects/private/mahjong/storage/mjlog/hanchan").map( file => {
			logger.debug(file.toString)
			try {
				MjlogSerializer.getUniqueTags(file)
			} catch {
				case e: org.xml.sax.SAXParseException => {
					logger.info(file.toString + ":" + e.toString)
					Set[String]()
				}
			}
		}).reduce(_++_)

		tags.toSeq.sorted.foreach(println)

		//
	}

	def ls4(dir: String): Seq[File] = {
		def ls(dir: String) : Seq[File] = {
			new File(dir).listFiles.flatMap {
				//case f if f.isDirectory && f.getName >= "20100101" => List[File]()
				case f if f.isDirectory => ls(f.getPath)
				case x => List(x)
			}
		}
		ls(dir).filter( file => {
			file.getParentFile.getName >= "20100510" && file.getPath.endsWith(".mjlog")
		})
	}
}
