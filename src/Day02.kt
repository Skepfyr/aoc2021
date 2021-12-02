fun main() {
    val test = Day02(test = true)
    check(test.part1() == 150)
    check(test.part2() == 900)

    val day = Day02(test = false)
    println(day.part1())
    println(day.part2())
}

class Day02(test: Boolean) {
    private val input =
        readInput(2, test).map { line ->
            val parts = line.split(' ')
            val dir = when (parts[0]) {
                "forward" -> Direction.FORWARD
                "down" -> Direction.DOWN
                "up" -> Direction.UP
                else -> throw IllegalArgumentException()
            }
            val amount = parts[1].toInt()
            Command(dir, amount)
        }

    enum class Direction {
        FORWARD, DOWN, UP
    }

    data class Command(val direction: Direction, val amount: Int)

    fun part1(): Int {
        var pos = 0
        var depth = 0
        for (command in input) {
            when (command.direction) {
                Direction.FORWARD -> pos += command.amount
                Direction.DOWN -> depth += command.amount
                Direction.UP -> depth -= command.amount
            }
        }
        return pos * depth
    }

    fun part2(): Int {
        var pos = 0
        var depth = 0
        var aim = 0
        for (command in input) {
            when (command.direction) {
                Direction.FORWARD -> {
                    pos += command.amount
                    depth += aim * command.amount
                }
                Direction.DOWN -> aim += command.amount
                Direction.UP -> aim -= command.amount
            }
        }
        return pos * depth
    }
}
