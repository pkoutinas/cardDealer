package components

class Tile(
    val type: String,           // This will be game specific
    val owner: String?,         // null for communal tiles
    val pileType: String,       // can be single/overlap/cascade
    val visible: String?,       // can be null (for no) / top / all
    val category: String,       // can be draw, discard, both
    val cardTypeRestriction: String?,   // null if no restriction on card type
    val cardValueRestriction: String?,   // null if no restriction on card value
    val content: MutableList<out Card?>     // Empty list if no cards on tile
) {}