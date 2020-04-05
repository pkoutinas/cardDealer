package components

import statics.decks.*

interface Game {
    val name: String
    val minPlayers: Int
    val maxPlayers: Int
    val deck: List<Card>
    val startHand: Int          // Number of starting cards per player
    val roundType: String       // sequential / simultaneous / reflex
    val rules: Any?             // TODO: how?
}

class Uno(
    val name: String = "Uno",
    val minPlayers: Int = 8,
    val maxPlayers: Int = 8,
    val deck: List<Card> = getUno(),
    val startHand: Int = 7,
    val roundType: String = "sequential",
    val rules: Any? = null
) {
}