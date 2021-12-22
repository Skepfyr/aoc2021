import kotlin.math.max
import kotlin.math.min

fun main() {
    val test = Day22(test = true)
    check(test.part1() == 474140)
    check(test.part2() == 2758514936282235L)

    val day = Day22(test = false)
    println(day.part1())
    println(day.part2())
}

class Day22(test: Boolean) {
    companion object {
        val INSTRUCTION_REGEX = Regex("""(on|off) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""")
    }

    private val input = readInput(22, test).map {
        val (state, xMin, xMax, yMin, yMax, zMin, zMax) = INSTRUCTION_REGEX.matchEntire(it)!!.destructured
        Instruction(
            state == "on",
            Box(xMin.toLong()..xMax.toLong(), yMin.toLong()..yMax.toLong(), zMin.toLong()..zMax.toLong())
        )
    }

    private data class Box(var x: LongRange, var y: LongRange, var z: LongRange) {
        fun size(): Long {
            return (x.last - x.first + 1L) * (y.last - y.first + 1L) * (z.last - z.first + 1L)
        }

        fun overlaps(other: Box): Boolean {
            fun LongRange.overlaps(other: LongRange): Boolean {
                return !(this.last < other.first || this.first > other.last)
            }
            return x.overlaps(other.x) && y.overlaps(other.y) && z.overlaps(other.z)
        }
    }

    private data class Instruction(val state: Boolean, val box: Box)

    fun part1(): Int {
        val initRegion = Array(101) { Array(101) { Array(101) { false } } }
        val constrain = { range: LongRange -> range.first < -50 || range.last > 50 }
        instruction@ for ((state, box) in input) {
            if (constrain(box.x) || constrain(box.y) || constrain(box.z))
                continue@instruction
            for (x in box.x) {
                for (y in box.y) {
                    for (z in box.z) {
                        initRegion[x.toInt() + 50][y.toInt() + 50][z.toInt() + 50] = state
                    }
                }
            }
        }
        return initRegion.sumOf { x -> x.sumOf { xy -> xy.count { xyz -> xyz } } }
    }

    fun part2(): Long {
        var boxes = mutableSetOf<Box>()
        for ((state, setBox) in input) {
            val newBoxes = mutableSetOf<Box>()
            for (reactorBox in boxes) {
                if (setBox.overlaps(reactorBox)) {
                    fun Long.clamp(range: LongRange): Long {
                        return min(max(this, range.first), range.last)
                    }
                    
                    val minX = setBox.x.first.clamp(reactorBox.x)
                    val maxX = setBox.x.last.clamp(reactorBox.x)
                    val minY = setBox.y.first.clamp(reactorBox.y)
                    val maxY = setBox.y.last.clamp(reactorBox.y)
                    val minZ = setBox.z.first.clamp(reactorBox.z)
                    val maxZ = setBox.z.last.clamp(reactorBox.z)
                    val splitBoxes = listOf(
                        Box(reactorBox.x, reactorBox.y, reactorBox.z.first until minZ),
                        Box(reactorBox.x, reactorBox.y, (maxZ + 1)..reactorBox.z.last),
                        Box(reactorBox.x, reactorBox.y.first until minY, minZ..maxZ),
                        Box(reactorBox.x, (maxY + 1)..reactorBox.y.last, minZ..maxZ),
                        Box(reactorBox.x.first until minX, minY..maxY, minZ..maxZ),
                        Box((maxX + 1)..reactorBox.x.last, minY..maxY, minZ..maxZ),
                    ).filter { it.size() > 0 }
                    newBoxes.addAll(splitBoxes)
                } else {
                    newBoxes.add(reactorBox)
                }
            }
            if (state) {
                newBoxes.add(setBox)
            }
            boxes = newBoxes
        }
        return boxes.sumOf { it.size() }
    }
}
