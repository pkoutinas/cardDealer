package components

import java.util.UUID

/**
 * [Game] interface has to be overridden for every game type
 */
interface Game {

    /**
     * [Array] of [Player]s in order of play.
     */
    var playerOrder: Array<Player>

    /**
     * Name of the game used for identification (i.e. not for display)
     */
    val name: String

    /**
     * [Deck] to be used in this session.
     */
    val deck: Deck

    /**
     * List of core properties applicable to this [Game] type, structured as a [Map] where the key is the property name as
     * a [String] and the value is the property value in any primitive type.
     * Must always include minPlayers (int), maxPlayers (int), startingHand (int), roundType (String).
     */
    val properties: Map<String, Any>

    val rules: Any?             // TODO: how?

    /**
     * [MutableList] containing all the [Round]s that have already taken place.
     * This is the 'working document' of the game application.
     */
    val rounds: MutableList<Round>

    /**
     * Checks if the game settings meet the minimum starting properties
     * @return [Boolean]
     */
    fun canStartGame(candidates: List<Player>): Boolean {
        return (candidates.size >= properties["minPlayers"] as Int && candidates.size <= properties["maxPlayers"] as Int )
    }

    /**
     * Sets the default playing order based on the [players] provided.
     * Default implementation returns the same as the input parameter.
     * @param [players] is an [Array] of [Player]s to be used in this game.
     * @return [Array] of [Player]s in the order of play.
     */
    fun setPlayerOrder(candidates: List<Player>): Array<Player> {
        return candidates.toTypedArray()
    }

    /**
     * Used to start the game by dealing the appropriate [Card]s to the [Player]s via [generateInitialHands]
     * and then setting up the board with the remaining [Card]s. Must be overridden.
     * This results in a new starting [Round] being returned.
     * @param [candidates] is the [List] of [Player]s to be added to this game
     * @return [Round] with [Round.index] = 0 and [Round.playerId] = null, along with the newly created board and hands
     */
    fun dealRoundZero(candidates: List<Player>): Round

    /**
     * Distributes the appropriate [Card]s to all [Player]s by order of play and returns the remaining deck and a
     * list of [Hand]s in a [Pair].
     * @param [deckForHands] is the [List] of [Card]s to be used for dealing to [Player]s
     * @return [Pair] of:
     *  - The remaining deck of cards as an [List] of [Card]s
     *  - [List] of [Hand]s.
     */
    fun generateInitialHands(deckForHands: List<Card>): Pair<List<Card>, List<Hand>> {
        val hands: MutableList<Hand> = mutableListOf()
        var remainingDeck: List<Card> = deckForHands.toList()

        for (i in playerOrder.indices) {
            val parts: Pair<List<Card>, List<Card>> =
                remainingDeck.partition { card -> remainingDeck.indexOf(card) < properties["startHand"] as Int }
            hands.add(Hand(playerOrder[i], parts.first))
            remainingDeck = parts.second
        }
        return Pair(remainingDeck, hands)
    }

    fun getCurrentBoard(): Board {
        return rounds.last().board
    }

    fun getLatestRound(): Round? {
        return rounds.lastOrNull()
    }

    /**
     * Returns the [Player] that is currently 'playing' (i.e. that hasn't made any moves).
     * In the opening round, the [Player] returned will be the first one in the [playerOrder].
     * By default, the order of play is sequential and repeats.
     * @return [Player] currently due for action
     */
    fun getCurrentPlayer(): Player {
        val lastId = getLatestRound()?.playerId
        if (lastId == null || lastId == playerOrder.last().id) {
            return playerOrder[0]
        }
        return playerOrder[playerOrder.indexOfFirst { player -> player.id == lastId } + 1]
    }

    /**
     *  Triggers the end of a turn by storing the last completed [Round].
     *  @param [newBoard] is the validated [Board] following the current's player's moves.
     *  @param [newHands] is a [List] of [Hand]s as created by [generateInitialHands] and stored in [rounds].
     *  @return Next [Player] that is due to play or null if the game is over
     */
    fun endTurn(newBoard: Board, newHands: List<Hand>): Round?
}

data class Round(
    val index: Int,           // Must increment at every player round, overall round is mod(# of players)
    val playerId: UUID?,
    val board: Board,
    val hands: List<Hand>
) {
    fun getOverallCurrentRound(numberOfPlayer: Int): Int {
        if (numberOfPlayer == 0) {
            return 0
        }
        return (index / numberOfPlayer)
    }
}

data class Hand(
    val player: Player,
    val cards: List<Card>
)

data class Player(
    val name: String,
    val visibleHand: Boolean = false,
    val id: UUID = UUID.randomUUID()
)