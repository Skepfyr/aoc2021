fun main() {
    val test = Day03(test = true)
    check(test.part1() == 198)
    check(test.part2() == 230)

    val day = Day03(test = false)
    println(day.part1())
    println(day.part2())
}

class Day03(test: Boolean) {
    private val bits: Int
    private val values: List<Int>

    init {
        val input = readInput(3, test)
        bits = input[0].length
        values = input.map { it.toInt(2) }
    }


    fun part1(): Int {
        var gamma = 0
        var epsilon = 0
        for (bit in 0 until bits) {
            val numOnes = values.count { it and (1 shl bit) != 0 }
            val mostCommon = if (numOnes > values.size / 2) 1 else 0
            val leastCommon = if (numOnes <= values.size / 2) 1 else 0
            gamma = gamma or (mostCommon shl bit)
            epsilon = epsilon or (leastCommon shl bit)
        }
        return gamma * epsilon
    }

    fun part2(): Int {
        var o2Reports = values
        var o2Bit = bits - 1
        while (o2Reports.size > 1) {
            val numOnes = o2Reports.count { it and (1 shl o2Bit) != 0 }
            val mostCommon = numOnes in (o2Reports.size + 1) / 2..o2Reports.size
            o2Reports = o2Reports.filter { (it and (1 shl o2Bit) != 0) == mostCommon }
            o2Bit -= 1
        }
        val o2 = o2Reports[0]
        var co2Reports = values
        var co2Bit = bits - 1
        while (co2Reports.size > 1) {
            val numOnes = co2Reports.count { it and (1 shl co2Bit) != 0 }
            val mostCommon = numOnes in 0..(co2Reports.size - 1) / 2
            co2Reports = co2Reports.filter { (it and (1 shl co2Bit) != 0) == mostCommon }
            co2Bit -= 1
        }
        val co2 = co2Reports[0]
        return o2 * co2
    }
}
