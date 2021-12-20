fun main() {
    val test = Day20(test = true)
    check(test.part1() == 35)
    check(test.part2() == 3351)

    val day = Day20(test = false)
    println(day.part1())
    println(day.part2())
}

class Day20(test: Boolean) {
    private val rules: List<Boolean>
    private val initialImage: Image

    init {
        val input = readInput(20, test)
        rules = input[0].map { it == '#' }
        check(input[1].isBlank())
        initialImage = Image(input.subList(2, input.size).map { row -> row.map { it == '#' } }, false)
    }

    private data class Image(val pixels: List<List<Boolean>>, val border: Boolean) {
        override fun toString(): String {
            fun pixel(bit: Boolean): Char {
                return if (bit) '#' else '.'
            }

            val builder = StringBuilder((pixels.size + 2) * (pixels.size + 2))
            generateSequence { pixel(border) }.take(pixels.size + 2).joinTo(builder, "")
            builder.appendLine()
            for (row in pixels) {
                sequence {
                    yield(border)
                    yieldAll(row)
                    yield(border)
                }.map { pixel(it) }.joinTo(builder, "")
                builder.appendLine()
            }
            generateSequence { pixel(border) }.take(pixels.size + 2).joinTo(builder, "")
            builder.appendLine()
            return builder.toString()
        }
    }

    private fun Int.shiftBit(bit: Boolean): Int {
        return (this shl 1) or (if (bit) 1 else 0)
    }

    private fun Int.shiftAll(bits: Iterable<Boolean>): Int {
        return bits.fold(this) { value, bit -> value.shiftBit(bit) }
    }

    private fun stepImage(image: Image): Image {
        val (pixels, border) = image
        val stepped = MutableList(pixels.size + 2) { MutableList(pixels.size + 2) { rules[0] } }

        fun Int.shiftBorder(): Int {
            return this.shiftBit(border).shiftBit(border).shiftBit(border)
        }

        fun Int.shiftPixels(row: Int, col: Int): Int {
            return this.shiftAll(sequence {
                yield(border)
                yield(border)
                yieldAll(pixels[row - 1])
                yield(border)
                yield(border)
            }.drop(col).take(3).asIterable())
        }

        for (row in stepped.indices) {
            for (col in stepped[row].indices) {
                var index = 0
                index = if (row < 2) {
                    index.shiftBorder()
                } else {
                    index.shiftPixels(row - 1, col)
                }
                index = when (row) {
                    0, stepped.lastIndex -> index.shiftBorder()
                    else -> index.shiftPixels(row, col)
                }
                index = if (row < stepped.size - 2) {
                    index.shiftPixels(row + 1, col)
                } else {
                    index.shiftBorder()
                }
                stepped[row][col] = rules[index]
            }
        }
        return Image(stepped, if (border) rules[511] else rules[0])
    }

    fun part1(): Int {
        val finalImage = stepImage(stepImage(initialImage))
        check(!finalImage.border) { "Border is all lit!" }
        return finalImage.pixels.sumOf { row -> row.count { it } }
    }

    fun part2(): Int {
        val ret = (1..50).fold(initialImage) { image, _ -> stepImage(image) }
        println(ret)
        return ret.pixels.sumOf { row -> row.count { it } }
    }
}
