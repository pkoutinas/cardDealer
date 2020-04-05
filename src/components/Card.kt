package components

class Card(val family: String, val value: String) {

    override fun toString(): String {
        return "$value of $family"
    }

    fun sameFamily(otherCard: Card): Boolean {
        return this.family == otherCard.family;
    }

    fun sameValue(otherCard: Card): Boolean {
        return this.value == otherCard.value;
    }
}