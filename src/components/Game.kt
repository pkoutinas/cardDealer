package components

import statics.games.UnoCard
import java.util.*

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
     * [MutableList] of all [Card]s to be used in this session. [deck] is initialised by the overriding method
     * [generateDeck].
     */
    var deck: MutableList<out Card>
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
     */
    val rounds: MutableList<Round>

    val rules: Any?             // TODO: how?

    /**
     * Generate Deck (must be overridden)
     *
     * @param
     * @return [List] of [Card] corresponding to a given game type.
     */
    fun generateDeck(): List<Card>

    /**
     * Generates the initial game board (must be overridden).
     * The board is made up of all the card piles visible to the players.
     * @param
     * @return [List] of [Tile]s representing the initial setup of a given game type. Note that list
     * does not contain any positional data. This is the responsibility of front-end development.
     */
    fun generateInitialBoard(): List<Tile>

    /**
     * Sets the default playing order based on the [players] provided.
     *
     * @param [players] is an [Array] of [Player]s to be used in this game.
     * @return [Array] of [Player]s in the order of play. Default implementation returns the same as the input parameter.
     */
    fun setPlayerOrder(players: Array<Player>): Array<Player> {
        return players
    }

    /**
     * Used to start the game by dealing the appropriate cards to the players and setting the board.
     *
     * @param
     * @return
     */
    fun dealRoundZero() {
        // First deal to players
        val hands: Map<Player, MutableList<out Card>> = generateInitialHands()
        // Place rest on board
        val board: List<Tile> = generateInitialBoard()

        rounds.add(Round(0, null, board, hands))
    }

    /**
     * Distributes the appropriate [Card]s to all [Player]s by order of play and returns a [Map] containing each
     * [Player]'s starting hand.
     *
     * @param
     * @return [Map] of all [Player] hands with the key being the [Player] object and the value being
     * a [MutableList] of [Card]s
     */
    fun generateInitialHands(): Map<Player, MutableList<out Card>> {
        val hands: MutableMap<Player, MutableList<out Card>> = mutableMapOf()
        for (i in playerOrder.indices) {
            val parts: Pair<List<Card>, List<Card>> =
                deck.partition { card -> deck.indexOf(card) < startHand }
            hands[playerOrder[i]] = parts.first.toMutableList()
            deck = parts.second.toMutableList()
        }
        return hands
    }

    fun getCurrentBoard(): List<Tile> {
        return rounds.last().board
    }

    fun getLastPlayerId(): UUID? {
        return rounds.last().playerId
    }

    /**
     * Returns the [Player] that is currently 'playing' (i.e. that hasn't made any moves).
     * In the opening round, the [Player] returned will be the first one in the [playerOrder].
     * By default, the order of play is sequential and repeats.
     *
     * @param
     * @return [Player] currently due for action
     */
    fun getCurrentPlayer(): Player {
        val lastId = getLastPlayerId()
        if (lastId == null || lastId == playerOrder.last().id) {
            return playerOrder[0]
        }
        return playerOrder[playerOrder.indexOfFirst { player -> player.id == lastId } + 1]
    }

    fun getPlayerCurrentHand(player: Player): MutableList<out Card>? {
        return rounds.last().hands[player]
    }

    /**
     * Draws the last [Card] of a given [cardPile] of [Card] and returns the given [Card] and the
     * remaining [cardPile].
     * If the [cardPile] is empty, then null is returned along with an empty [MutableList]
     *
     * @param [cardPile] is [MutableList] of [Card] objects. It can be empty but not null.
     * @return [Pair] containing:
     *  - first: The [Card] drawn or null if the [cardPile] was empty
     *  - second: A [MutableList] contain what remains of the [cardPile] after the [Card] in first was drawn.
     *  If the [cardPile] is depleted, an empty [MutableList] is returned.
     */
    fun drawTop(cardPile: MutableList<out Card>): Pair<Card?, MutableList<out Card>> {
        if (cardPile.size > 0) {
            val c = cardPile[cardPile.size - 1] as UnoCard
            if (cardPile.size > 1) {
                val p = cardPile.subList(0, cardPile.size - 2)
                return Pair(c, p)
            }
            return Pair(c, mutableListOf())
        }
        return Pair(null, cardPile)
    }
}

class Round(
    val index: Int,           // Must increment at every player round, overall round is mod(# of players)
    val playerId: UUID?,
    val board: List<Tile>,
    val hands: Map<Player, MutableList<out Card>>
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
    val visibleHand: Boolean
) {
    val id: UUID = UUID.randomUUID()
}