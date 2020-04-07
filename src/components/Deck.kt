package components

import java.util.*

interface Deck {
    val name:String
    val cards:List<Card>
    fun generateDeck(): List<Card>
}

interface Card {
    val uid: UUID
    val value: String
    val type: String

    fun sameType(otherCard: Card): Boolean {
        return this.type == otherCard.type
    }

    fun sameValue(otherCard: Card): Boolean {
        return this.value == otherCard.value
    }
}

interface SequentialCard:Card {
    val loops:Boolean
    val partOfSequence:Boolean
    val index:Int

    fun isGreaterThan(otherCard: SequentialCard):Boolean {
        return this.index > otherCard.index
    }

    fun isJustGreaterThan(otherCard: SequentialCard):Boolean {
        return this.index == otherCard.index + 1
    }

    fun isSmallerThan(otherCard: SequentialCard):Boolean {
        return this.index < otherCard.index
    }

    fun isJustSmallerThan(otherCard: SequentialCard):Boolean {
        return this.index == otherCard.index - 1
    }
}