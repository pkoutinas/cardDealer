
import components.Player
import statics.games.Uno

fun main() {
    // Add players
    val players: MutableList<Player> = mutableListOf()
    players.add(Player("Paul", false))
    players.add(Player("Hugo", false))
    println("Number of players: ${players.size}")

    // Choose game
    // TODO: check max/min for given game

    // When ready, start by dealing round 0
    println("Let's start!")
    val game = Uno(players.toTypedArray())
    game.dealRoundZero()


    println("Last player was: ${game.getLatestRound()?.playerId}")
    println("Current player is: ${players.find { p -> p == game.getCurrentPlayer()}?.name }")
}