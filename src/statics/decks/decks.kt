package statics.decks

import components.Card

fun getClassic(): List<Card> {
    val families: Array<String> = arrayOf("spades", "clubs", "diamonds", "hearts")
    val cardValues: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "D", "K")

    return families.flatMap { f ->
            cardValues.map { v ->
                Card(family = f, value = v)
            }
        }
}

fun getUno(): List<Card> {
    val families: Array<String> = arrayOf("Red", "Yellow", "Blue", "Green")
    val cardValues: Array<String> = arrayOf("0","1", "2", "3", "4", "5", "6", "7", "8", "9", "Skip", "Reverse", "Draw 2")
    val deck: MutableList<Card> = families.flatMap { f ->
        cardValues.map { v ->
            Card(family = f, value = v)
        }
    }.toMutableList()

    deck.addAll(0, families.flatMap { f ->
        cardValues.map { v ->
            Card(family = f, value = v)
        }
    })

    deck.addAll(0, List(4) { Card("Wildcard", "Recolour")})
    deck.addAll(0, List(4) { Card("Wildcard", "Draw 4")})

    println(deck.size)

    return deck
}
