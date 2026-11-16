package hk.edu.thei.mti.bingogame

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * HistoryActivity displays every game round that has been saved to SQLite.
 *
 * It reads all records via [DatabaseHelper.getAllRecords] and renders them in
 * a scrollable text view. Each line shows the four required attributes:
 * username, round number, the five Bingo numbers, and the system time.
 */
class HistoryActivity : AppCompatActivity() {

    // The database helper used to read the stored records.
    private lateinit var dbHelper: DatabaseHelper

    // UI references.
    private lateinit var tvHistory: TextView
    private lateinit var btnRefresh: Button
    private lateinit var btnClear: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Bind the UI controls.
        tvHistory = findViewById(R.id.tvHistory)
        btnRefresh = findViewById(R.id.btnRefresh)
        btnClear = findViewById(R.id.btnClear)
        btnBack = findViewById(R.id.btnBack)

        // Create the database helper.
        dbHelper = DatabaseHelper(this)

        // Populate the history on first launch.
        refreshHistory()

        // "Refresh" -> re-read the database and update the display.
        btnRefresh.setOnClickListener { refreshHistory() }

        // "Clear" -> delete all records and refresh.
        btnClear.setOnClickListener {
            dbHelper.deleteAllRecords()
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()
            refreshHistory()
        }

        // "Back" -> close this screen.
        btnBack.setOnClickListener { finish() }
    }

    /**
     * Reads all records from SQLite and shows them in the text view.
     */
    private fun refreshHistory() {
        val records = dbHelper.getAllRecords()

        if (records.isEmpty()) {
            // No records yet -> show a friendly message.
            tvHistory.text = getString(R.string.history_empty)
            return
        }

        // Build a readable multi-line summary of every record.
        val sb = StringBuilder()
        for (record in records) {
            sb.append("Username: ").append(record.username).append('\n')
            sb.append("Round: ").append(record.roundNumber).append('\n')
            sb.append("Bingo numbers: ").append(record.bingoNumbers).append('\n')
            sb.append("Time: ").append(formatTime(record.systemTime)).append('\n')
            sb.append("------------------------------\n")
        }
        tvHistory.text = sb.toString()
    }

    /**
     * Converts an epoch-millisecond timestamp into a human-readable string.
     *
     * @param epochMillis the timestamp in milliseconds since the Unix epoch.
     * @return a formatted date/time string such as "2026-11-16 21:40:26".
     */
    private fun formatTime(epochMillis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date(epochMillis))
    }
}
