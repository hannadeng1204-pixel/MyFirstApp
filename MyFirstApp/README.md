# MyFirstApp (Android)

An Android app written in Kotlin that combines a **Bingo game** and a
**number-guessing game**, and stores every finished Bingo round in a local
**SQLite database**.

This project was created by cleaning up an existing "MyfirstApp" project and
extending it with new features.

---

## Features

1. **Login screen** — enter a username (and password) to start; the username is
   remembered for the session. After login you go straight to the Bingo game.
2. **Bingo game** — a standard 75-ball 5×5 Bingo card (random numbers per
   column, with a FREE centre cell). Tap **Draw Number** to call a number; you
   win **BINGO!** as soon as you complete any full row, column, or diagonal.
3. **Number-guessing game** — the computer picks a secret number (range
   0–100 or 0–500, configurable in Settings) and the player guesses it.
   (Reached from the Bingo screen via "Guess Game".)
4. **SQLite persistence** — every finished Bingo round is saved with:
   - `username`
   - `round_number`
   - `bingo_numbers` (the five Bingo numbers called)
   - `system_time`
5. **History screen** — view all saved records, refresh, or clear them.
6. **Selection screen** — a demo of Android checkboxes and radio buttons.
7. **Detailed comments** — every source file is documented.

---

## Project structure

```
app/src/main/java/hk/edu/thei/mti/bingogame/
├── LoginActivity.kt        # Login screen (entry point)
├── BingoActivity.kt        # Bingo game screen (first screen after login)
├── BingoGame.kt            # Pure Bingo game logic (no UI)
├── MainActivity.kt         # Number-guessing game
├── HistoryActivity.kt      # Shows SQLite records
├── DatabaseHelper.kt       # SQLiteOpenHelper — creates/queries the DB
├── GameRecord.kt           # Data model for one DB row
├── SettingsActivity.kt     # Range settings
├── SelectionActivity.kt    # Selection widgets demo
└── ui/theme/               # Compose theme (Color, Theme, Type)
```

---

## Database schema

Table `game_records`:

| Column          | Type    | Description                          |
|-----------------|---------|--------------------------------------|
| `_id`           | INTEGER | Primary key (auto-increment)         |
| `username`      | TEXT    | The player's username                |
| `round_number`  | INTEGER | The round number of the game         |
| `bingo_numbers` | TEXT    | Five Bingo numbers (comma-separated) |
| `system_time`   | INTEGER | Save time (epoch milliseconds)       |

---

## Build & run

Requirements: Android Studio (with the Android SDK) or the Android command-line
tools, plus JDK 17+.

1. Open the project in Android Studio.
2. Let Gradle sync download dependencies.
3. Run the `app` configuration on a device or emulator (min SDK 24).

Or from the command line:

```bash
./gradlew assembleDebug        # build a debug APK
```

The APK is written to `app/build/outputs/apk/debug/`.

---

## Tests

- `app/src/test/.../ExampleUnitTest.kt` — JVM unit tests (includes a Bingo logic test).
- `app/src/androidTest/.../ExampleInstrumentedTest.kt` — on-device tests.

Run unit tests with:

```bash
./gradlew test
```

---

## Notes

- The login state and the guessing range are stored in `SharedPreferences`
  (not SQLite); only game history uses SQLite.
- The `local.properties` file (Android SDK path) is intentionally not committed;
  see `.gitignore`.
