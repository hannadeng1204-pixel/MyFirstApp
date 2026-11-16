package hk.edu.thei.mti.bingogame

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper manages the app's SQLite database.
 *
 * It extends [SQLiteOpenHelper], which handles creating and upgrading the
 * database file for us. The database stores the history of finished game
 * rounds with the following attributes (as required):
 *
 *   1. Username      — the player's name.
 *   2. Round number  — which round of the game it was.
 *   3. Five Bingo    — the five Bingo numbers that were drawn.
 *   4. System time   — when the record was saved.
 *
 * All database access is encapsulated here so the rest of the app never has
 * to write raw SQL. Callers just use [insertRecord], [getAllRecords], etc.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Called the first time the database is created.
     *
     * @param db the empty database to initialise with our table.
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Create the "game_records" table. The SQL uses:
        //   _id             -> auto-incrementing primary key.
        //   username        -> TEXT, the player's name.
        //   round_number    -> INTEGER, the round number of the game.
        //   bingo_numbers   -> TEXT, the five Bingo numbers (comma separated).
        //   system_time     -> INTEGER, the save time in epoch milliseconds.
        db.execSQL(CREATE_TABLE_SQL)
    }

    /**
     * Called when the database version increases (an app update).
     *
     * For this simple project we simply drop and recreate the table. In a
     * production app you would normally write migration logic to preserve data.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /**
     * Inserts a new game record into the database.
     *
     * @param username    the player's name.
     * @param roundNumber the round number of the game.
     * @param bingoNumbers the five Bingo numbers as a comma-separated string.
     * @param systemTime  the save time in epoch milliseconds.
     * @return the row id of the newly inserted record, or -1 on failure.
     */
    fun insertRecord(
        username: String,
        roundNumber: Int,
        bingoNumbers: String,
        systemTime: Long
    ): Long {
        // ContentValues is a key/value map Android uses to safely write a row.
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_ROUND_NUMBER, roundNumber)
            put(COLUMN_BINGO_NUMBERS, bingoNumbers)
            put(COLUMN_SYSTEM_TIME, systemTime)
        }
        // writableDatabase opens (and creates, if needed) the DB for writing.
        return writableDatabase.insert(TABLE_NAME, null, values)
    }

    /**
     * Returns every game record, ordered by the most recent system time first.
     *
     * @return a list of [GameRecord] objects (empty if no records exist yet).
     */
    fun getAllRecords(): List<GameRecord> {
        val records = mutableListOf<GameRecord>()
        // Query the table, ordered newest first.
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,                                    // all columns
            null, null, null, null,
            "$COLUMN_SYSTEM_TIME DESC"               // sort by time descending
        )
        // Use the cursor "use" block so it is always closed afterwards.
        cursor.use {
            // Get the column indexes once for speed.
            val idIndex = it.getColumnIndexOrThrow(COLUMN_ID)
            val userIndex = it.getColumnIndexOrThrow(COLUMN_USERNAME)
            val roundIndex = it.getColumnIndexOrThrow(COLUMN_ROUND_NUMBER)
            val bingoIndex = it.getColumnIndexOrThrow(COLUMN_BINGO_NUMBERS)
            val timeIndex = it.getColumnIndexOrThrow(COLUMN_SYSTEM_TIME)

            // Walk through each row and build a GameRecord object.
            while (it.moveToNext()) {
                records.add(
                    GameRecord(
                        id = it.getLong(idIndex),
                        username = it.getString(userIndex),
                        roundNumber = it.getInt(roundIndex),
                        bingoNumbers = it.getString(bingoIndex),
                        systemTime = it.getLong(timeIndex)
                    )
                )
            }
        }
        return records
    }

    /**
     * Returns the highest round number currently stored for a given username.
     *
     * This is used so a new round continues counting from where the player
     * left off (round 1, then 2, then 3, ...).
     *
     * @param username the player whose latest round number to look up.
     * @return the highest round number found, or 0 if the player has no records.
     */
    fun getMaxRoundNumber(username: String): Int {
        val db = readableDatabase
        // SELECT MAX(round_number) ... WHERE username = ?
        val cursor = db.rawQuery(
            "SELECT MAX($COLUMN_ROUND_NUMBER) FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ?",
            arrayOf(username)
        )
        return cursor.use {
            if (it.moveToFirst() && !it.isNull(0)) it.getInt(0) else 0
        }
    }

    /**
     * Deletes all records from the database.
     *
     * Useful for a "Clear history" action.
     */
    fun deleteAllRecords() {
        writableDatabase.delete(TABLE_NAME, null, null)
    }

    // The companion object holds constants shared by every DatabaseHelper.
    companion object {
        private const val DATABASE_NAME = "bingo_game.db"   // the DB file name
        private const val DATABASE_VERSION = 1              // schema version

        private const val TABLE_NAME = "game_records"       // the table name

        // Column names (also used as map keys / SQL identifiers).
        private const val COLUMN_ID = "_id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_ROUND_NUMBER = "round_number"
        private const val COLUMN_BINGO_NUMBERS = "bingo_numbers"
        private const val COLUMN_SYSTEM_TIME = "system_time"

        // The SQL statement that creates the table on first launch.
        private const val CREATE_TABLE_SQL = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_ROUND_NUMBER INTEGER NOT NULL,
                $COLUMN_BINGO_NUMBERS TEXT NOT NULL,
                $COLUMN_SYSTEM_TIME INTEGER NOT NULL
            )
        """
    }
}
