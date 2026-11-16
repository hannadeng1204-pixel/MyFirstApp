package hk.edu.thei.mti.bingogame

/**
 * A single game record stored in the SQLite database.
 *
 * This data class is a plain "model" object: it holds the values for one row
 * of the game history table. It has no Android dependencies so it can be
 * reused in unit tests as well.
 *
 * @property id          The auto-increment primary key of the row (null until saved).
 * @property username    The name of the player who played the round.
 * @property roundNumber The round number of the game (1, 2, 3, ...).
 * @property bingoNumbers The five Bingo numbers that were drawn, joined as text.
 * @property systemTime  The date/time the record was created, in milliseconds
 *                       since the Unix epoch (the value returned by
 *                       [System.currentTimeMillis]).
 */
data class GameRecord(
    val id: Long = 0L,
    val username: String,
    val roundNumber: Int,
    val bingoNumbers: String,
    val systemTime: Long
) {
    /**
     * Returns the five Bingo numbers as a list of integers.
     *
     * The numbers are stored in the database as a comma-separated string,
     * for example "3,17,42,66,81". This helper splits that string back into
     * individual numbers for display or further processing.
     *
     * @return a list of the five integers, or an empty list if the field is blank.
     */
    fun bingoNumberList(): List<Int> {
        // Split the stored string on commas, trim whitespace, and keep only
        // the values that are actually valid integers.
        return bingoNumbers.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
    }
}
