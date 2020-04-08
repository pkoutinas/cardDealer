import components.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainKeys
import io.kotest.matchers.shouldBe

import statics.games.Uno

@ExperimentalStdlibApi
class UnoTest : FunSpec({
    val candidate0: List<Player> = listOf()
    val candidate1: List<Player> = listOf(Player("Paul"))
    val candidates10: List<Player> = List(10){Player("test")}
    val candidates: List<Player> = listOf(Player("Paul"), Player("Hugo"))
    val game: Game = Uno()
    game.rounds.add(game.dealRoundZero(candidates))
    val roundZero: Round = game.rounds[0]
    val startBoard: Board = roundZero.board

    test("Game, deck and board must have name \"Uno\"") {
        game.name shouldBe "uno"
        game.deck.name shouldBe "uno"
        startBoard.name shouldBe "uno"
        game.rules.name shouldBe "uno"
    }
    test("Uno deck has 112 cards") {
        game.deck.cards.size shouldBe 112
    }
    // TODO find way to test types
    test("All core properties are present"){
        game.properties.shouldContainKeys("minPlayers", "maxPlayers", "startHand", "roundType")
    }
    test("Can only start game if the core properties are met"){
        game.canStartGame(candidate0) shouldBe false
        game.canStartGame(candidate1) shouldBe false
        game.canStartGame(candidates) shouldBe true
        game.canStartGame(candidates10) shouldBe false
    }
    test("Initial playing order is the same as the game setup") {
        (candidates.toTypedArray().contentEquals(game.playerOrder)) shouldBe true
        (candidates.toTypedArray().contentEquals(game.setPlayerOrder(candidates))) shouldBe true
    }
    test("Uno board has exactly 3 tiles") {
        startBoard.tiles.size shouldBe 3
    }
    test("Uno board has exactly one 'draw' tile") {
        startBoard.tiles.filter { t -> t.name == "draw" }.size shouldBe 1
    }
    test("Uno board has exactly one 'discard' tile") {
        startBoard.tiles.filter { t -> t.name == "discard" }.size shouldBe 1
    }
    test("Uno board has exactly one 'plus' tile") {
        startBoard.tiles.filter { t -> t.name == "plus" }.size shouldBe 1
    }
    test("The 'discard' tile has exactly 1 card") {
        startBoard.tiles.find { t -> t.name == "discard" }!!.content.size shouldBe 1
    }
    test("The 'plus' tile is empty") {
        startBoard.tiles.find { t -> t.name == "plus" }!!.content.size shouldBe 0
    }
    test("Entire deck is either on the board or in hands") {
        roundZero.hands.fold(0, { total, hand -> total + hand.cards.size }) + startBoard.tiles.fold(
            0,
            { total, tile -> total + tile.content.size }) shouldBe game.deck.cards.size

    }
    test("Hands in all player's hands are equal to ${game.properties["startHand"] as Int}") {
        roundZero.hands.forEach { h -> h.cards.size shouldBe game.properties["startHand"] as Int }
    }
})