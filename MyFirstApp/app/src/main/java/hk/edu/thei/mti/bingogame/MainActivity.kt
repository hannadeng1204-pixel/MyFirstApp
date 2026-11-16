package hk.edu.thei.mti.bingogame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

/**
 * MainActivity is the number-guessing game screen.
 *
 * The computer picks a secret number within a configurable range (0..100 or
 * 0..500) and the player tries to guess it. This screen is reached from the
 * Bingo game (which is now the first screen after login) and also offers
 * navigation to the selection demo, settings, history, and logout.
 *
 * The player's username and the selected range are read from SharedPreferences
 * (which are set by LoginActivity and SettingsActivity respectively).
 */
class MainActivity : AppCompatActivity() {

    // ---- UI references ----
    private lateinit var tvMessage: TextView   // shows instructions/feedback
    private lateinit var etGuess: EditText     // where the player types a guess
    private lateinit var btnGuess: Button      // the confirm / play-again button

    // ---- Game state ----
    private var secretNumber = 0   // the number the player must guess
    private var gameOver = false   // true after the player wins the round
    private var maxNumber = 100    // the upper bound of the guessing range

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Load the layout and initialise the base activity.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. Bind the guessing UI controls.
        tvMessage = findViewById(R.id.tvMessage)
        etGuess = findViewById(R.id.etGuess)
        btnGuess = findViewById(R.id.btnGuess)

        // 3. Bind the navigation buttons.
        val btnGoToSelection = findViewById<Button>(R.id.btnGoToSelection)
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnBingo = findViewById<Button>(R.id.btnBingo)
        val btnHistory = findViewById<Button>(R.id.btnHistory)

        // 4. Read the saved configuration (username and range).
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        maxNumber = sharedPref.getInt("maxNumber", 100)
        val username = sharedPref.getString("username", "Player") ?: "Player"

        // 5. Show a personalised welcome message.
        tvMessage.text = "Welcome $username!\nGuess a number (0 - $maxNumber):"

        // 6. Navigation: open the selection (checkbox/radio) demo screen.
        btnGoToSelection.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }

        // 7. Navigation: open the settings screen.
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // 8. Navigation: open the Bingo game screen.
        btnBingo.setOnClickListener {
            startActivity(Intent(this, BingoActivity::class.java))
        }

        // 9. Navigation: open the game history (SQLite) screen.
        btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // 10. Logout: clear saved login state and return to the login screen.
        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            // Clear the whole activity stack so the user cannot press "back"
            // and return to the game after logging out.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 11. Start the first guessing round.
        startNewGame()

        // 12. Handle the confirm button (guess / play-again logic).
        btnGuess.setOnClickListener {
            // If the round is over, this button now means "Play Again".
            if (gameOver) {
                startNewGame()
                return@setOnClickListener
            }

            // Read and validate the player's input.
            val input = etGuess.text.toString().trim()
            val guess = input.toIntOrNull()

            // Reject non-numeric or out-of-range input.
            if (guess == null || guess !in 0..maxNumber) {
                tvMessage.text = "Your data is incorrect. Enter again!"
                etGuess.text.clear()
                return@setOnClickListener
            }

            if (guess == secretNumber) {
                // Correct guess -> the player wins this round.
                tvMessage.text = getString(R.string.win)
                gameOver = true
                btnGuess.text = getString(R.string.play_again)
                etGuess.isEnabled = false
            } else {
                // Wrong guess -> let the player try again.
                tvMessage.text = getString(R.string.wrong)
                etGuess.text.clear()
            }
        }
    }

    /**
     * Called when the activity returns to the foreground.
     *
     * If the player changed the range in the settings screen, we reset the
     * game so the new range takes effect immediately.
     */
    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val newMaxNumber = sharedPref.getInt("maxNumber", 100)

        // If the range changed, update it and restart the game.
        if (newMaxNumber != maxNumber) {
            maxNumber = newMaxNumber
            val username = sharedPref.getString("username", "Player")
            tvMessage.text = "Welcome $username!\nGuess a number (0 - $maxNumber):"
            startNewGame()
        }
    }

    /**
     * Starts a brand-new guessing round.
     *
     * Picks a new random secret number, resets the "game over" flag, clears
     * the input field and re-enables it.
     */
    private fun startNewGame() {
        // Generate a random secret number within [0, maxNumber].
        secretNumber = Random.nextInt(0, maxNumber + 1)
        gameOver = false
        // Reset the input field for the new round.
        etGuess.text.clear()
        etGuess.isEnabled = true
        btnGuess.text = getString(R.string.btn_confirm)
        etGuess.requestFocus()
    }
}
