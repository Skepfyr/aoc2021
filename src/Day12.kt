fun main() {
    val test = Day12(test = true)
    check(test.part1() == 19)
    check(test.part2() == 103)

    val day = Day12(test = false)
    println(day.part1())
    println(day.part2())
}

class Day12(test: Boolean) {
    private val input = readInput(12, test).fold(mutableMapOf<String, MutableSet<String>>()) { map, line ->
        val caves = line.split('-')
        map.getOrPut(caves[0]) { mutableSetOf() }.add(caves[1])
        map.getOrPut(caves[1]) { mutableSetOf() }.add(caves[0])
        map
    }.mapValues { it.value.toSet() }

    private fun pathsToEnd1(start: String, avoid: Set<String>): Int {
        return input[start]!!
            .filter { !avoid.contains(it) }
            .sumOf {
                if (it == "end") {
                    1
                } else {
                    pathsToEnd1(it, if (it.first().isUpperCase()) avoid else avoid union setOf(it))
                }
            }
    }

    fun part1(): Int {
        return pathsToEnd1("start", setOf("start"))
    }

    private fun pathsToEnd2(start: String, avoid: Set<String>, usedDouble: Boolean): Int {
        return input[start]!!
            .filter { it != "start" && (!usedDouble || !avoid.contains(it)) }
            .sumOf {
                if (it == "end") {
                    1
                } else {
                    pathsToEnd2(
                        it,
                        if (it.first().isUpperCase()) avoid else avoid union setOf(it),
                        usedDouble || avoid.contains(it)
                    )
                }
            }
    }

    fun part2(): Int {
        return pathsToEnd2("start", setOf("start"), false)
    }
}
