import java.util.regex.Pattern

fun main() {
    val test = Day04(test = true)
    check(test.part1() == 4512)
    check(test.part2() == 1924)

    val day = Day04(test = false)
    println(day.part1())
    println(day.part2())
}

class Day04(test: Boolean) {
    private val numbers: List<Int>
    private val boards: List<BingoBoard>

    init {
        val input = readInput(4, test).listIterator()
        numbers = input.next().split(',').map { it.trim().toInt() }
        val newBoards = mutableListOf<BingoBoard>()
        while (input.hasNext()) {
            check(input.next().isBlank())
            newBoards.add(BingoBoard(Array(5) {
                input.next().trim().split(Regex("""\s+""")).map { BingoBoard.Cell(it.trim().toInt(), false) }
                    .toTypedArray()
            }))
        }
        boards = newBoards
    }

    fun part1(): Int {
        val boards = boards.map { it.deepCopy() }
        val (winner, winningNum) = numbers.firstNotNullOf { num ->
            boards.forEach { it.markNumber(num) }
            val winner = boards.find { it.isWon() } ?: return@firstNotNullOf null
            Pair(winner, num)
        }
        return winner.score(winningNum)
    }

    fun part2(): Int {
        val boards = boards.map { it.deepCopy() }.toMutableSet()
        for (num in numbers) {
            boards.forEach { it.markNumber(num) }
            if (boards.size == 1) {
                val loser = boards.iterator().next()
                if (loser.isWon()) {
                    return loser.score(num)
                }
            }
            boards.removeAll { it.isWon() }
        }
        return 0
    }
}

class BingoBoard(var board: Array<Array<Cell>>) {
    data class Cell(var num: Int, var marked: Boolean)

    fun deepCopy(): BingoBoard {
        return BingoBoard(board.map { row -> row.map { it.copy() }.toTypedArray() }.toTypedArray())
    }

    fun markNumber(num: Int) {
        for (row in board) {
            for (cell in row) {
                if (cell.num == num) {
                    cell.marked = true
                }
            }
        }
    }

    fun isWon(): Boolean {
        for (row in board) {
            if (row.all { it.marked }) {
                return true
            }
        }

        for (col in board[0].indices) {
            if (board.indices.all { row -> board[row][col].marked }) {
                return true
            }
        }

        return false
    }

    fun score(winningNum: Int): Int {
        return winningNum * board.sumOf { row -> row.sumOf { cell -> if (cell.marked) 0 else cell.num } }
    }
}
