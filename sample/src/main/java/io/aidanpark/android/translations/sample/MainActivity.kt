package io.aidanpark.android.translations.sample

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import io.aidanpark.android.translations.TransRequest
import io.aidanpark.android.translations.sample.ui.theme.AndroidTranslationsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val TAG: String = "MainActivity"

    //private val transViewModel: GoogleJSViewModel by viewModels()
    private val transViewModel: AzureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTranslationsTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        translationTest()
    }

    private fun translationTest() {
        transViewModel.transResponse.observe(this) {
            Log.d(TAG, "transResponse observed $it")
        }

        val sourceLanguageCode = "auto" // "ko"
        val targetLanguageCode = "en"

        var sourceText = "호랑이가 산을 뛰어다닙니다.\n아주 빠르게 뛰어다닙니다."
        var transRequest = TransRequest(sourceLanguageCode, targetLanguageCode, sourceText)
        transViewModel.request(transRequest)

//        sourceText = "두번째 호랑이가 산을 뛰어다닙니다.\n아주 빠르게 뛰어다닙니다."
//        transRequest = TransRequest(sourceLanguageCode, targetLanguageCode, sourceText)
//        transViewModel.request(transRequest)
//
//        sourceText = "세번째 호랑이가 산을 뛰어다닙니다.\n아주 빠르게 뛰어다닙니다."
//        transRequest = TransRequest(sourceLanguageCode, targetLanguageCode, sourceText)
//        transViewModel.request(transRequest)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidTranslationsTheme {
        Greeting("Android")
    }
}