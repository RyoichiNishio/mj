package com.ryoichi.mahjong.game

/**
 * Created by ryoichi on 8/14/15.
 */
case class Round(
    wind: Int,
    id: Int,
    honba: Int
) {
    def this() = this(0,1,0)
}

