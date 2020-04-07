package components

import java.util.UUID

/**
 * [Game] interface has to be overridden for every game type
 */
interface Game {
    /**
     * [Array] of all [Player]s added to the game, in order of joining the session.
     */
    val players: Array<Player>

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
    var deck: Deck

    /**
     * Minimum number of players required to start a game (inclusive).
     */
    val minPlayers: Int

    /**
     * Maximum number of players required to start a game (inclusive).
     */
    val maxPlayers: Int

    /**
     * Number of cards each player will receive at the beginning of the game.
     */
    val startHand: Int

    /**
     * Describes how game rounds are taking place. Possible values are: sequential, simultaneous and reflex.
     */
    val roundType: String

    /**
     * [MutableList] containing all the [Round]s that have already taken place.
     * This is the 'working document' of the game application.
     */
    val rounds: MutableList<Round>

    val rules: Any?             // TODO: how?

    /**
     * Sets the default playing order based on the [players] provided.
     * Default implementation returns the same as the input parameter.
     * @param [players] is an [Array] of [Player]s to be used in this game.
     * @return [Array] of [Player]s in the order of play.
     */
    fun setPlayerOrder(players: Array<Player>): Array<Player> {
        return players
    }

    /**
     * Used to start the game by dealing the appropriate [Card]s to the [Player]s via [generateInitialHands]
     * and then setting up the board with the remaining [Card]s. Must be overridden.
     * This results in a new starting [Round] being returned.
     * @return [Round] with [Round.index] = 0 and [Round.playerId] = null, along with the newly created board and hands
     */
    fun dealRoundZero(): Round

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
                remainingDeck.partition { card -> remainingDeck.indexOf(card) < startHand }
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
     *  Triggers the end of a turn by performing the following actions:
     *   - Check if the game end conditions have been met by calling [isGameOver]
     *   - If the game is over, return null otherwise return the completed [Round]
     *  Note that [endTurn] does not check for the board or hand correctness.
     *  @param [newBoard] is the validated [Board] following the current's player's moves.
     *  @param [newHands] is a [List] of [Hand]s as created by [generateInitialHands] and stored in [rounds].
     *  @return Next [Player] that is due to play or null if the game is over
     */
    fun endTurn(newBoard: Board, newHands: List<Hand>): Round? {
        val newRound = Round((getLatestRound()?.index ?:-1) + 1, getCurrentPlayer().id, newBoard, newHands)
        if (isGameOver(newRound)) {
            return null
        }
        return newRound
    }

    /**
     * Checks if the [finalRound] provided satisfies the [Game]'s end conditions (must be overridden).
     * @param [finalRound] is the [Round] to be checked
     * @return [Boolean] returned is true if the conditions are met, false if they are not.
     */
    fun isGameOver(finalRound: Round): Boolean
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