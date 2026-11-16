package hk.edu.thei.mti.bingogame

import kotlin.random.Random

/**
 * BingoGame implements the game logic for a standard 5x5 Bingo card.
 *
 * Standard Bingo rules:
 *   * The card is a 5x5 grid. Each cell holds a random number drawn from a
 *     different range for each column (the classic "75-ball" Bingo layout):
 *         Column 0 (B): 1-15
 *         Column 1 (I): 16-30
 *         Column 2 (N): 31-45  (the centre cell is a free space)
 *         Column 3 (G): 46-60
 *         Column 4 (O): 61-75
 *   * Numbers are called (drawn) one at a time from the full 1-75 pool.
 *   * A called number only marks the card if that number is on the card.
 *   * The player wins ("BINGO!") as soon as any full row, column, or
 *     diagonal has been marked (the free centre cell counts as marked).
 *
 * This class is independent of the UI so the rules can be understood and
 * tested on their own.
 */
class BingoGame {

    /**
     * The number of columns / rows on the card.
     */
    companion object {
        const val SIZE = 5

        /**
         * The range (inclusive) of numbers used for each of the 5 columns.
         * Index 0 -> column "B" (1-15), index 1 -> "I" (16-30), and so on.
         */
        val COLUMN_RANGES = listOf(
            1..15,
            16..30,
            31..45,
            46..60,
            61..75
        )
    }

    /**
     * The player's 5x5 card, stored as a flat list of 25 numbers in row-major
     * order (index = row * SIZE + column). The centre cell (index 12) is the
     * FREE space and is represented by [FREE_SPACE] (0).
     */
    private var card: IntArray = IntArray(SIZE * SIZE)

    /**
     * Tracks which card cells are marked (drawn), as a set of cell indexes.
     * The free centre cell is always considered marked.
     */
    private val marked = mutableSetOf<Int>()

    /**
     * The numbers that have been called this round, in call order.
     */
    private val drawnNumbers = mutableListOf<Int>()

    /**
     * The most recently called number (null before the first call).
     */
    private var lastDrawn: Int? = null

    /**
     * True once a full row, column, or diagonal is marked.
     */
    var isGameWon: Boolean = false
        private set

    /**
     * The special value stored in the centre cell to represent the FREE space.
     */
    private val FREE_SPACE = 0

    init {
        // Build the initial card the first time the game is created.
        newCard()
    }

    /**
     * Generates a brand-new random card and resets the round state.
     */
    fun newCard() {
        // Generate a random number for each column from that column's range.
        card = IntArray(SIZE * SIZE)
        val used = mutableSetOf<Int>()

        for (col in 0 until SIZE) {
            // Shuffle the column range and take SIZE unique numbers.
            val range = COLUMN_RANGES[col].shuffled()
            for (row in 0 until SIZE) {
                val value = range[row]
                card[row * SIZE + col] = value
                used.add(value)
            }
        }

        // The centre cell is the FREE space.
        card[12] = FREE_SPACE

        // Reset the round state.
        marked.clear()
        // Mark the free centre cell immediately.
        marked.add(12)
        drawnNumbers.clear()
        lastDrawn = null
        isGameWon = false
    }

    /**
     * Resets the round (draws are cleared and a fresh card is generated).
     */
    fun reset() {
        newCard()
    }

    /**
     * Calls (draws) a single random number from the full 1-75 pool that has
     * not been called yet.
     *
     * If the called number is on the card, that cell is marked. After marking,
     * the method checks for a completed line and sets [isGameWon] if found.
     *
     * @return the number that was called, or null if every number has already
     *         been called (the round is exhausted).
     */
    fun drawNumber(): Int? {
        if (isGameWon) return null

        // All numbers 1..75 that have not been called yet.
        val remaining = (1..75).filter { it !in drawnNumbers }
        if (remaining.isEmpty()) return null

        // Call one remaining number at random.
        val drawn = remaining[Random.nextInt(remaining.size)]
        drawnNumbers.add(drawn)
        lastDrawn = drawn

        // Mark the card cell (if any) that holds this number.
        val index = card.indexOf(drawn)
        if (index >= 0) {
            marked.add(index)
        }

        // Check whether any line is now complete.
        if (hasWinningLine()) {
            isGameWon = true
        }
        return drawn
    }

    /**
     * Checks whether any full row, column, or diagonal is marked.
     *
     * @return true if the player has a winning line.
     */
    fun hasWinningLine(): Boolean {
        // Check each row.
        for (row in 0 until SIZE) {
            var complete = true
            for (col in 0 until SIZE) {
                if (!isCellMarked(row, col)) {
                    complete = false
                    break
                }
            }
            if (complete) return true
        }

        // Check each column.
        for (col in 0 until SIZE) {
            var complete = true
            for (row in 0 until SIZE) {
                if (!isCellMarked(row, col)) {
                    complete = false
                    break
                }
            }
            if (complete) return true
        }

        // Check the main diagonal (top-left to bottom-right).
        var complete = true
        for (i in 0 until SIZE) {
            if (!isCellMarked(i, i)) {
                complete = false
                break
            }
        }
        if (complete) return true

        // Check the anti-diagonal (top-right to bottom-left).
        complete = true
        for (i in 0 until SIZE) {
            if (!isCellMarked(i, SIZE - 1 - i)) {
                complete = false
                break
            }
        }
        return complete
    }

    /**
     * @return true if the cell at [row],[col] is marked (or is the free space).
     */
    private fun isCellMarked(row: Int, col: Int): Boolean {
        return (row * SIZE + col) in marked
    }

    /**
     * @return the value stored in the card at [row],[col]. Returns 0 for the
     *         FREE centre space.
     */
    fun getCell(row: Int, col: Int): Int = card[row * SIZE + col]

    /**
     * @return true if the cell at [row],[col] is marked.
     */
    fun isMarked(row: Int, col: Int): Boolean = (row * SIZE + col) in marked

    /**
     * @return the most recently called number, or null if none yet.
     */
    fun getLastDrawn(): Int? = lastDrawn

    /**
     * @return the numbers called so far this round, in call order.
     */
    fun getDrawnNumbers(): List<Int> = drawnNumbers.toList()

    /**
     * @return the five Bingo numbers as a comma-separated string, suitable for
     *         storing in the SQLite database. This takes the first five numbers
     *         that were called this round.
     */
    fun getFiveBingoNumbers(): String {
        val five = drawnNumbers.take(5)
        return five.joinToString(",")
    }
}
