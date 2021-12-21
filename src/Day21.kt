fun main() {
    val test = Day21(test = true)
    check(test.part1() == 739785)
    check(test.part2() == 444356092776315L)

    val day = Day21(test = false)
    println(day.part1())
    println(day.part2())
}

class Day21(test: Boolean) {
    private val startingPos =
        readInput(21, test).map { (it.subSequence(it.indexOf(':') + 2, it.length).toString().toInt() + 9) % 10 }

    fun part1(): Int {
        val scores = mutableListOf(0, 0)
        val positions = startingPos.toMutableList()
        var player = 0
        var die = 0
        var numRolls = 0
        val roll = {
            numRolls += 1
            val roll = die + 1
            die = (die + 1) % 100
            roll
        }
        while (scores.all { it < 1000 }) {
            positions[player] = (positions[player] + roll() + roll() + roll()) % 10
            scores[player] += positions[player] + 1
            player = 1 - player
        }
        return scores.minOf { it } * numRolls
    }

    fun part2(): Long {
        data class State(val player: Int, val position: List<Int>, val score: List<Int>) {
            val minScore = score.minOf { it }

            fun move(roll: Int): State {
                val newPosition = position.toMutableList()
                newPosition[player] = (position[player] + roll) % 10
                val newScore = score.toMutableList()
                newScore[player] = score[player] + newPosition[player] + 1
                val newPlayer = 1 - player
                return State(newPlayer, newPosition, newScore)
            }
        }

        val rollCounts = mapOf(3 to 1L, 4 to 3, 5 to 6, 6 to 7, 7 to 6, 8 to 3, 9 to 1)
        val comparator = compareBy<State>(
            { it.minScore },
            { it.score[0] },
            { it.score[1] },
            { it.position[0] },
            { it.position[1] },
            { it.player }
        )

        val wins = mutableListOf(0L, 0L)
        val scores = mutableMapOf<State, Long>()
        val nextScores = sortedSetOf<State>(comparator)
        val startingState = State(0, startingPos, listOf(0, 0))
        scores[startingState] = (scores[startingState] ?: 0) + 1
        nextScores.add(startingState)
        scores@ while (nextScores.isNotEmpty()) {
            val minState = nextScores.first()
            nextScores.remove(minState)
            for (player in 0..1) {
                if (minState.score[player] >= 21) {
                    wins[player] = wins[player] + scores[minState]!!
                    continue@scores
                }
            }
            for ((roll, count) in rollCounts) {
                val nextState = minState.move(roll)
                scores[nextState] = (scores[nextState] ?: 0) + count * scores[minState]!!
                nextScores.add(nextState)
            }
        }
        return wins.maxOf { it }
    }
}
