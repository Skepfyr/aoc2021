import java.util.*

fun main() {
    val test = Day09(test = true)
    check(test.part1() == 15)
    check(test.part2() == 1134)

    val day = Day09(test = false)
    println(day.part1())
    println(day.part2())
}

class Day09(test: Boolean) {
    private val heightmap: List<List<Int>>

    init {
        heightmap = readInput(9, test).map { it.toCharArray().map { c -> c - '0' } }
    }

    fun part1(): Int {
        var risk = 0
        for (row in heightmap.indices) {
            candidate@ for (col in heightmap[row].indices) {
                val height = heightmap[row][col]
                for (point in adjacentPoints(Pair(row, col))) {
                    if (heightmap[point.first][point.second] <= height) {
                        continue@candidate
                    }
                }
                risk += 1 + height
            }
        }
        return risk
    }

    fun part2(): Int {
        val basins = mutableListOf<Int>()

        for (row in heightmap.indices) {
            candidate@ for (col in heightmap[row].indices) {
                val height = heightmap[row][col]
                for (point in adjacentPoints(Pair(row, col))) {
                    if (heightmap[point.first][point.second] <= height) {
                        continue@candidate
                    }
                }
                val basinPoints = mutableSetOf(Pair(row, col))
                val newPoints = LinkedList<Pair<Int, Int>>()
                newPoints.push(Pair(row, col))
                while (newPoints.size > 0) {
                    val point = newPoints.pop()
                    for (adjacent in adjacentPoints(point)) {
                        if (heightmap[adjacent.first][adjacent.second] != 9 && basinPoints.add(adjacent)) {
                            newPoints.push(adjacent)
                        }
                    }
                }
                basins.add(basinPoints.size)
            }
        }
        basins.sortDescending()
        return basins[0] * basins[1] * basins[2]
    }


    private fun adjacentPoints(point: Pair<Int, Int>): List<Pair<Int, Int>> {
        val points = mutableListOf<Pair<Int, Int>>()
        if (point.first != 0)
            points.add(Pair(point.first - 1, point.second))
        if (point.second != 0)
            points.add(Pair(point.first, point.second - 1))
        if (point.first != heightmap.lastIndex)
            points.add(Pair(point.first + 1, point.second))
        if (point.second != heightmap[point.first].lastIndex)
            points.add(Pair(point.first, point.second + 1))
        return points
    }
}
