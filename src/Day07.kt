import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.roundToInt

fun main() {
    val test = Day07(test = true)
    check(test.part1() == 37)
    check(test.part2() == 168)

    val day = Day07(test = false)
    println(day.part1())
    println(day.part2())
}

class Day07(test: Boolean) {
    private val input: List<Int>

    init {
        input = readInput(7, test)[0].split(',').map(String::toInt)
    }

    fun part1(): Int {
        val positions = input.sorted()
        val best = positions[positions.size / 2]
        return positions.sumOf { (it - best).absoluteValue }
    }

    fun part2(): Int {
        val bestLower = input.sum() / input.size
        val bestUpper = bestLower + 1
        val lower = input.sumOf {
            val dist = (it - bestLower).absoluteValue
            (dist * (dist + 1)) / 2
        }
        val upper = input.sumOf {
            val dist = (it - bestUpper).absoluteValue
            (dist * (dist + 1)) / 2
        }
        return min(lower, upper)
    }
}
