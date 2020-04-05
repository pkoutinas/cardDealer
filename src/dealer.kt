
import statics.games.Uno

fun main() {
    val game = Uno()

    println("Starting a game of ${game.name}...")
    println("${game.deck.size} card in the deck")
}