fun main() {
    val test = Day15(test = true)
    check(test.part1() == 40)
    check(test.part2() == 315)

    val day = Day15(test = false)
    println(day.part1())
    println(day.part2())
}

class Day15(test: Boolean) {
    private val input = readInput(15, test).map { line -> line.toCharArray().map { it.digitToInt() } }

    private data class Cell(val x: Int, val y: Int)

    private fun minPath(map: List<List<Int>>): Int {
        val distance = MutableList(map.size) { MutableList(map[it].size) { Int.MAX_VALUE } }
        val next = ArrayDeque<Cell>()
        distance[0][0] = 0
        for (y in map.indices) {
            for (x in map[y].indices) {
                next.add(Cell(x, y))
            }
        }

        while (next.isNotEmpty()) {
            val cell = next.removeFirst()
            if (cell.y == map.lastIndex && cell.x == map[cell.y].lastIndex) {
                return distance[cell.y][cell.x]
            }
            for (neighbour in listOf(
                Cell(cell.x, cell.y - 1),
                Cell(cell.x, cell.y + 1),
                Cell(cell.x - 1, cell.y),
                Cell(cell.x + 1, cell.y)
            )) {

                if (neighbour.y < 0 || neighbour.x < 0 ||
                    neighbour.y > map.lastIndex || neighbour.x > map[neighbour.y].lastIndex
                ) {
                    continue
                }
                val newDist = distance[cell.y][cell.x] + map[neighbour.y][neighbour.x]
                if (newDist < distance[neighbour.y][neighbour.x]) {
                    distance[neighbour.y][neighbour.x] = newDist
                    next.addLast(neighbour)
                }
            }
        }
        return 0

    }

    fun part1(): Int {
        return minPath(input)
    }

    fun part2(): Int {
        val map = List(input.size * 5) { y ->
            val repeatY = y / input.size
            val mapY = y % input.size
            List(input[mapY].size * 5) { x ->
                val repeatX = x / input[mapY].size
                val mapX = x % input[mapY].size
                val increase = repeatX + repeatY
                (input[mapY][mapX] + increase - 1) % 9 + 1
            }
        }
        return minPath(map)
    }
}
