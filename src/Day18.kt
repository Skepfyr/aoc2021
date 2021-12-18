import kotlin.math.max

fun main() {
    val test = Day18(test = true)
    check(test.part1() == 4140)
    check(test.part2() == 3993)

    val day = Day18(test = false)
    println(day.part1())
    println(day.part2())
}

class Day18(test: Boolean) {
    private val input = readInput(18, test).map { SnailNumber.parseString(it) }

    private sealed class SnailNumber {
        data class Tuple(val left: SnailNumber, val right: SnailNumber) : SnailNumber() {
            override fun magnitude(): Int {
                return 3 * left.magnitude() + 2 * right.magnitude()
            }

            override fun explode(depth: Int): ExplodeResult {
                if (depth == 4) {
                    return ExplodeResult(
                        true,
                        Value(0),
                        left as Value,
                        right as Value,
                    )
                }
                val leftExplode = left.explode(depth + 1)
                if (leftExplode.hasExploded) {
                    val newRight = if (leftExplode.right == null) right else right.addLeftmost(leftExplode.right)
                    return ExplodeResult(
                        true,
                        Tuple(leftExplode.num, newRight),
                        leftExplode.left,
                        null
                    )
                }
                val rightExplode = right.explode(depth + 1)
                if (rightExplode.hasExploded) {
                    val newLeft = if (rightExplode.left == null) left else left.addRightmost(rightExplode.left)
                    return ExplodeResult(
                        true,
                        Tuple(newLeft, rightExplode.num),
                        null,
                        rightExplode.right,
                    )
                }
                return ExplodeResult(false, this, null, null)
            }

            override fun split(): SnailNumber? {
                val leftSplit = left.split()
                if (leftSplit != null) {
                    return Tuple(leftSplit, right)
                }
                val rightSplit = right.split()
                if (rightSplit != null) {
                    return Tuple(left, rightSplit)
                }
                return null
            }

            override fun addLeftmost(num: Value): SnailNumber {
                return Tuple(left.addLeftmost(num), right)
            }

            override fun addRightmost(num: Value): SnailNumber {
                return Tuple(left, right.addRightmost(num))
            }

            override fun toString(): String {
                return "[$left, $right]"
            }
        }

        data class Value(val value: Int) : SnailNumber() {
            override fun magnitude(): Int {
                return value
            }

            override fun explode(depth: Int): ExplodeResult {
                return ExplodeResult(false, this, null, null)
            }

            override fun split(): SnailNumber? {
                return if (value >= 10) {
                    Tuple(Value(value / 2), Value((value + 1) / 2))
                } else {
                    null
                }
            }

            override fun addLeftmost(num: Value): SnailNumber {
                return Value(value + num.value)
            }

            override fun addRightmost(num: Value): SnailNumber {
                return Value(value + num.value)
            }

            override fun toString(): String {
                return value.toString()
            }
        }

        operator fun plus(num: SnailNumber): SnailNumber {
            return Tuple(this, num).reduced()
        }

        fun reduced(): SnailNumber {
            var result = this
            while (true) {
                val exploded = result.explode(0)
                if (exploded.hasExploded) {
                    result = exploded.num
                    continue
                }
                val split = result.split()
                if (split != null) {
                    result = split
                    continue
                }
                return result
            }
        }

        private data class ExplodeResult(
            val hasExploded: Boolean,
            val num: SnailNumber,
            val left: Value?,
            val right: Value?,
        )

        abstract fun magnitude(): Int
        abstract fun explode(depth: Int): ExplodeResult
        abstract fun split(): SnailNumber?
        abstract fun addLeftmost(num: Value): SnailNumber
        abstract fun addRightmost(num: Value): SnailNumber
        abstract override fun toString(): String

        companion object {
            fun parseString(string: String): SnailNumber {
                return if (string.first().isDigit()) {
                    Value(string.toInt())
                } else {
                    var numBrackets = 0
                    val middle = string.indexOfFirst {
                        when (it) {
                            '[' -> {
                                numBrackets += 1
                                false
                            }
                            ']' -> {
                                numBrackets -= 1
                                false
                            }
                            ',' -> numBrackets == 1
                            else -> false
                        }
                    }
                    Tuple(
                        parseString(string.substring(1, middle)),
                        parseString(string.substring(middle + 1, string.length - 1))
                    )
                }
            }
        }
    }

    fun part1(): Int {
        return input.subList(1, input.size).fold(input[0]) { a, b -> a + b }.magnitude()
    }

    fun part2(): Int {
        var maxMagnitude = 0
        for ((i, left) in input.withIndex()) {
            for ((j, right) in input.withIndex()) {
                if (i == j) continue
                maxMagnitude = max(maxMagnitude, (left + right).magnitude())
            }
        }
        return maxMagnitude
    }
}
