package components

import java.util.UUID

/**
 * [Game] interface has to be overridden for every game type.
 * All class properties must be initialised and the following functions must be implemented:
 *  - [generateDeck] to create the game deck
 *  - [generateInitialBoard] to set the initial game board
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
     * [Array] of all [Card]s to be used in this session. [deck] is initialised by the overridden method
     * [generateDeck].
     */
    var deck: Array<Card>

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
     * Generate Deck (must be overridden)
     * @return [Array] of [Card]s corresponding to a given game type.
     */
    fun generateDeck(): Array<Card>

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
     * and then setting up the board via [generateInitialBoard] with the remaining [Card]s.
     * Must be overridden if the order of execution must change.
     * This results in a new starting [Round] being returned.
     * @return [Round] with [Round.index] = 0 and [Round.playerId] = null, along with the newly created board and hands
     */
    fun dealRoundZero(): Round {
        val resultOfDeal: Pair<Array<Card>, Map<Player, Array<Card>>> = generateInitialHands(deck)
        // First deal to players
        val hands: Map<Player, Array<Card>> = resultOfDeal.second
        // Place rest on board
        val board: List<Tile> = generateInitialBoard(resultOfDeal.first)

        return Round(0, null, board, hands)
    }

    /**
     * Distributes the appropriate [Card]s to all [Player]s by order of play and returns the remaining deck and a
     * [Map] containing each [Player]'s starting hand in a [Pair].
     * @param [deckForHands] is the [Array] of [Card]s to be used for dealing to [Player]s
     * @return [Pair] of:
     *  - The remaining deck of cards as an [Array] of [Card]s
     *  - [Map] of all [Player] hands with the key being the [Player] and the value being a [Array] of [Card]s.
     */
    fun generateInitialHands(deckForHands: Array<Card>): Pair<Array<Card>, Map<Player, Array<Card>>> {
        val hands: MutableMap<Player, Array<Card>> = mutableMapOf()
        var remainingDeck: List<Card> = deckForHands.toList()
        for (i in playerOrder.indices) {
            val parts: Pair<List<Card>, List<Card>> =
                remainingDeck.partition { card -> remainingDeck.indexOf(card) < startHand }
            hands[playerOrder[i]] = parts.first.toTypedArray()
            remainingDeck = parts.second
        }
        return Pair(remainingDeck.toTypedArray(), hands)
    }

    /**
     * Generates the initial game board (must be overridden).
     * The board is made up of all the card piles visible to the players.
     * @param [deckForBoard] is the [Array] of [Card]s to be used on the board
     * @return [List] of [Tile]s representing the initial setup of a given game type. Note that list
     * does not contain any positional data. This is the responsibility of front-end development.
     */
    fun generateInitialBoard(deckForBoard: Array<Card>): List<Tile>

    fun getCurrentBoard(): List<Tile> {
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

    fun getPlayerCurrentHand(player: Player): Array<Card>? {
        // TODO: it would be better if this throws an exception when there is no round (rather than being able to return null
        if (rounds.isNullOrEmpty()) {
            return rounds.last().hands[player]
        }
        return null
    }

    /**
     * Draws the last/top [Card] of a given [cardPile] and returns the given [Card] and the remaining [cardPile].
     * If the [cardPile] is empty, then null is returned along with the empty [cardPile].
     *
     * @param [cardPile] is an [Array] of [Card]s. It may be empty but not null.
     * @return [Pair] containing:
     *  - first: The [Card] drawn or null if the [cardPile] is empty
     *  - second: An [Array] containing what remains of the [cardPile] after the [Card] in 'first' has been drawn.
     *  If the [cardPile] is depleted, an empty [Array] is returned.
     */
    fun drawTop(cardPile: Array<Card>): Pair<Card?, Array<Card>> {
        if (cardPile.isNotEmpty()) {
            val c = cardPile[cardPile.size - 1]
            if (cardPile.size > 1) {
                val p = cardPile.dropLast(1).toTypedArray()
                return Pair(c, p)
            }
            return Pair(c, arrayOf())
        }
        return Pair(null, cardPile)
    }

    /**
     *  Triggers the end of a turn by performing the following actions:
     *   - Check if the game end conditions have been met by calling [isGameOver]
     *   - If the game is over, return null otherwise return the completed [Round]
     *  Note that [endTurn] does not check for the board or hand correctness.
     *  @param [newBoard] is the validated board following the current's player's moves. It is a [List] of [Tile]s
     *  (in line with the board setup created by [generateInitialBoard] and stored in [rounds].
     *  @param [newHands] is a [Map] of [Player] to [Array] of [Card]s as created by [generateInitialHands] and stored
     *  in [rounds].
     *  @return Next [Player] that is due to play or null if the game is over
     */
    fun endTurn(newBoard: List<Tile>, newHands: Map<Player, Array<Card>>): Round? {
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

class Round(
    val index: Int,           // Must increment at every player round, overall round is mod(# of players)
    val playerId: UUID?,
    val board: List<Tile>,
    val hands: Map<Player, Array <Card>>
) {
    fun getOverallCurrentRound(numberOfPlayer: Int): Int {
        if (numberOfPlayer == 0) {
            return 0
        }
        return (index / numberOfPlayer)
    }
}

class Player(
    val name: String,
    val visibleHand: Boolean = false
) {
    val id: UUID = UUID.randomUUID()
}