package hk.edu.thei.mti.bingogame

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.assertEquals

/**
 * Example instrumented test.
 *
 * This test runs on an Android device or emulator, so it can use Android APIs.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    /**
     * Verifies that the app context is the expected package.
     */
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("hk.edu.thei.mti.bingogame", appContext.packageName)
    }
}
