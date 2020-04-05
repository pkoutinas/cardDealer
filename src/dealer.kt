import statics.decks.*

fun main() {
    val unoDeck = getUno()

    unoDeck.forEach { println(it.toString()) }
}