fun main() {
    val test = Day11(test = true)
    check(test.part1() == 1656)
    check(test.part2() == 195)

    val day = Day11(test = false)
    println(day.part1())
    println(day.part2())
}

class Day11(test: Boolean) {
    private val input = readInput(11, test).map { line ->
        line.toCharArray().map { it.digitToInt() }
    }

    private fun step(octopuses: MutableList<MutableList<Int>>): Int {
        for (row in octopuses.indices) {
            for (col in octopuses[row].indices) {
                octopuses[row][col] += 1
            }
        }
        var flashes = 0
        var didFlash = true
        while (didFlash) {
            didFlash = false
            for (row in octopuses.indices) {
                for (col in octopuses[row].indices) {
                    if (octopuses[row][col] > 9) {
                        for (newRow in row - 1..row + 1) {
                            for (newCol in col - 1..col + 1) {
                                if (newRow in octopuses.indices && newCol in octopuses[newRow].indices) {
                                    octopuses[newRow][newCol] += 1
                                }
                            }
                        }
                        didFlash = true
                        flashes += 1
                        octopuses[row][col] = -100
                    }
                }
            }
        }
        for (row in octopuses.indices) {
            for (col in octopuses[row].indices) {
                if (octopuses[row][col] < 0) {
                    octopuses[row][col] = 0
                }
            }
        }
        return flashes
    }

    fun part1(): Int {
        val octopuses = input.map { it.toMutableList() }.toMutableList()
        return (1..100).sumOf { step(octopuses) }
    }

    fun part2(): Int {
        val octopuses = input.map { it.toMutableList() }.toMutableList()
        return generateSequence(1) { it + 1 }.first { step(octopuses) == 100 }
    }
}
