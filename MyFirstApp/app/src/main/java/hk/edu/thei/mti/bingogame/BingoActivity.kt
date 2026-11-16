package hk.edu.thei.mti.bingogame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * BingoActivity hosts the Bingo game screen.
 *
 * It renders a 5x5 Bingo card (random numbers per column, with a FREE centre
 * cell), a status text view, and the action buttons. Every time the player
 * wins a round, the result is saved to the SQLite database via [DatabaseHelper]
 * with the required attributes:
 *
 *   1. Username      — read from SharedPreferences (set at login).
 *   2. Round number  — incremented for each round the player completes.
 *   3. Five Bingo    — the first five numbers called this round.
 *   4. System time   — the time the round finished.
 */
class BingoActivity : AppCompatActivity() {

    // The game engine that holds the state of the current round.
    private val game = BingoGame()

    // UI references (initialised in onCreate).
    private lateinit var tvStatus: TextView
    private lateinit var tvLastDrawn: TextView
    private lateinit var grid: GridLayout
    private lateinit var btnDraw: Button
    private lateinit var btnNewRound: Button
    private lateinit var btnGuessGame: Button
    private lateinit var btnHistory: Button

    // Holds a reference to each of the 25 card buttons (row-major order).
    private val numberButtons = mutableListOf<Button>()

    // Database helper used to persist finished rounds.
    private lateinit var dbHelper: DatabaseHelper

    // The current player's username, loaded from SharedPreferences.
    private var username = "Player"

    // The round number for the *next* round the player completes.
    private var currentRound = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bingo)

        // Bind the UI controls from the layout.
        tvStatus = findViewById(R.id.tvBingoStatus)
        tvLastDrawn = findViewById(R.id.tvLastDrawn)
        grid = findViewById(R.id.gridBingo)
        btnDraw = findViewById(R.id.btnDraw)
        btnNewRound = findViewById(R.id.btnNewRound)
        btnGuessGame = findViewById(R.id.btnGuessGame)
        btnHistory = findViewById(R.id.btnHistory)

        // Create the database helper (opens/creates the DB as needed).
        dbHelper = DatabaseHelper(this)

        // Load the logged-in username from SharedPreferences.
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        username = sharedPref.getString("username", "Player") ?: "Player"

        // Determine the next round number for this player.
        // getMaxRoundNumber returns the highest round already saved; the next
        // round is therefore (max + 1).
        currentRound = dbHelper.getMaxRoundNumber(username) + 1

        // Build the 5x5 grid of buttons.
        buildNumberGrid()

        // Start the first round.
        startNewRound()

        // "Draw Number" -> call one number and update the UI.
        btnDraw.setOnClickListener { onDrawNumber() }

        // "New Round" -> reset the game and start fresh.
        btnNewRound.setOnClickListener { startNewRound() }

        // "Guess Game" -> open the number-guessing game screen.
        btnGuessGame.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // "History" -> open the SQLite history screen.
        btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    /**
     * Creates the 5x5 grid of buttons and adds them to the layout.
     *
     * The buttons are labelled from the game's card data. They are not
     * directly clickable for marking (marking happens when a number is
     * called); they only visually reflect the card and its marked state.
     */
    private fun buildNumberGrid() {
        grid.columnCount = BingoGame.SIZE   // 5 columns.
        grid.removeAllViews()               // clear any old buttons.
        numberButtons.clear()

        // Add one button per cell (row-major order).
        for (row in 0 until BingoGame.SIZE) {
            for (col in 0 until BingoGame.SIZE) {
                val button = Button(this).apply {
                    // Label with the card number, or "FREE" for the centre cell.
                    text = cardLabel(row, col)
                    // Make each button an equal-width cell.
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = dp(64)
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        setMargins(dp(2), dp(2), dp(2), dp(2))
                    }
                    setOnClickListener {
                        Toast.makeText(
                            this@BingoActivity,
                            "Cell: ${cardLabel(row, col)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                grid.addView(button)
                numberButtons.add(button)
            }
        }
    }

    /**
     * Returns the display label for a card cell.
     *
     * The centre cell (row 2, col 2) is the FREE space; everything else shows
     * its number.
     */
    private fun cardLabel(row: Int, col: Int): String {
        val value = game.getCell(row, col)
        return if (value == 0) "FREE" else value.toString()
    }

    /**
     * Starts a brand-new Bingo round: generates a fresh card, resets the game,
     * and refreshes the UI.
     */
    private fun startNewRound() {
        game.reset()
        refreshGridLabels()
        updateStatus()
        updateLastDrawn()
        updateGrid()
        // Re-enable drawing (in case it was disabled after a win).
        btnDraw.isEnabled = true
    }

    /**
     * Handles the "Draw Number" button press.
     *
     * Calls one number, refreshes the grid/status, and if the round was won,
     * saves the result to the database and disables further drawing.
     */
    private fun onDrawNumber() {
        val drawn = game.drawNumber() ?: return
        updateLastDrawn()
        updateGrid()

        if (game.isGameWon) {
            // The player has completed a line -> win!
            tvStatus.text = getString(R.string.bingo_win)
            btnDraw.isEnabled = false
            saveRoundToDatabase()
        } else {
            tvStatus.text = getString(R.string.bingo_lose)
        }
    }

    /**
     * Saves the finished round to SQLite.
     *
     * Stores all four required attributes: username, round number, the five
     * Bingo numbers (as a comma-separated string), and the current system time.
     */
    private fun saveRoundToDatabase() {
        // The five Bingo numbers from the game engine.
        val bingoNumbers = game.getFiveBingoNumbers()
        // Current system time in epoch milliseconds.
        val systemTime = System.currentTimeMillis()

        // Insert the record and report success/failure to the user.
        val id = dbHelper.insertRecord(username, currentRound, bingoNumbers, systemTime)
        if (id != -1L) {
            Toast.makeText(
                this,
                "Round $currentRound saved to history!",
                Toast.LENGTH_LONG
            ).show()
            // Advance the round number for the next win.
            currentRound++
        } else {
            Toast.makeText(this, "Failed to save record", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Updates the "last drawn" text view with the most recent number (if any).
     */
    private fun updateLastDrawn() {
        val last = game.getLastDrawn()
        tvLastDrawn.text = if (last != null) {
            getString(R.string.last_drawn, last)
        } else {
            getString(R.string.last_drawn, "-")
        }
    }

    /**
     * Updates the status text view based on the current game state.
     */
    private fun updateStatus() {
        tvStatus.text = getString(R.string.bingo_prompt)
    }

    /**
     * Re-reads the card labels and refreshes every button's text.
     *
     * Called when a new card is generated so the buttons show the new numbers.
     */
    private fun refreshGridLabels() {
        var index = 0
        for (row in 0 until BingoGame.SIZE) {
            for (col in 0 until BingoGame.SIZE) {
                numberButtons[index].text = cardLabel(row, col)
                index++
            }
        }
    }

    /**
     * Refreshes the visual appearance of the 25 card buttons.
     *
     * A button is highlighted (and disabled) once its number has been called,
     * so the player can see which numbers are already marked on the card.
     */
    private fun updateGrid() {
        var index = 0
        for (row in 0 until BingoGame.SIZE) {
            for (col in 0 until BingoGame.SIZE) {
                val button = numberButtons[index]
                if (game.isMarked(row, col)) {
                    // Marked cells are visually distinct and no longer clickable.
                    button.isEnabled = false
                    button.setBackgroundColor(getColor(android.R.color.darker_gray))
                } else {
                    // Un-marked cells are restored to their normal appearance.
                    button.isEnabled = true
                    button.setBackgroundColor(getColor(android.R.color.transparent))
                }
                index++
            }
        }
    }

    /**
     * Converts a density-independent pixel (dp) value to pixels for this device.
     *
     * @param value the dp value to convert.
     * @return the equivalent value in device pixels.
     */
    private fun dp(value: Int): Int {
        val density = resources.displayMetrics.density
        return (value * density).toInt()
    }
}
