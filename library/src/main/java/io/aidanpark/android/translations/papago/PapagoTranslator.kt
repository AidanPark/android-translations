package io.aidanpark.android.translations.papago

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import io.aidanpark.android.translations.TransRequest
import io.aidanpark.android.translations.TransResponse
import io.aidanpark.android.translations.Translator
import io.aidanpark.android.translations.google.GoogleCloudTranslator
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLEncoder

@SuppressLint("SetJavaScriptEnabled")
class PapagoTranslator(val context: Context) : Translator {

    companion object {
        private val supportedLanguageCodes = arrayOf(
            "de", //
            "en", //
            "es", //
            "fr", //
            "hi", //
            "id", //
            "it", //
            "ja", //
            "ko", //
            "pt", //
            "ru", //
            "th", //
            "vi", //
            "zh-CN", //
            "zh-TW", //
        )

        val supportedSourceLanguageCodes: List<String> = mutableListOf(*supportedLanguageCodes)
            //.apply { add(0, "auto-detect") }
            .toList()

        val supportedTargetLanguageCodes: List<String> = listOf(*supportedLanguageCodes)
    }

    override val supportedSourceLanguageCodes: List<String> = PapagoTranslator.supportedSourceLanguageCodes

    override val supportedTargetLanguageCodes: List<String> = PapagoTranslator.supportedTargetLanguageCodes

    private var webView: WebView? = null

    /**
     * must called in Main Lopper
     *
     * @param transRequest
     * @param onCompleted
     */
    override suspend fun translate(transRequest: TransRequest, onCompleted: (transResponse: TransResponse) -> Unit) {
        if (webView == null) {
            webView = WebView(context).apply {
                this.webChromeClient = WebChromeClient()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.settings.safeBrowsingEnabled = false
                }
                this.settings.javaScriptEnabled = true
                this.settings.domStorageEnabled = true
                this.settings.javaScriptCanOpenWindowsAutomatically = true
                this.settings.userAgentString = "en-US"
                addJavascriptInterface(ResultDetector(), "ResultDetector")
            }
        }

        webView!!.webViewClient = object : WebViewClient() {
            override fun onLoadResource(view: WebView, url: String) {
                println("onLoadResource $url");
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                println("onPageFinished $url");
                //응답없음 감지
                //  detectResultTimeout()
                runRequestDetect(7000)
            }
        }

        val url: String = StringBuilder().append("https://papago.naver.com/?")
            .append("&sk=${transRequest.sourceLanguageCode}")
            .append("&tk=${transRequest.targetLanguageCode}")
            .append("&st=${URLEncoder.encode(transRequest.sourceText.trim { it <= ' ' }, "UTF-8")}")
            .toString()

        println("url : $url");

        //Log.d(TAG, "papago_translate_web_url : " + query_url);
        val headers: MutableMap<String, String> = HashMap()
        headers["accept-language"] = "en-US,en;q=0.9" //accept-language: en-US,en;q=0.9

        webView!!.loadUrl(url, headers)
    }

    private val requestDetectHandler = Handler()

    private val requestDetect = java.lang.Runnable { webView!!.loadUrl("javascript:window.ResultDetector.detectTranslate('<html>'+document.getElementsByTagName('body')[0].innerHTML+'</html>');") }

    private fun cancelRequestDetect() {
        requestDetectHandler.removeCallbacks(requestDetect)
    }

    private fun runRequestDetect(delay: Int) {
        //Log.i(TAG, "#### requestDetect(" + delay + ") ####");
        cancelRequestDetect()
        requestDetectHandler.postDelayed(requestDetect, delay.toLong())
    }

    private class ResultDetector {
        @JavascriptInterface
        fun detectTranslate(html: String?) {
            //Log.d(TAG, "### detectTranslate ###");
            //번역
            try {
                val document: Document = Jsoup.parse(html)
                val transText: String = document.select("div#txtTarget").text()
                //Log.d(TAG, "번역 transText [" + transText + "]");
                if (TextUtils.isEmpty(transText)) {
                    //  runRequestDetect(200)
                    return
                }

                try {
                    //"div.result-shield-container span.tlid-translation.translation span"
                    var detectedCode: String = document.select("button#ddSourceLanguageButton").text()
                    detectedCode = detectedCode.split("-").toTypedArray()[0].trim { it <= ' ' }
                    println("detectedCode: $detectedCode");
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}