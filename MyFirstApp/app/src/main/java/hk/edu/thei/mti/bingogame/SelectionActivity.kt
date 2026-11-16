package hk.edu.thei.mti.bingogame

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * SelectionActivity is a demonstration screen for Android selection widgets.
 *
 * It shows two CheckBoxes ("Customer 1" / "Customer 2") and a RadioGroup of
 * three meals (Breakfast / Lunch / Dinner). Pressing "Confirm" builds a
 * sentence describing the selections, and "Return" goes back to the game.
 */
class SelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        // 1. Bind all the controls.
        val cbCustomer1 = findViewById<CheckBox>(R.id.cbCustomer1)
        val cbCustomer2 = findViewById<CheckBox>(R.id.cbCustomer2)
        val rgMeals = findViewById<RadioGroup>(R.id.rgMeals)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val btnReturn = findViewById<Button>(R.id.btnReturn)

        // 2. Handle the Confirm button.
        btnConfirm.setOnClickListener {
            // Collect the names of the selected customers.
            val selectedCustomers = mutableListOf<String>()
            if (cbCustomer1.isChecked) selectedCustomers.add("Customer 1")
            if (cbCustomer2.isChecked) selectedCustomers.add("Customer 2")

            // Find which meal radio button is selected (-1 means none).
            val selectedRadioId = rgMeals.checkedRadioButtonId

            if (selectedCustomers.isEmpty() || selectedRadioId == -1) {
                // The player must select at least one customer and one meal.
                tvResult.text = "Please select customer and item."
            } else {
                // Read the label of the selected meal radio button.
                val selectedMeal = findViewById<RadioButton>(selectedRadioId).text.toString()
                // Join the customer names with a comma.
                val customerText = selectedCustomers.joinToString(", ")
                // Build the result sentence.
                tvResult.text = "$customerText select $selectedMeal."
            }
        }

        // 3. Handle the Return button: close this screen.
        btnReturn.setOnClickListener {
            finish() // return to the previous screen.
        }
    }
}
