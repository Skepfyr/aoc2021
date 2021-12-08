fun main() {
    val test = Day08(test = true)
    check(test.part1() == 26)
    check(test.part2() == 61229)

    val day = Day08(test = false)
    println(day.part1())
    println(day.part2())
}

class Day08(test: Boolean) {
    private val notes: List<Note>

    init {
        notes = readInput(8, test).map { note ->
            val (first, second) = note.split('|')
            val input =
                first.trim().split(' ').map { it.toCharArray().map { c -> Wire.fromChar(c) }.toSortedSet() }.toSet()
            val output =
                second.trim().split(' ').map { it.toCharArray().map { c -> Wire.fromChar(c) }.toSortedSet() }.toList()
            Note(input, output)
        }
    }

    fun part1(): Int {
        return notes.sumOf { note -> note.output.count { it.size in setOf(2, 4, 3, 7) } }
    }

    fun part2(): Int {
        return notes.sumOf { note ->
            val digits = mutableMapOf<Set<Wire>, Int>()
            val one = note.input.find { it.size == 2 }!!
            digits[one] = 1
            val seven = note.input.find { it.size == 3 }!!
            digits[seven] = 7
            val four = note.input.find { it.size == 4 }!!
            digits[four] = 4
            val eight = note.input.find { it.size == 7 }!!
            digits[eight] = 8

            val sixSegments = note.input.filter { it.size == 6 }
            check(sixSegments.size == 3)
            var six: Set<Wire>? = null
            for (candidate in sixSegments) {
                if (!candidate.containsAll(one)) {
                    digits[candidate] = 6
                    six = candidate
                } else if (candidate.containsAll(four)) {
                    digits[candidate] = 9
                } else {
                    digits[candidate] = 0
                }
            }

            val fiveSegments = note.input.filter { it.size == 5 }
            check(fiveSegments.size == 3)
            for (candidate in fiveSegments) {
                if (candidate.containsAll(one)) {
                    digits[candidate] = 3
                } else if (six!!.containsAll(candidate)) {
                    digits[candidate] = 5
                } else {
                    digits[candidate] = 2
                }
            }

            val outputDigits = note.output.map { digits[it]!! }
            outputDigits[0] * 1000 + outputDigits[1] * 100 + outputDigits[2] * 10 + outputDigits[3]
        }
    }
}

enum class Wire {
    A, B, C, D, E, F, G;

    companion object {
        fun fromChar(char: Char): Wire {
            return when (char) {
                'a' -> A
                'b' -> B
                'c' -> C
                'd' -> D
                'e' -> E
                'f' -> F
                'g' -> G
                else -> throw IllegalArgumentException()
            }
        }
    }
}

private data class Note(val input: Set<Set<Wire>>, val output: List<Set<Wire>>)
