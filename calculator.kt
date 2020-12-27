import java.lang.IllegalStateException
import kotlin.math.pow

interface Expr {
    fun evaluate() : Double
    fun isComplete() : Boolean
}

abstract class BinaryExpr : Expr {
    var first: Expr? = null
    var second: Expr? = null

    override fun isComplete(): Boolean = first != null && second != null

    protected fun firstVal() : Double = first?.evaluate() ?: throw IllegalStateException("First argument not set")
    protected fun secondVal() : Double = second?.evaluate() ?: throw IllegalStateException("Second argument not set")
}

class PlusExpr : BinaryExpr() {
    override fun evaluate(): Double = firstVal() + secondVal()
    override fun toString(): String = "($first + $second)"
}

class MultExpr : BinaryExpr() {
    override fun evaluate(): Double = firstVal() * secondVal()
    override fun toString(): String = "($first * $second)"
}

class MinusExpr : BinaryExpr() {
    override fun evaluate(): Double = firstVal() - secondVal()
    override fun toString(): String = "($first - $second)"
}

class DivExpr : BinaryExpr() {
    override fun evaluate(): Double = firstVal() / secondVal()
    override fun toString(): String = "($first / $second)"
}

class ExpExpr : BinaryExpr() {
    override fun evaluate(): Double = firstVal().pow(secondVal())
    override fun toString(): String = "($first ^ $second)"
}

class NumericExpr(private val value : Double) : Expr {
    override fun evaluate(): Double = value

    override fun isComplete() : Boolean = true

    override fun toString(): String = value.toString()
}

class Parser {
    var currentExpr : Expr? = null

    private var currentNumber : String = ""
    private var subParser : Parser? = null
    private var depth : Int = 0
    private var isNegativeNumber : Boolean = false

    private fun finalizeNumberExpr() {
        if (currentNumber.isNotEmpty()) {
            val numericExpr = NumericExpr(if (isNegativeNumber) -currentNumber.toDouble() else currentNumber.toDouble())
            if (currentExpr == null) {
                currentExpr = numericExpr
            } else if (currentExpr is BinaryExpr) {
                (currentExpr as BinaryExpr).second = numericExpr
            }
            isNegativeNumber = false;
            currentNumber = ""
        }
    }

    fun addChar(char : Char) {
        when (char) {
            '(' -> {
                depth++
                if (subParser != null) {
                    subParser?.addChar(char)
                } else {
                    subParser = Parser()
                }
                return
            }
            ')' -> {
                depth--
                if (depth == 0) {
                    subParser?.finished()
                    if (currentExpr == null) {
                        currentExpr = subParser?.currentExpr
                    } else if (currentExpr is BinaryExpr) {
                        (currentExpr as BinaryExpr).second = subParser?.currentExpr
                    }
                    subParser = null
                } else {
                    subParser?.addChar(char)
                }
                return
            }
        }
        if (subParser != null) {
            subParser?.addChar(char)
            return
        }

        if (char == ' ') return

        if (char in '0'..'9' || char == ',' || char == '.') {
            currentNumber += char
            return
        } else {
            finalizeNumberExpr()
        }

        if (currentExpr?.isComplete() == true) {
            var binaryExpr : BinaryExpr? = null

            when (char) {
                '+' -> binaryExpr = PlusExpr()
                '-' -> binaryExpr = MinusExpr()
                '*' -> binaryExpr = MultExpr()
                '/' -> binaryExpr = DivExpr()
                '^' -> binaryExpr = ExpExpr()
            }

            if (binaryExpr != null) {
                binaryExpr.first = currentExpr
                currentExpr = binaryExpr
            }
        } else if (char == '-') {
            isNegativeNumber = true
        } else {
            throw IllegalStateException("Unexpected character ($char) found")
        }


    }

    fun finished() {
        finalizeNumberExpr()
    }

    fun getResult() : Double? = currentExpr?.evaluate()

    fun parse(input: String) : Parser {
        for (char in input) {
            addChar(char)
        }
        finished()
        return this
    }
}


fun main() {
    while (true) {
        print("Enter expression: ")
        val input = readLine() ?: break

        val parser = Parser()
        try {
            parser.parse(input)

            println("Parsed: " + parser.currentExpr)
            println("Result: " + parser.getResult())
        }
        catch (e : IllegalStateException) {
            println(e.message)
        }
    }

}