package components

class Tile(
    val type: String,           // This will be game specific
    var owner: String?,         // null for communal tiles
    var pileType: String,       // can be single/overlap/cascase
    var visible: String?,       // can be null (for no) / top / all
    var category: String,       // can be draw, discard, play
    var content: List<Card>
) {

}