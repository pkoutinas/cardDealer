import components.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import statics.decks.ClassicCard
import statics.games.UnoBoard

class BoardAndDeckAndCardTest : FunSpec({
    val cardT1: Card = ClassicCard(index = 0, loops = true, type = "T", value = "1", partOfSequence = true)
    val cardT2: Card = ClassicCard(index = 0, loops = true, type = "T", value = "2", partOfSequence = true)
    val cardS1: Card = ClassicCard(index = 0, loops = true, type = "S", value = "1", partOfSequence = true)
    val cardS2: Card = ClassicCard(index = 0, loops = true, type = "S", value = "2", partOfSequence = true)

    val emptyCardList: List<Card> = listOf()
    val cardListOf1: List<Card> = listOf(cardT1)
    val cardListOf4: List<Card> = listOf(cardT1, cardT2, cardS1, cardS2)

    val boardNoCards: Board = UnoBoard(emptyCardList)
    val board1Card: Board = UnoBoard(cardListOf1)
    val board4Cards: Board = UnoBoard(cardListOf4)

    test("Card types and values matching should work") {
        cardT1.sameType(cardT2) shouldBe true
        cardT1.sameType(cardS1) shouldBe false
        cardT1.sameValue(cardS1) shouldBe true
        cardT1.sameValue(cardT2) shouldBe false
    }
    test("Draw top card") {
        boardNoCards.drawTop(emptyCardList).first shouldBe null
        boardNoCards.drawTop(emptyCardList).second shouldBe listOf()
        board1Card.drawTop(cardListOf1).first shouldBe cardT1
        board1Card.drawTop(cardListOf1).second shouldBe listOf()
        board4Cards.drawTop(cardListOf4).first shouldBe cardS2
        board4Cards.drawTop(cardListOf4).second shouldBe listOf(cardT1, cardT2, cardS1)
    }
})