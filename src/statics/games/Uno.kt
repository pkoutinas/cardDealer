package statics.games

import components.Card
import components.Game
import components.Tile


class Uno(
) : Game {
    override val name: String = "Uno"
    override val minPlayers: Int = 8
    override val maxPlayers: Int = 8
    override val deck: List<UnoCard> = generateDeck()
    override val startHand: Int = 7
    override val roundType: String = "sequential"
    override val initialBoard: List<Tile> = generateInitialBoard()
    override val rules: Any? = null

    override fun generateDeck(): List<UnoCard> {
        val families: Array<String> = arrayOf("Red", "Yellow", "Blue", "Green")
        val cardValues: Array<String> =
            arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Skip", "Reverse", "Draw 2")
        val deck: MutableList<UnoCard> = families.flatMap { f ->
            cardValues.map { v ->
                UnoCard(f, v)
            }
        }.toMutableList()

        deck.addAll(0, families.flatMap { f ->
            cardValues.map { v ->
                UnoCard(f, v)
            }
        })

        deck.addAll(0, List(4) { UnoCard("Wildcard", "Recolour") })
        deck.addAll(0, List(4) { UnoCard("Wildcard", "Draw 4") })

        return deck.toList()
    }

    override fun generateInitialBoard(): List<Tile> {

        return listOf(
        )
    }
}

class UnoCard(v:String, t:String):Card {
    override val value = v
    override val type = t

    override fun toString(): String {
        return "$value of $type"
    }
}