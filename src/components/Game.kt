package components

import statics.games.UnoCard
import java.util.*

interface Game {
    val players: Array<Player>

    val name: String
    var deck: MutableList<out Card>
    val minPlayers: Int
    val maxPlayers: Int
    val startHand: Int          // Number of starting cards per player
    val roundType: String       // sequential / simultaneous / reflex
    val rounds: MutableList<Round>
    var playerOrder: Array<Player>
    val rules: Any?             // TODO: how?

    fun generateDeck(): List<Card>
    fun generateInitialBoard(): List<Tile>

    fun setPlayerOrder(players: Array<Player>): Array<Player> {
        return players
    }

    fun dealRoundZero() {
        // First deal to players
        val hands: Map<Player, MutableList<out Card>> = generateInitialHands(playerOrder)
        // Place rest on board
        val board: List<Tile> = generateInitialBoard()

        rounds.add(Round(0, null, board, hands))
    }

    fun generateInitialHands(players: Array<Player>): Map<Player, MutableList<out Card>> {
        val hands: MutableMap<Player, MutableList<out Card>> = mutableMapOf()
        for (i in players.indices) {
            val parts: Pair<List<Card>, List<Card>> =
                deck.partition { card -> deck.indexOf(card) < startHand }
            hands[players[i]] = parts.first.toMutableList()
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