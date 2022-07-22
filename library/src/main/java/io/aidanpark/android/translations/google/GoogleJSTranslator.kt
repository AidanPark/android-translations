package io.aidanpark.android.translations.google

import io.aidanpark.android.translations.TransRequest
import io.aidanpark.android.translations.TransResponse
import io.aidanpark.android.translations.Translator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.zip.GZIPInputStream
import javax.script.Invocable
import javax.script.ScriptEngineManager
import javax.script.ScriptException

/**
 * Translation function using Google translation {@link https://translate.google.com/} site.
 * This is based on an http request made on the Google Translation site, and it may not work properly without notice at any time.
 *
 * @version 1.0
 */
class GoogleJSTranslator : Translator {

    companion object {
        private val supportedLanguageCodes = arrayOf(
            "af",
            "ak",
            "am",
            "ar",
            "as",
            "ay",
            "az",
            "be",
            "bg",
            "bho",
            "bm",
            "bn",
            "bs",
            "ca",
            "ceb",
            "ckb",
            "co",
            "cs",
            "cy",
            "da",
            "de",
            "doi",
            "dv",
            "ee",
            "el",
            "en",
            "eo",
            "es",
            "et",
            "eu",
            "fa",
            "fi",
            "fr",
            "fy",
            "ga",
            "gd",
            "gl",
            "gn",
            "gom",
            "gu",
            "ha",
            "haw",
            "hi",
            "hmn",
            "hr",
            "ht",
            "hu",
            "hy",
            "id",
            "ig",
            "ilo",
            "is",
            "it",
            "iw",
            "ja",
            "jw",
            "ka",
            "kk",
            "km",
            "kn",
            "ko",
            "kri",
            "ku",
            "ky",
            "la",
            "lb",
            "lg",
            "ln",
            "lo",
            "lt",
            "lus",
            "lv",
            "mai",
            "mg",
            "mi",
            "mk",
            "ml",
            "mn",
            "mni-Mtei",
            "mr",
            "ms",
            "mt",
            "my",
            "ne",
            "nl",
            "no",
            "nso",
            "ny",
            "om",
            "or",
            "pa",
            "pl",
            "ps",
            "pt",
            "qu",
            "ro",
            "ru",
            "rw",
            "sa",
            "sd",
            "si",
            "sk",
            "sl",
            "sm",
            "sn",
            "so",
            "sq",
            "sr",
            "st",
            "su",
            "sv",
            "sw",
            "ta",
            "te",
            "tg",
            "th",
            "ti",
            "tk",
            "tl",
            "tr",
            "ts",
            "tt",
            "ug",
            "uk",
            "ur",
            "uz",
            "vi",
            "xh",
            "yi",
            "yo",
            "zh-CN",
            "zh-TW",
            "zu",
        )
    }

    private val tokenCreator: Invocable? by lazy {
        val token_js = "function token(a) {\n" +
                "    var k = \"\";\n" +
                "    var b = 406644;\n" +
                "    var b1 = 3293161072;\n" +
                "\n" +
                "    var jd = \".\";\n" +
                "    var sb = \"+-a^+6\";\n" +
                "    var Zb = \"+-3^+b+-f\";\n" +
                "\n" +
                "    for (var e = [], f = 0, g = 0; g < a.length; g++) {\n" +
                "        var m = a.charCodeAt(g);\n" +
                "        128 > m ? e[f++] = m: (2048 > m ? e[f++] = m >> 6 | 192 : (55296 == (m & 64512) && g + 1 < a.length && 56320 == (a.charCodeAt(g + 1) & 64512) ? (m = 65536 + ((m & 1023) << 10) + (a.charCodeAt(++g) & 1023), e[f++] = m >> 18 | 240, e[f++] = m >> 12 & 63 | 128) : e[f++] = m >> 12 | 224, e[f++] = m >> 6 & 63 | 128), e[f++] = m & 63 | 128)\n" +
                "    }\n" +
                "    a = b;\n" +
                "    for (f = 0; f < e.length; f++) a += e[f],\n" +
                "    a = RL(a, sb);\n" +
                "    a = RL(a, Zb);\n" +
                "    a ^= b1 || 0;\n" +
                "    0 > a && (a = (a & 2147483647) + 2147483648);\n" +
                "    a %= 1E6;\n" +
                "    return a.toString() + jd + (a ^ b)\n" +
                "};\n" +
                "\n" +
                "function RL(a, b) {\n" +
                "    var t = \"a\";\n" +
                "    var Yb = \"+\";\n" +
                "    for (var c = 0; c < b.length - 2; c += 3) {\n" +
                "        var d = b.charAt(c + 2),\n" +
                "        d = d >= t ? d.charCodeAt(0) - 87 : Number(d),\n" +
                "        d = b.charAt(c + 1) == Yb ? a >>> d: a << d;\n" +
                "        a = b.charAt(c) == Yb ? a + d & 4294967295 : a ^ d\n" +
                "    }\n" +
                "    return a\n" +
                "}"
        val inputStream = token_js.byteInputStream()
        val inputReader = InputStreamReader(inputStream)
        val buffReader = BufferedReader(inputReader)

        val engine = ScriptEngineManager().getEngineByName("js")
        engine.eval(buffReader)

        if (engine is Invocable) {
            engine
        } else {
            null
        }
    }

    private fun getToken(text: String): String {
        try {
            return tokenCreator?.invokeFunction("token", text).toString()
        } catch (e: ScriptException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
        return ""
    }

    override val supportedSourceLanguageCodes: List<String> by lazy {
        mutableListOf(*supportedLanguageCodes)
            .apply { add(0, "auto") }
            .toList()
    }

    override val supportedTargetLanguageCodes: List<String> by lazy {
        listOf(*supportedLanguageCodes)
    }

    override suspend fun translate(transRequest: TransRequest, onCompleted: (transResponse: TransResponse) -> Unit) {
        val transResponse = TransResponse(transRequest)

        val url: StringBuilder = StringBuilder()
        try {
            url.append("https://translate.google.com/translate_a/single?")
                .append("client=webapp")
                .append("&sl=${transRequest.sourceLanguageCode}")
                .append("&tl=${transRequest.targetLanguageCode}")
                .append("&hl=${transRequest.sourceLanguageCode}")
                .append("&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&source=btn&ssel=0&tsel=0&kc=1")
                .append("&tk=${getToken(transRequest.sourceText.trim { it <= ' ' })}")
                .append("&q=${URLEncoder.encode(transRequest.sourceText.trim { it <= ' ' }, "UTF-8")}")
        } catch (e: UnsupportedEncodingException) {
            transResponse.exception = e
            e.printStackTrace()
        }
        //println("req url : $url")

        val request: Request = Request.Builder()
            .url(url.toString())
            .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .addHeader("accept-encoding", "gzip, deflate, br")
            .addHeader("Content-Type", "application/json")
            .get()
            .build()

        var response: Response? = null

        try {
            response = OkHttpClient().newCall(request).execute()
            //println("response.code() : " + response.code)

            if (response.code == 200) {
                val `is` = response.body!!.byteStream()
                val gis = GZIPInputStream(`is`)
                val `in` = BufferedReader(InputStreamReader(gis, "UTF-8"))
                var inputLine: String?
                val sb = StringBuilder()
                while (`in`.readLine().also { inputLine = it } != null) {
                    sb.append(inputLine)
                }

                //print result
                val responseJson = sb.toString()
                //println("responseJson : $responseJson")
                /*
                    [
                      [
                        [
                          "The tiger runs around the mountain.",
                          "호랑이가 산을 뛰어다닙니다.",
                          null,
                          null,
                          3,
                          null,
                          null,
                          [[null, "offline"]],
                          [[["f4781aa9f1a33aa9cb1639e79945391d", "efficient_models_2022q2.md"]]]
                        ],
                        [null, null, null, "holang-iga san-eul ttwieodanibnida."]
                      ],
                      null,
                      "ko",
                      null,
                      null,
                      [
                        [
                          "호랑이가 산을 뛰어다닙니다.",
                          null,
                          [
                            ["The tiger runs around the mountain.", 0, true, false, [3], null, [[3]]],
                            ["The tiger runs around the mountains.", 0, true, false, [8]]
                          ],
                          [[0, 15]],
                          "호랑이가 산을 뛰어다닙니다.",
                          0,
                          0
                        ]
                      ],
                      1,
                      [],
                      [
                        ["ko"],
                        null,
                        [1],
                        ["ko"]
                      ]
                    ]
                 */
                val jsonArr = JSONArray(responseJson)
                val jsonArr_0 = jsonArr[0] as JSONArray
                val originStringBuilder = StringBuilder()
                val transStringBuilder = StringBuilder()
                for (i in 0 until jsonArr_0.length()) {
                    val jsonArr_0_n = jsonArr_0[i] as JSONArray
                    try {
                        val origin = jsonArr_0_n[1] as String
                        originStringBuilder.append(origin)
                        val trans = jsonArr_0_n[0] as String
                        transStringBuilder.append(trans)
                        //println("$i : $trans")
                    } catch (e: Exception) {
                    }
                }
                val originText = originStringBuilder.toString()

                transResponse.transText = transStringBuilder.toString()
                try {
                    transResponse.detectedLanguageCode = jsonArr[2] as String
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            transResponse.exception = e
        } finally {
            response?.close()
        }

        onCompleted(transResponse)
    }
}