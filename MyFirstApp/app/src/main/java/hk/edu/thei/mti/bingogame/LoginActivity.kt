package hk.edu.thei.mti.bingogame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * LoginActivity is the entry (LAUNCHER) screen of the app.
 *
 * The player enters a username and password, and presses "Login". The
 * username is saved to SharedPreferences so it can be reused across screens
 * (for example, when saving a Bingo record to SQLite). If the user has
 * already logged in before, the app skips this screen and goes straight to
 * the game.
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Bind the username / password fields and the login button.
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Access the app's SharedPreferences (used to store login state).
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        // If the user is already logged in, skip the login screen entirely and
        // go straight to the Bingo game (the first screen after login).
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, BingoActivity::class.java))
            finish()
            return
        }

        // Handle the login button press.
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()

            // Basic validation: both fields must be non-empty.
            if (username.isEmpty() || etPassword.text.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            } else {
                // Save the login state and the username for later use.
                sharedPref.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("username", username)
                    .apply()

                // Go to the Bingo game screen (the first screen after login).
                startActivity(Intent(this, BingoActivity::class.java))
                finish()
            }
        }
    }
}
