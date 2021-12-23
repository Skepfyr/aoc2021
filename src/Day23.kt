import java.lang.IllegalArgumentException
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds

fun main() {
    val time = measureTimeMillis {
        val test = Day23(test = true)
        check(test.part1() == 12521)
        check(test.part2() == 44169)

        val day = Day23(test = false)
        println(day.part1())
        println(day.part2())
    }.milliseconds
    println("$time")
}

class Day23(test: Boolean) {
    private val initialBurrow: Burrow

    init {
        val input = readInput(23, test)
        initialBurrow = Burrow(
            MutableList(11) { null },
            listOf(
                mutableListOf(Amphipod.fromChar(input[2][3]), Amphipod.fromChar(input[3][3])),
                mutableListOf(Amphipod.fromChar(input[2][5]), Amphipod.fromChar(input[3][5])),
                mutableListOf(Amphipod.fromChar(input[2][7]), Amphipod.fromChar(input[3][7])),
                mutableListOf(Amphipod.fromChar(input[2][9]), Amphipod.fromChar(input[3][9])),
            ),
            0,
        )
    }

    enum class Amphipod(val energy: Int, val room: Int, val roomLoc: Int) {
        A(1, 0, 2), B(10, 1, 4), C(100, 2, 6), D(1000, 3, 8);

        fun toChar(): Char {
            return when (this) {
                A -> 'A'
                B -> 'B'
                C -> 'C'
                D -> 'D'
            }
        }

        companion object {
            fun fromChar(char: Char): Amphipod {
                return when (char) {
                    'A' -> A
                    'B' -> B
                    'C' -> C
                    'D' -> D
                    else -> throw IllegalArgumentException("'$this' is not a valid Amphipod.")
                }
            }
        }
    }

    data class Burrow(val hallway: MutableList<Amphipod?>, val rooms: List<MutableList<Amphipod?>>, var energy: Int) {
        fun isDone(): Boolean {
            return rooms.withIndex()
                .all { (i, room) -> room.all { amphipod -> amphipod != null && amphipod.room == i } }
        }

        override fun toString(): String {
            val burrow = StringBuilder(13 * 5)
            burrow.appendLine("#############")
            burrow.append('#')
            for (amphipod in hallway) {
                burrow.append(amphipod?.toChar() ?: '.')
            }
            burrow.appendLine('#')
            burrow.append("###")
            for (room in rooms) {
                burrow.append(room[0]?.toChar() ?: '.')
                burrow.append('#')
            }
            burrow.appendLine("##")
            for (i in 1..rooms[0].lastIndex) {
                burrow.append("  #")
                for (room in rooms) {
                    burrow.append(room[i]?.toChar() ?: '.')
                    burrow.append('#')
                }
                burrow.appendLine("  ")
            }
            burrow.appendLine("  #########")
            return burrow.toString()
        }
    }

    private fun minEnergySolution(burrow: Burrow, minEnergy: Int): Int {
        if (burrow.energy >= minEnergy) return minEnergy
        if (burrow.isDone()) return burrow.energy
        var newMin = minEnergy
        for ((i, amphipod) in burrow.hallway.withIndex()) {
            if (amphipod == null) continue
            val targetRoom = burrow.rooms[amphipod.room]
            if (!targetRoom.all { it == null || it.room == amphipod.room }) continue
            var targetSlot = targetRoom.indexOfFirst { it != null } - 1
            targetSlot = if (targetSlot < 0) targetRoom.lastIndex else targetSlot
            val emptyHallway = if (i < amphipod.roomLoc) {
                burrow.hallway.subList(i + 1, amphipod.roomLoc + 1)
            } else {
                burrow.hallway.subList(amphipod.roomLoc, i)
            }.all { it == null }
            if (!emptyHallway) continue
            burrow.hallway[i] = null
            burrow.rooms[amphipod.room][targetSlot] = amphipod
            val energyUsed = amphipod.energy * (targetSlot + 1 + (amphipod.roomLoc - i).absoluteValue)
            burrow.energy += energyUsed
            val solutionEnergy = minEnergySolution(burrow, newMin)
            burrow.hallway[i] = amphipod
            burrow.rooms[amphipod.room][targetSlot] = null
            burrow.energy -= energyUsed
            newMin = min(newMin, solutionEnergy)
        }
        for ((i, room) in burrow.rooms.withIndex()) {
            val roomLoc = 2 + i * 2
            val index = room.indexOfFirst { it != null }
            if (index == -1) continue
            val amphipod = room[index]!!
            if (room.all { it == null || it.room == i }) continue
            val firstIndex = burrow.hallway.subList(0, roomLoc).indexOfLast { it != null } + 1
            val lastAmphipod = burrow.hallway.subList(roomLoc, burrow.hallway.size).indexOfFirst { it != null }
            val lastIndex = if (lastAmphipod < 0) burrow.hallway.lastIndex else roomLoc + lastAmphipod - 1

            burrow.rooms[i][index] = null
            for (dest in firstIndex..lastIndex) {
                if (dest == 2 || dest == 4 || dest == 6 || dest == 8) continue
                burrow.hallway[dest] = amphipod
                val energyUsed = amphipod.energy * (index + 1 + (roomLoc - dest).absoluteValue)
                burrow.energy += energyUsed
                val solutionEnergy = minEnergySolution(burrow, newMin)
                burrow.hallway[dest] = null
                burrow.energy -= energyUsed
                newMin = min(newMin, solutionEnergy)
            }
            burrow.rooms[i][index] = amphipod
        }
        return newMin
    }

    fun part1(): Int {
        return minEnergySolution(initialBurrow, Int.MAX_VALUE)
    }

    fun part2(): Int {
        val extra = listOf(
            listOf(Amphipod.D, Amphipod.D),
            listOf(Amphipod.C, Amphipod.B),
            listOf(Amphipod.B, Amphipod.A),
            listOf(Amphipod.A, Amphipod.C),
        )
        val rooms = List(initialBurrow.rooms.size) {
            val room = mutableListOf<Amphipod?>()
            room.add(initialBurrow.rooms[it][0])
            room.addAll(extra[it])
            room.add(initialBurrow.rooms[it][1])
            room
        }
        val burrow = Burrow(initialBurrow.hallway, rooms, initialBurrow.energy)
        return minEnergySolution(burrow, Int.MAX_VALUE)
    }
}
