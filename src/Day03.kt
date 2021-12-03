fun main() {
    val test = Day03(test = true)
    check(test.part1() == 198)
    check(test.part2() == 230)

    val day = Day03(test = false)
    println(day.part1())
    println(day.part2())
}

class Day03(test: Boolean) {
    private val input =
        readInput(3, test).map { line ->
            line.toCharArray().map {
                when (it) {
                    '0' -> false
                    '1' -> true
                    else -> throw IllegalArgumentException()
                }
            }.toBooleanArray()
        }


    fun part1(): Int {
        val counts = input.fold(IntArray(input[0].size)) { counts, input ->
            for (i in input.indices) {
                counts[input.size - i - 1] += if (input[i]) 1 else 0
            }
            counts
        }
        val (gamma, epsilon) = counts.withIndex().fold(Pair(0, 0)) { (gamma, epsilon), (i, count) ->
            val newGamma = gamma + if (count > input.size / 2) 1 shl i else 0
            val newEpsilon = epsilon + if (count < input.size / 2) 1 shl i else 0
            Pair(newGamma, newEpsilon)
        }
        return gamma * epsilon
    }

    fun part2(): Int {
        var o2Reports = input
        var o2Index = 0
        while (o2Reports.size > 1) {
            val count = o2Reports.count { it[o2Index] }
            val mostCommon = count in (o2Reports.size + 1) / 2..o2Reports.size
            o2Reports = o2Reports.filter { it[o2Index] == mostCommon }
            o2Index += 1
        }
        val o2 = o2Reports[0].reversed().withIndex().fold(0) { value, (i, bit) ->
            value + if (bit) 1 shl i else 0
        }
        var co2Reports = input
        var co2Index = 0
        while (co2Reports.size > 1) {
            val count = co2Reports.count { it[co2Index] }
            val mostCommon = count in 0..(co2Reports.size - 1) / 2
            co2Reports = co2Reports.filter { it[co2Index] == mostCommon }
            co2Index += 1
        }
        val co2 = co2Reports[0].reversed().withIndex().fold(0) { value, (i, bit) ->
            value + if (bit) 1 shl i else 0
        }
        return o2 * co2
    }
}
