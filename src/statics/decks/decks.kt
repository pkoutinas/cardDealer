package statics.decks

import components.Card

fun getClassic(): List<Card> {
    val families: Array<String> = arrayOf("spades", "clubs", "diamonds", "hearts")
    val cardValues: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "D", "K")

    return families.flatMap { f ->
        cardValues.map { v ->
            ClassicCard(f, v)
        }
    }
}


class ClassicCard(v: String, t: String) : Card {
    override val value = v
    override val type = t

    override fun toString(): String {
        return "$value of $type"
    }
}