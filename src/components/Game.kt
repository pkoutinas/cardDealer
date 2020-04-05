package components

interface Game {
    val name: String
    val minPlayers: Int
    val maxPlayers: Int
    val deck: List<Card>
    val startHand: Int          // Number of starting cards per player
    val roundType: String       // sequential / simultaneous / reflex
    val initialBoard: List<Tile>
    val rules: Any?             // TODO: how?

    fun generateDeck(): List<Card>
    fun generateInitialBoard(): List<Tile>
}