package hk.edu.thei.mti.bingogame

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

/**
 * Example local unit test.
 *
 * This test runs on the development machine (the JVM), not on an Android
 * device, so it can only test plain Kotlin/Java logic with no Android APIs.
 */
class ExampleUnitTest {

    /**
     * A trivial test that verifies basic integer arithmetic. It serves as a
     * sanity check that the JUnit test setup is working.
     */
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    /**
     * A unit test for the [BingoGame] logic: a fresh game should not be won,
     * and the centre FREE cell should always be marked from the start.
     */
    @Test
    fun bingoGame_newGame_isNotWon_andFreeIsMarked() {
        val game = BingoGame()
        // A new game must not already be won.
        assertFalse(game.isGameWon)
        // The centre cell (row 2, col 2) is the FREE space and is marked.
        assertTrue(game.isMarked(2, 2))
        // The card values in each column must fall in that column's range.
        for (col in 0 until BingoGame.SIZE) {
            val range = BingoGame.COLUMN_RANGES[col]
            for (row in 0 until BingoGame.SIZE) {
                val value = game.getCell(row, col)
                if (value != 0) {
                    assertTrue(value in range)
                }
            }
        }
    }

    /**
     * A unit test for the [BingoGame] line detection: calling enough numbers
     * to cover one full row should eventually produce a win. Because the card
     * is random, we instead verify the win-detection indirectly by drawing
     * numbers until either a win occurs or the pool is exhausted, and assert
     * that a win is always possible before exhausting all 75 numbers.
     */
    @Test
    fun bingoGame_hasWinningLineDetection() {
        val game = BingoGame()
        var won = false
        // Call up to all 75 numbers.
        repeat(75) {
            if (!game.isGameWon) {
                game.drawNumber()
                if (game.isGameWon) won = true
            }
        }
        // With 5 columns and a free centre, a full line is guaranteed to be
        // completed well before all 75 numbers are called.
        assertTrue(won)
    }
}
