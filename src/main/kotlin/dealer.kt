import components.Player
import components.Rule
import statics.games.Uno
import kotlin.reflect.typeOf

@ExperimentalStdlibApi
fun main() {
    // Add players
    val players: MutableList<Player> = mutableListOf()
    players.add(Player("Paul", false))
    players.add(Player("Hugo", false))
    println("Number of players: ${players.size}")

    // Choose game
    val game = Uno()


    // When ready, start by dealing round 0
    println("Let's start!")
    if (game.canStartGame(players)) {
        game.dealRoundZero(players)

        println("Last player was: ${game.getLatestRound()?.playerId}")
        println("Current player is: ${players.find { p -> p == game.getCurrentPlayer() }?.name}")
    }
}