import java.math.BigInteger

fun main() {
    val test = Day06(test = true)
    check(test.part1() == 5934)
    check(test.part2() == 26984457539L)

    val day = Day06(test = false)
    println(day.part1())
    println(day.part2())
}

class Day06(test: Boolean) {
    private val input: List<Int>

    init {
        input = readInput(6, test)[0].split(',').map(String::toInt)
    }

    fun part1(): Int {
        val fish = input.toMutableList()
        for (day in 1..80) {
            var newFish = 0
            for (i in fish.indices) {
                fish[i] -= 1
                if (fish[i] < 0) {
                    fish[i] = 6
                    newFish += 1
                }
            }
            fish.addAll(List(newFish) { 8 })
        }
        return fish.size
    }

    fun part2(): Long {
        val fish = input.fold(MutableList(9) { 0L }) { fish, age ->
            fish[age] += 1L
            fish
        }
        for (day in 1..256) {
            val ageZero = fish.removeFirst()
            fish[6] += ageZero
            fish.add(ageZero)
        }
        return fish.sum()
    }
}
