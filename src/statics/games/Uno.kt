package statics.games

import components.*
import java.util.*


class Uno(
    override val players: Array<Player>
) : Game {
    override val name: String = "Uno"
    override val minPlayers: Int = 2
    override val maxPlayers: Int = 8
    override val startHand: Int = 7
    override val roundType: String = "sequential"
    override val rules: Any? = null

    override var deck: MutableList<out Card> = generateDeck()
    override val rounds: MutableList<Round> = mutableListOf()
    override var playerOrder: Array<Player> = setPlayerOrder(players)

    override fun generateDeck(): MutableList<out Card> {
        val families: Array<String> = arrayOf("Red", "Yellow", "Blue", "Green")
        val cardValues: Array<String> =
            arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Skip", "Reverse", "Draw 2")
        val deck: MutableList<UnoCard> = families.flatMap { f ->
            cardValues.map { v ->
                UnoCard(f, v)
            }
        }.toMutableList()         // First create base deck combination
        deck.addAll(families.flatMap { f ->
            cardValues.map { v ->
                UnoCard(f, v)
            }
        }.toMutableList())         // Duplicate it
        deck.addAll(List(4) { UnoCard("Wildcard", "Recolour") })
        deck.addAll(List(4) { UnoCard("Wildcard", "Draw 4") })

        // Shuffle
        deck.sortBy { it.uid }

        return deck
    }

    override fun generateInitialBoard(): List<Tile> {
        val draw: Pair<Card?, MutableList<out Card>> = drawTop(deck)
        return listOf(
            Tile(
                type = "draw",
                owner = null,
                pileType = "overlap",
                visible = null,
                category = "draw",
                content = draw.second,
                cardTypeRestriction = null,
                cardValueRestriction = null
            ),
            Tile(
                type = "discard",
                owner = null,
                pileType = "overlap",
                visible = "all",
                category = "discard",
                content = mutableListOf(draw.first),
                cardTypeRestriction = null,
                cardValueRestriction = null
            )
        )
    }

    fun reversePlayerOrder() {
        playerOrder.reverse()
    }
}

class UnoCard(v: String, t: String) : Card {
    override val uid: UUID = UUID.randomUUID()
    override val value = v
    override val type = t

    override fun toString(): String {
        return "($uid) $type $value"
    }
}