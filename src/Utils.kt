import java.io.File

/**
 * Reads lines from the given input file.
 */
fun readInput(day: Int, test: Boolean = false) =
    File("resources", "Day%02d%s.txt".format(day, if (test) "_test" else "")).readLines()
