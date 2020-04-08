package statics.games

import components.*
import java.util.*
import kotlin.reflect.typeOf


@ExperimentalStdlibApi
class Uno : Game {
    override val name: String = "uno"
    override val properties: Map<String, Any> = mapOf(
        Pair("minPlayers", 2),
        Pair("maxPlayers", 8),
        Pair("startHand", 7),
        Pair("roundType", "sequential")
    )
    override val rules: Any? = null
    // shedding game
    // can only shed to 'discard' tile
    // can shed as many cards as applicable
    //  - same family
    //  - same value
    //  - wildcard
    // if no card shed, then draw from 'draw' pile
    // cannot draw if card shed (even if player 'could' have shed a card)
    // Turn start condition: draw X cards from previous draw cards >> maybe we could have an extra pile for this in Uno
    // Turn end conditions:
    //  - player has 0 cards left in hand
    //  - player has drawn
    //  - player has shed at least one card and 'ends round'
    // Game end:
    //  - player has 0 cards in hand

    override var deck: Deck = UnoDeck()
    override val rounds: Stack<Round> = Stack()
    override lateinit var playerOrder: Array<Player>

    override fun dealRoundZero(candidates: List<Player>): Round {
        //First set the player order
        playerOrder = setPlayerOrder(candidates)

        val resultOfDeal: Pair<List<Card>, List<Hand>> = generateInitialHands(deck.cards)
        // Then deal to players
        val hands: List<Hand> = resultOfDeal.second

        // Finally place rest of deck on board
        val board: Board = UnoBoard(resultOfDeal.first)

        return Round(0, null, board, hands)
    }

    override fun endTurn(newBoard: Board, newHands: List<Hand>): Round? {
        val newRound = Round((getLatestRound()?.index ?:-1) + 1, getCurrentPlayer().id, newBoard, newHands)
        if (true) {
            return null
        }
        return newRound
    }

    fun reversePlayerOrder() {
        playerOrder.reverse()
    }
}

class UnoBoard(deckForBoard: List<Card>) : Board {
    override val name: String = "uno"
    override val tiles: List<Tile> = generateBoard(deckForBoard)

    override fun generateBoard(deckForBoard: List<Card>): List<Tile> {
        val draw: Pair<Card?, List<Card>> = drawTop(deckForBoard)
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
                content = if (draw.first == null) listOf() else listOf(draw.first as Card),
                cardTypeRestriction = null,
                cardValueRestriction = null
            )
        )
    }
}

class UnoDeck : Deck {
    override val name: String = "uno"
    override val cards: List<Card> = generateDeck()

    override fun generateDeck(): List<Card> {
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
}

class UnoCard(v: String, t: String) : Card {
    override val uid: UUID = UUID.randomUUID()
    override val value = v
    override val type = t

    override fun toString(): String {
        return "($uid) $type $value"
    }
}