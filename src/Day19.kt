import kotlin.math.absoluteValue

fun main() {
    val test = Day19(test = true)
    check(test.part1() == 79)
    check(test.part2() == 3621)

    val day = Day19(test = false)
    println(day.part1())
    println(day.part2())
}

class Day19(test: Boolean) {
    private val scanners: List<Position>
    private val beacons: Set<Position>

    private data class Position(val x: Int, val y: Int, val z: Int) {
        operator fun plus(other: Position): Position {
            return Position(this.x + other.x, this.y + other.y, this.z + other.z)
        }

        operator fun minus(other: Position): Position {
            return Position(this.x - other.x, this.y - other.y, this.z - other.z)
        }
    }

    init {
        val scannerRegex = Regex("""--- scanner (\d+) ---""")
        val positionRegex = Regex("""(-?\d+),(-?\d+),(-?\d+)""")
        val input = readInput(19, test).listIterator()
        val reports = mutableListOf<Set<Position>>()
        while (input.hasNext()) {
            val (scannerNumber) = scannerRegex.matchEntire(input.next())!!.destructured
            check(scannerNumber.toInt() == reports.size)
            val scanner = mutableSetOf<Position>()
            var beacon = ""
            while (input.hasNext() && input.next().also { beacon = it }.isNotBlank()) {
                val (x, y, z) = positionRegex.matchEntire(beacon)!!.destructured
                scanner.add(Position(x.toInt(), y.toInt(), z.toInt()))
            }
            reports.add(scanner.toSet())
        }
        val rotations = listOf<(Position) -> Position>(
            { (x, y, z) -> Position(x, y, z) },
            { (x, y, z) -> Position(x, -z, y) },
            { (x, y, z) -> Position(x, -y, -z) },
            { (x, y, z) -> Position(x, z, -y) },
            { (x, y, z) -> Position(-x, -y, z) },
            { (x, y, z) -> Position(-x, z, y) },
            { (x, y, z) -> Position(-x, y, -z) },
            { (x, y, z) -> Position(-x, -z, -y) },
            { (x, y, z) -> Position(y, z, x) },
            { (x, y, z) -> Position(y, -x, z) },
            { (x, y, z) -> Position(y, -z, -x) },
            { (x, y, z) -> Position(y, x, -z) },
            { (x, y, z) -> Position(-y, -z, x) },
            { (x, y, z) -> Position(-y, x, z) },
            { (x, y, z) -> Position(-y, z, -x) },
            { (x, y, z) -> Position(-y, -x, -z) },
            { (x, y, z) -> Position(z, x, y) },
            { (x, y, z) -> Position(z, -y, x) },
            { (x, y, z) -> Position(z, -x, -y) },
            { (x, y, z) -> Position(z, y, -x) },
            { (x, y, z) -> Position(-z, -x, y) },
            { (x, y, z) -> Position(-z, y, x) },
            { (x, y, z) -> Position(-z, x, -y) },
            { (x, y, z) -> Position(-z, -y, -x) },
        )

        val scanners = MutableList<Position?>(reports.size) { null }
        val beacons = mutableSetOf<Position>()
        val relativePositions = mutableMapOf<Position, MutableSet<Position>>()

        val relativeReports = reports.map { report ->
            val relativeReport = mutableMapOf<Position, MutableSet<Position>>()
            for (posA in report) {
                for (posB in report) {
                    if (posA == posB) continue
                    relativeReport.getOrPut(posB - posA) { mutableSetOf() }.add(posA)
                }
            }
            relativeReport.mapValues { (_, v) -> v.toSet() }.toMap()
        }

        scanners[0] = Position(0, 0, 0)
        beacons.addAll(reports[0])
        relativePositions.putAll(relativeReports[0].mapValues { (_, v) -> v.toMutableSet() })

        outer@ for (i in 1..scanners.lastIndex) {
            for (scanner in 1..scanners.lastIndex) {
                if (scanners[scanner] != null) continue
                for (rotated in rotations) {
                    val potentialScannerPositions = mutableMapOf<Position, Int>()
                    for ((relativePos, potentialBeacons) in relativeReports[scanner]) {
                        val rotatedRelativePos = rotated(relativePos)
                        val existingBeacons = relativePositions[rotatedRelativePos] ?: continue
                        for (beacon in potentialBeacons) {
                            val rotatedBeaconPos = rotated(beacon)
                            for (existingBeacon in existingBeacons) {
                                val scannerPos = existingBeacon - rotatedBeaconPos
                                potentialScannerPositions[scannerPos] = (potentialScannerPositions[scannerPos] ?: 0) + 1
                            }
                        }
                    }
                    for ((scannerPos, attestations) in potentialScannerPositions) {
                        if (attestations < 132) continue
                        scanners[scanner] = scannerPos
                        beacons.addAll(reports[scanner].map { rotated(it) + scannerPos })
                        for ((relativePos, scannerBeacons) in relativeReports[scanner]) {
                            relativePositions.getOrPut(rotated(relativePos)) { mutableSetOf() }
                                .addAll(scannerBeacons.map { rotated(it) + scannerPos })
                        }
                        continue@outer
                    }
                }
            }
            throw IllegalStateException("No scanner fits!")
        }
        this.scanners = scanners.map { it!! }
        this.beacons = beacons
    }

    fun part1(): Int {
        return beacons.size
    }

    fun part2(): Int {
        return scanners.flatMap { a -> scanners.map { it - a } }
            .maxOf { (x, y, z) -> x.absoluteValue + y.absoluteValue + z.absoluteValue }
    }
}
