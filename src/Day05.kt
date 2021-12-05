import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun main() {
    val test = Day05(test = true)
    check(test.part1() == 5)
    check(test.part2() == 12)

    val day = Day05(test = false)
    println(day.part1())
    println(day.part2())
}

class Day05(test: Boolean) {
    private val size: Pair<Int, Int>
    private val lines: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>

    init {
        var maxX = 0
        var maxY = 0
        lines = readInput(5, test).map {
            val (start, end) = it.split(" -> ").map {
                val (x, y) = it.split(',').map(String::toInt)
                Pair(x, y)
            }
            maxX = max(maxX, start.first)
            maxX = max(maxX, end.first)
            maxY = max(maxY, start.second)
            maxY = max(maxY, end.second)
            Pair(start, end)
        }
        size = Pair(maxX, maxY)
    }

    fun part1(): Int {
        val locations = mutableMapOf<Pair<Int, Int>, Int>()
        for ((start, end) in lines) {
            if (start.first == end.first) {
                val min = min(start.second, end.second)
                val max = max(start.second, end.second)
                (min..max).forEach {
                    val location = Pair(start.first, it)
                    locations[location] = locations.getOrDefault(location, 0) + 1
                }
            } else if (start.second == end.second) {
                val min = min(start.first, end.first)
                val max = max(start.first, end.first)
                (min..max).forEach {
                    val location = Pair(it, start.second)
                    locations[location] = locations.getOrDefault(location, 0) + 1
                }
            }
        }
        return locations.asIterable().count { (_, count) -> count > 1 }
    }

    fun part2(): Int {
        val locations = mutableMapOf<Pair<Int, Int>, Int>()
        for ((start, end) in lines) {
            val xRange = if (start.first < end.first) {
                start.first..end.first
            } else if (start.first > end.first) {
                start.first downTo end.first
            } else {
                List((start.second - end.second).absoluteValue + 1) { start.first }
            }
            val yRange = if (start.second < end.second) {
                start.second..end.second
            } else if (start.second > end.second) {
                start.second downTo end.second
            } else {
                List((start.first - end.first).absoluteValue + 1) { start.second }
            }
            xRange.zip(yRange).forEach {
                locations[it] = locations.getOrDefault(it, 0) + 1
            }
        }
        return locations.asIterable().count { (_, count) -> count > 1 }
    }
}
