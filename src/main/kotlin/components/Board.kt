package components

interface Board {
    val name: String
    val tiles: List<Tile>

    /**
     * Generates the initial game board (must be overridden).
     * The board is made up of all the card piles visible to the players.
     * @param [deckForBoard] is the [List] of [Card]s to be used on the board
     * @return [List] of [Tile]s representing the initial setup of a given game type. Note that list
     * does not contain any positional data. This is the responsibility of front-end development.
     */
    fun generateBoard(deckForBoard: List<Card>): List<Tile>

    /**
     * Draws the last/top [Card] of a given [cardPile] and returns the given [Card] and the remaining [cardPile].
     * If the [cardPile] is empty, then null is returned along with the empty [cardPile].
     *
     * @param [cardPile] is an [Array] of [Card]s. It may be empty but not null.
     * @return [Pair] containing:
     *  - first: The [Card] drawn or null if the [cardPile] is empty
     *  - second: An [List] containing what remains of the [cardPile] after the [Card] in 'first' has been drawn.
     *  If the [cardPile] is depleted, an empty [List] is returned.
     */
    fun drawTop(cardPile: List<Card>): Pair<Card?, List<Card>> {
        if (cardPile.isNotEmpty()) {
            val c = cardPile[cardPile.size - 1]
            if (cardPile.size > 1) {
                val p = cardPile.dropLast(1)
                return Pair(c, p)
            }
            return Pair(c, listOf())
        }
        return Pair(null, cardPile)
    }
}

data class Tile(
    val type: String,           // This will be game specific
    val owner: String?,         // null for communal tiles
    val pileType: String,       // can be single/overlap/cascade
    val visible: String?,       // can be null (for no) / top / all
    val category: String,       // can be draw, discard, both
    val cardTypeRestriction: String?,   // null if no restriction on card type
    val cardValueRestriction: String?,   // null if no restriction on card value
    val content: List<Card>     // Empty list if no cards on tile
)