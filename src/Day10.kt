fun main() {
    val test = Day10(test = true)
    check(test.part1() == 26397)
    check(test.part2() == 288957L)

    val day = Day10(test = false)
    println(day.part1())
    println(day.part2())
}

class Day10(test: Boolean) {
    private val input = readInput(10, test).map {
        it.toCharArray().map { char ->
            when (char) {
                '(' -> Bracket(BracketKind.PAREN, OpenClose.OPEN)
                '[' -> Bracket(BracketKind.SQUARE, OpenClose.OPEN)
                '{' -> Bracket(BracketKind.CURLY, OpenClose.OPEN)
                '<' -> Bracket(BracketKind.ANGLE, OpenClose.OPEN)
                ')' -> Bracket(BracketKind.PAREN, OpenClose.CLOSE)
                ']' -> Bracket(BracketKind.SQUARE, OpenClose.CLOSE)
                '}' -> Bracket(BracketKind.CURLY, OpenClose.CLOSE)
                '>' -> Bracket(BracketKind.ANGLE, OpenClose.CLOSE)
                else -> throw IllegalArgumentException("Found unexpected character $char")
            }
        }
    }

    private data class Bracket(val kind: BracketKind, val type: OpenClose)

    private enum class BracketKind(val score: Int, val value: Long) {
        PAREN(3, 1L), SQUARE(57, 2L), CURLY(1197, 3L), ANGLE(25137, 4L)
    }

    private enum class OpenClose { OPEN, CLOSE }

    fun part1(): Int {
        return input.sumOf line@{ line: List<Bracket> ->
            val stack = ArrayDeque<BracketKind>()
            for (bracket in line) {
                when (bracket.type) {
                    OpenClose.OPEN -> stack.addLast(bracket.kind)
                    OpenClose.CLOSE -> {
                        when (stack.removeLastOrNull()) {
                            bracket.kind -> {}
                            null -> throw IllegalArgumentException("Tried to close unopened paren $bracket")
                            else -> return@line bracket.kind.score
                        }
                    }
                }
            }
            0 // Incomplete
        }
    }

    fun part2(): Long {
        val scores = input.map line@{ line ->
            val stack = ArrayDeque<BracketKind>()
            for (bracket in line) {
                when (bracket.type) {
                    OpenClose.OPEN -> stack.addLast(bracket.kind)
                    OpenClose.CLOSE -> {
                        when (stack.lastOrNull()) {
                            bracket.kind -> stack.removeLast()
                            null -> throw IllegalArgumentException("Tried to close unopened paren $bracket")
                            else -> return@line null // Corrupted
                        }
                    }
                }
            }
            stack.foldRight(0L) { bracketKind, score -> score * 5 + bracketKind.value }
        }.filterNotNull().sorted()
        return scores[scores.size / 2]
    }
}
