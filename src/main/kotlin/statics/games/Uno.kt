package statics.games

import components.*
import java.util.*


@ExperimentalStdlibApi
class Uno : Game {
    override val name: String = "uno"
    override val properties: Map<String, Any> = mapOf(
        Pair("minPlayers", 2),
        Pair("maxPlayers", 8),
        Pair("startHand", 7),
        Pair("roundType", "sequential")
    )
    override val rules: RuleSet = UnoRules()

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
        val newRound = Round((getLatestRound()?.index ?: -1) + 1, getCurrentPlayer().id, newBoard, newHands)
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
                name = "draw",
                owner = null,
                pileType = "overlap",
                visible = null,
                category = "draw",
                content = draw.second,
                cardTypeRestriction = null,
                cardValueRestriction = null
            ),
            Tile(
                name = "discard",
                owner = null,
                pileType = "overlap",
                visible = "all",
                category = "discard",
                content = if (draw.first == null) listOf() else listOf(draw.first as Card),
                cardTypeRestriction = null,
                cardValueRestriction = null
            ),
            Tile(
                name = "plus",
                owner = null,
                pileType = "overlap",
                visible = null,
                category = "draw",
                content = listOf(),
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
        val types: Array<String> = arrayOf("Red", "Yellow", "Blue", "Green")
        val values: Array<String> =
            arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Skip", "Reverse", "Draw 2")
        val deck: MutableList<UnoCard> = types.flatMap { f ->
            values.map { v ->
                UnoCard(f, v)
            }
        }.toMutableList()         // First create base deck combination
        deck.addAll(types.flatMap { f ->
            values.map { v ->
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

class UnoRules : RuleSet {
    /**
     * The [Game] ends when the current [Player] has no [Card]s left in [Hand].
     * @param \params contains only one [Hand] parameter and checks if empty.
     */
    private val gameEnd: (List<Any>) -> Boolean = { params -> (params[0] as Hand).cards.isEmpty() }

    /**
     * A [Round] can end when:
     *  - player has 0 cards left in [Hand] (game would need to check for gameEnd first)
     *  - player has drawn
     *  - player has shed at least one card
     *  - there are no [Card]s in the 'plus' pile
     *  @param \params contains:
     *  - the [Player]'s previous [Round]'s [Hand] and checks if it is larger or smaller
     *  - the [Player]'s current [Hand]
     *  - the current 'plus' [Tile]
     */
    private val playerHasPlayed: (List<Any>) -> Boolean =
        { params -> (params[0] as Hand).cards.size != (params[1] as Hand).cards.size }

    private val noCardsForPickUp: (List<Any>) -> Boolean =
        { params -> (params[2] as Tile).content.isEmpty() }

    /**
     * A [Player] can shed if the [Player] hasn't drawn from the 'draw' pile
     * @param \params contains:
     *  - the previous [Round]'s 'draw' Tile
     *  - the current [Player]'s [Hand]
     */
    private val hasNotDrawn: (List<Any>) -> Boolean =
        { params -> !(params[1] as Hand).cards.contains((params[0] as Tile).content.last()) }

    /**
     * A [Card] can be shed if it is a wildcard or if the [Card] at the top of the 'discard' pile is:
     *  - of the same type
     *  - of the same value
     * @param \params contains:
     *  - the [Card] at the top of the 'discard' pile
     *  - the [Card] to be shed
     */
    private val cardIsSheddable: (List<Any>) -> Boolean =
        { params ->
            (params[0] as Card).sameType(params[1] as Card) ||
                    (params[0] as Card).sameValue(params[1] as Card)
        }
    private val cardIsWildcard: (List<Any>) -> Boolean =
        { params -> (params[2] as Card).type == "wildcard" }

    /**
     * A [Player] can draw if
     *  - the [Player] hasn't drawn from the 'draw' pile yet
     *  - the 'plus' pile was empty (and nothing was taken in this round)
     */

    /**
     * A valid first draw is any non-wildcard [Card]
     * @param \params contains:
     *  - the 'draw' pile
     */
    private val topIsNotWildcard: (List<Any>) -> Boolean =
        { params -> (params[0] as Tile).content.last().type != "wildcard" }

    override
    val name: String = "uno"
    override val rules: List<Rule> = listOf(
        Rule("gameEnd", listOf(gameEnd)),
        Rule("turnEnd", listOf(playerHasPlayed, noCardsForPickUp)),
        Rule("canShed", listOf(hasNotDrawn)),
        Rule("canDraw", listOf(hasNotDrawn, noCardsForPickUp)),
        Rule("cardIsSheddable", listOf(cardIsSheddable, cardIsWildcard)),
        Rule("validFirstDraw", listOf(topIsNotWildcard))
    )
}