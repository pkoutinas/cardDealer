package components

interface RuleSet{
    val name: String
    val rules: List<Rule>
}

/**
 * A [Rule] that needs to be validated.
 * @param name as [String] of the rule.
 * @param conditions is a [List] of lamda functions, each returning a [Boolean]. This [List] of conditions takes
 * a single [List] of parameters for their execution.
 * Note that the type of those parameters needs to be cast within the function.
 */
class Rule(private val name: String, private val conditions: List<(List<Any>) -> Boolean>) {

    private fun applyFunction(v: List<Any>, f: (List<Any>) -> Boolean): Boolean = f(v)

    fun validateRule(params: List<Any>): Boolean {
//        return applyFunction(params, condition)
        return conditions.fold(true){ result: Boolean, function: (List<Any>) -> Boolean ->
            result && applyFunction(params, function)}
    }
}