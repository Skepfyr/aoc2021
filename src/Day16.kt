fun main() {
    val test = Day16(test = true)
    check(test.part1() == 20L)
    check(test.part2() == 1L)

    val day = Day16(test = false)
    println(day.part1())
    println(day.part2())
}

class Day16(test: Boolean) {
    private val input: Packet

    private sealed class Packet(val version: Long, val type: Long) {
        abstract fun <T> fold(initial: T, f: (T, Packet) -> T): T
        abstract fun evaluate(): Long
    }

    private class Operator(version: Long, type: Long, val subPackets: List<Packet>) : Packet(version, type) {
        override fun <T> fold(initial: T, f: (T, Packet) -> T): T {
            return f(subPackets.fold(initial) { acc, packet -> packet.fold(acc, f) }, this)
        }

        override fun evaluate(): Long {
            val subexpressions = subPackets.map { it.evaluate() }
            return when (type) {
                0L -> subexpressions.sum()
                1L -> subexpressions.fold(1L) { acc, i -> acc * i }
                2L -> subexpressions.minOf { it }
                3L -> subexpressions.maxOf { it }
                5L -> if (subexpressions[0] > subexpressions[1]) 1 else 0
                6L -> if (subexpressions[0] < subexpressions[1]) 1 else 0
                7L -> if (subexpressions[0] == subexpressions[1]) 1 else 0
                else -> throw IllegalStateException()
            }
        }

        override fun toString(): String {
            return "Operator($version, $type) $subPackets"
        }
    }

    private class Literal(version: Long, type: Long, val literal: Long) : Packet(version, type) {
        override fun <T> fold(initial: T, f: (T, Packet) -> T): T {
            return f(initial, this)
        }

        override fun evaluate(): Long {
            return literal
        }

        override fun toString(): String {
            return "Literal($version, $type, $literal)"
        }
    }

    init {
        val bits = readInput(16, test)[0].toCharArray().flatMap { digit ->
            val int = digit.digitToInt(16)
            listOf((int shr 3) and 1 != 0, (int shr 2) and 1 != 0, (int shr 1) and 1 != 0, (int shr 0) and 1 != 0)
        }
        input = parsePacket(bits).first
    }

    private fun bitsToLong(bits: List<Boolean>): Long {
        return bits.fold(0L) { int, bit -> (int shl 1) or (if (bit) 1 else 0) }
    }

    private fun parsePacket(bits: List<Boolean>): Pair<Packet, Int> {
        val version = bitsToLong(bits.subList(0, 3))
        val type = bitsToLong(bits.subList(3, 6))
        return if (type == 4L) {
            val literalBits = mutableListOf<Boolean>()
            var i = 6
            while (true) {
                i += 5
                literalBits.addAll(bits.subList(i - 4, i))
                if (!bits[i - 5])
                    break
            }
            Pair(Literal(version, type, bitsToLong(literalBits)), i)
        } else {
            val (subPackets, i) = if (bits[6]) {
                val numSubPackets = bitsToLong(bits.subList(7, 18))
                val subPackets = mutableListOf<Packet>()
                var packetStart = 18
                for (i in 1..numSubPackets) {
                    val (subPacket, packetLen) = parsePacket(bits.subList(packetStart, bits.size))
                    subPackets.add(subPacket)
                    packetStart += packetLen
                }
                Pair(subPackets.toList(), packetStart)
            } else {
                val numBits = bitsToLong(bits.subList(7, 22))
                val subPackets = mutableListOf<Packet>()
                var packetStart = 22
                while (packetStart < 22 + numBits) {
                    val (subPacket, packetLen) = parsePacket(bits.subList(packetStart, bits.size))
                    subPackets.add(subPacket)
                    packetStart += packetLen
                }
                Pair(subPackets.toList(), packetStart)
            }
            Pair(Operator(version, type, subPackets), i)
        }
    }

    fun part1(): Long {
        return input.fold(0L) { total, packet ->
            total + packet.version
        }
    }

    fun part2(): Long {
        return input.evaluate()
    }
}
