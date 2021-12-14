fun main() {
    val test = Day14(test = true)
    check(test.part1() == 1588)
    check(test.part2() == 2188189693529L)

    val day = Day14(test = false)
    println(day.part1())
    println(day.part2())
}

class Day14(test: Boolean) {
    private val template: String
    private val rules: Map<Pair<Char, Char>, Char>

    init {
        val input = readInput(14, test).listIterator()
        template = input.next().trim()
        check(input.next().isBlank())
        val rules = mutableMapOf<Pair<Char, Char>, Char>()
        while (input.hasNext()) {
            val rule = input.next().split("->").map { it.trim() }
            rules[Pair(rule[0][0], rule[0][1])] = rule[1][0]
        }
        this.rules = rules
    }

    fun part1(): Int {
        val pairCounts =
            (1..10).fold(template) { polymer, _ ->
                val newPolymer = StringBuilder(polymer.length * 2)
                for (i in polymer.indices) {
                    if (i > 0) {
                        val insert = rules[Pair(polymer[i - 1], polymer[i])]
                        if (insert != null) {
                            newPolymer.append(insert)
                        }
                    }
                    newPolymer.append(polymer[i])
                }
                newPolymer.toString()
            }.groupingBy { it }.eachCount()
        val min = pairCounts.minOf { it.value }
        val max = pairCounts.maxOf { it.value }
        return max - min
    }

    fun part2(): Long {
        val first = template.first()
        val last = template.last()
        val initialPairCounts =
            template.windowedSequence(2) { Pair(it[0], it[1]) }.groupingBy { it }.eachCount()
                .mapValues { it.value.toLong() }
        val finalPairCounts = (1..40).fold(initialPairCounts) { oldPairCounts, _ ->
            val pairCounts = mutableMapOf<Pair<Char, Char>, Long>()
            for ((pair, count) in oldPairCounts) {
                val insert = rules[pair]
                if (insert == null) {
                    pairCounts[pair] = count
                } else {
                    pairCounts.compute(Pair(pair.first, insert)) { _, v -> (v ?: 0) + count }
                    pairCounts.compute(Pair(insert, pair.second)) { _, v -> (v ?: 0) + count }
                }
            }
            pairCounts
        }
        val letterFrequencies = mutableMapOf<Char, Long>()
        for ((pair, count) in finalPairCounts) {
            letterFrequencies.compute(pair.first) { _, v -> (v ?: 0) + count }
            letterFrequencies.compute(pair.second) { _, v -> (v ?: 0) + count }
        }
        letterFrequencies[first] = letterFrequencies[first]!! + 1
        letterFrequencies[last] = letterFrequencies[last]!! + 1
        val min = letterFrequencies.minOf { it.value } / 2
        val max = letterFrequencies.maxOf { it.value } / 2
        return max - min
    }
}
