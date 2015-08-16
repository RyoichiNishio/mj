package com.ryoichi.mahjong.basis.hand

import com.ryoichi.mahjong.basis.tile.TileKind

/**
 * Created by ryoichi on 7/16/15.
 */
class ReadyHand(unorderedParts: Seq[Part]) {
    val parts = unorderedParts.sorted
    val category = {
        val nMentsu = parts.count(p => p.isMentsu)
        val nHead = parts.count(p => p.isToitsu)
        val nTanki = parts.count(p => p.isUki)
        val nTaatsu = parts.count(p => p.isTaatsu)
        (nMentsu, nHead, nTanki, nTaatsu) match {
            case (4, 0, 1, 0) => ReadyCategory.TANKI
            case (3, 2, 0, 0) => ReadyCategory.SHABO
            case (3, 1, 0, 1) => {
                val nPenchan = parts.count(p => p.isPenchan)
                val nKanchan = parts.count(p => p.isKanchan)
                val nRyanmen = parts.count(p => p.isRyanmen)
                (nPenchan, nKanchan, nRyanmen) match {
                    case (1, 0, 0) => ReadyCategory.PENCHAN
                    case (0, 1, 0) => ReadyCategory.KANCHAN
                    case (0, 0, 1) => ReadyCategory.RYANMEN
                    case _ => throw new RuntimeException
                }
            }
            case _ => throw new RuntimeException
        }
    }
    val winningTiles: Set[TileKind.Value] = {
        category match {
            case ReadyCategory.RYANMEN => parts.filter(p => p.isRyanmen).flatMap(p => {
                val kind = p.kind
                val kind2 = TileKind(kind.id + 3)
                Set(kind, kind2)
            }).toSet
            case ReadyCategory.TANKI => parts.filter(p => p.isUki).map(p => p.kind).toSet
            case ReadyCategory.SHABO => parts.filter(p => p.isToitsu).map(p => p.kind).toSet
            case ReadyCategory.PENCHAN => parts.filter(p => p.isPenchan).map(p => p.kind).toSet
            case ReadyCategory.KANCHAN => parts.filter(p => p.isKanchan).map(p => p.kind).toSet
        }
    }

}

