package hk.edu.thei.mti.bingogame

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

/**
 * SettingsActivity lets the player configure the number-guessing range.
 *
 * The player can choose between 0..100 and 0..500. The selected value is
 * saved to SharedPreferences so MainActivity can read it and adjust the game.
 */
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Bind the two radio buttons and the save button.
        val rb100 = findViewById<RadioButton>(R.id.rb100)
        val rb500 = findViewById<RadioButton>(R.id.rb500)
        val btnSave = findViewById<Button>(R.id.btnSaveConfig)

        // Access the app's SharedPreferences.
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        // Read the current setting and check the matching radio button.
        val currentMax = sharedPref.getInt("maxNumber", 100)
        if (currentMax == 500) rb500.isChecked = true else rb100.isChecked = true

        // Save the selected range and close this screen.
        btnSave.setOnClickListener {
            val selectedMax = if (rb500.isChecked) 500 else 100
            sharedPref.edit().putInt("maxNumber", selectedMax).apply()
            finish() // close settings and return to the game.
        }
    }
}
