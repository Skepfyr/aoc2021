fun main() {
    val test = Day13(test = true)
    check(test.part1() == 17)
    test.part2()

    val day = Day13(test = false)
    println(day.part1())
    day.part2()
}

class Day13(test: Boolean) {
    private val dots: Set<Dot>
    private val instructions: List<Pair<Dir, Int>>

    init {
        val input = readInput(13, test).listIterator()
        val dots = mutableSetOf<Dot>()
        while (input.hasNext()) {
            val line = input.next()
            if (line.isBlank()) {
                break
            }
            val coordinates = line.split(',')
            dots.add(Dot(coordinates[0].trim().toInt(), coordinates[1].trim().toInt()))
        }
        this.dots = dots.toSet()

        val instructions = mutableListOf<Pair<Dir, Int>>()
        while (input.hasNext()) {
            val line = input.next().removePrefix("fold along").trim()
            val parts = line.split('=')
            val dir = when (parts[0]) {
                "x" -> Dir.X
                "y" -> Dir.Y
                else -> throw IllegalArgumentException()
            }
            instructions.add(Pair(dir, parts[1].trim().toInt()))
        }
        this.instructions = instructions
    }

    private data class Dot(val x: Int, val y: Int)
    private enum class Dir { X, Y }

    private fun fold(dots: Set<Dot>, instruction: Pair<Dir, Int>): Set<Dot> {
        return dots.map {
            var (x, y) = it
            when (instruction.first) {
                Dir.X -> if (x < instruction.second) {
                    x = instruction.second - 1 - x
                } else if (x == instruction.second) {
                    throw IllegalStateException("Dot on fold line $instruction: $it")
                } else {
                    x -= instruction.second + 1
                }
                Dir.Y -> if (y > instruction.second) {
                    y = 2 * instruction.second - y
                    if (y < 0) {
                        throw IllegalStateException("$x, $y")
                    }
                } else if (y == instruction.second) {
                    throw IllegalStateException("Dot on fold line $instruction: $it")
                }
            }
            Dot(x, y)
        }.toSet()
    }

    fun part1(): Int {
        return fold(dots, instructions[0]).size
    }

    fun part2() {
        var dots = dots
        for (instruction in instructions) {
            dots = fold(dots, instruction)
        }
        val paper = mutableListOf<MutableList<Boolean>>()
        for ((x, y) in dots) {
            if (y + 1 > paper.size) {
                repeat(y + 1 - paper.size) { paper.add(mutableListOf()) }
            }
            if (x + 1 > paper[y].size) {
                repeat(x + 1 - paper[y].size) { paper[y].add(false) }
            }
            paper[y][x] = true
        }
        for (line in paper) {
            for (dot in line) {
                print(if (dot) '#' else '.')
            }
            println()
        }
    }
}
