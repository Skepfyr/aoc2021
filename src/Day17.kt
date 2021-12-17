import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

fun main() {
    /*
    val test = Day17(test = true)
    check(test.part1() == 45)
    check(test.part2() == 112)
*/
    val day = Day17(test = false)
    println(day.part1())
    println(day.part2())
}

class Day17(test: Boolean) {
    private val targetX: IntRange
    private val targetY: IntRange

    init {
        val ranges = readInput(17, test)[0].removePrefix("target area: ").split(',')
        val xRange = ranges[0].trim().removePrefix("x=").split("..")
        val yRange = ranges[1].trim().removePrefix("y=").split("..")
        targetX = xRange[0].toInt()..xRange[1].toInt()
        targetY = yRange[0].toInt()..yRange[1].toInt()
    }

    fun part1(): Int {
        val absMaxY = max(targetY.first.absoluteValue, targetY.last.absoluteValue)
        return absMaxY * (absMaxY - 1) / 2
    }

    fun part2(): Int {
        val maxX = max(targetX.first, targetX.last)
        val absMinY = min(targetY.first, targetY.last)
        var count = 0
        for (ySpeed in absMinY..this.part1()) {
            speedLoop@ for (xSpeed in 1..maxX) {
                var xVel = xSpeed
                var yVel = ySpeed
                var x = 0
                var y = 0
                while (y > targetY.first && x < targetX.last) {
                    x += xVel
                    y += yVel
                    xVel += -xVel.sign
                    yVel -= 1
                    if (x in targetX && y in targetY) {
                        count += 1
                        continue@speedLoop
                    }
                }
            }
        }
        return count
    }
}
