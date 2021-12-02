fun main() {
    val test = Day01(test = true)
    check(test.part1() == 7)
    check(test.part2() == 5)

    val day = Day01(test = false)
    println(day.part1())
    println(day.part2())
}

class Day01(test: Boolean) {
    private val input = readInput(1, test).map(String::toInt)

    fun part1(): Int {
        return input.zipWithNext().count { (a, b) -> b > a }
    }

    fun part2(): Int {
        return input.windowed(3) { it.sum() }.zipWithNext().count { (a, b) -> b > a }
    }
}
