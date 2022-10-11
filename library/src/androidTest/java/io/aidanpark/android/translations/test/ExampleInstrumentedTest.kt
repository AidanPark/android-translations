package io.aidanpark.android.translations.test

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.aidanpark.android.translations.TransRequest
import io.aidanpark.android.translations.google.GoogleCloudTranslator

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("io.aidanpark.android.translations.test", appContext.packageName)


//        val sourceLanguageCode = "ko" // "ko"
//        val targetLanguageCode = "en"
//        val sourceText = "호랑이가 산을 뛰어다닙니다.\n아주 빠르게 뛰어다닙니다."
//        val transRequest = TransRequest(sourceLanguageCode, targetLanguageCode, sourceText)
//        val translator: KakaoTranslatorTest = KakaoTranslatorTest()
//        translator.translateTest(transRequest) {
//
//        }


    }
}