package statics.decks

import components.Card
import components.Deck
import components.SequentialCard
import java.util.*

class Classic52 : Deck {
    override val name: String = "classic"
    override val cards: List<Card> = generateDeck()

    override fun generateDeck(): List<Card> {

        val families: Array<String> = arrayOf("spades", "clubs", "diamonds", "hearts")
        val cardValues: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "D", "K")

        return families.flatMap { f ->
            cardValues.map { v ->
                ClassicCard(value = v, type = f, loops = false, partOfSequence = true, index = cardValues.indexOf(v))
            }
        }
    }
}


class ClassicCard(
    override val uid: UUID = UUID.randomUUID(),
    override val value: String,
    override val type: String,
    override val loops: Boolean,
    override val partOfSequence: Boolean,
    override val index: Int
) : SequentialCard {
    override fun toString(): String {
        return "($uid) $value of $type"
    }
}