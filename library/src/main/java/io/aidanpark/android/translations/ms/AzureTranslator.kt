package io.aidanpark.android.translations.ms

import io.aidanpark.android.translations.TransRequest
import io.aidanpark.android.translations.TransResponse
import io.aidanpark.android.translations.Translator
import io.aidanpark.android.translations.google.GoogleCloudTranslator
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * Translation function using Microsoft Azure cloud translation service.
 *
 * Parameter 'azure_cloud_translation_api_key' used as API KEY, must be not exposed.
 *
 * @version 1.0
 */
class AzureTranslator(
    private val azure_cloud_translation_api_key: String,
    private val azure_cloud_translation_region: String
) : Translator {

    companion object {
        val supportedLanguageCodes = arrayOf(
            "af", // Afrikaans
            "sq", // Albanian
            "am", // Amharic
            "ar", // Arabic
            "hy", // Armenian
            "as", // Assamese
            "az", // Azerbaijani (Latin)
            "bn", // Bangla
            "ba", // Bashkir
            "eu", // Basque
            "bs", // Bosnian (Latin)
            "bg", // Bulgarian
            "yue", // Cantonese (Traditional)
            "ca", // Catalan
            "lzh", // Chinese (Literary)
            "zh-Hans", // Chinese Simplified
            "zh-Hant", // Chinese Traditional
            "hr", // Croatian
            "cs", // Czech
            "da", // Danish
            "prs", // Dari
            "dv", // Divehi
            "nl", // Dutch
            "en", // English
            "et", // Estonian
            "fo", // Faroese
            "fj", // Fijian
            "fil", // Filipino
            "fi", // Finnish
            "fr", // French
            "fr-ca", // French (Canada)
            "gl", // Galician
            "ka", // Georgian
            "de", // German
            "el", // Greek
            "gu", // Gujarati
            "ht", // Haitian Creole
            "he", // Hebrew
            "hi", // Hindi
            "mww", // Hmong Daw (Latin)
            "hu", // Hungarian
            "is", // Icelandic
            "id", // Indonesian
            "ikt", // Inuinnaqtun
            "iu", // Inuktitut
            "iu-Latn", // Inuktitut (Latin)
            "ga", // Irish
            "it", // Italian
            "ja", // Japanese
            "kn", // Kannada
            "kk", // Kazakh
            "km", // Khmer
            "tlh-Latn", // Klingon
            "tlh-Piqd", // Klingon (plqaD)
            "ko", // Korean
            "ku", // Kurdish (Central)
            "kmr", // Kurdish (Northern)
            "ky", // Kyrgyz (Cyrillic)
            "lo", // Lao
            "lv", // Latvian
            "lt", // Lithuanian
            "mk", // Macedonian
            "mg", // Malagasy
            "ms", // Malay (Latin)
            "ml", // Malayalam
            "mt", // Maltese
            "mi", // Maori
            "mr", // Marathi
            "mn-Cyrl", // Mongolian (Cyrillic)
            "mn-Mong", // Mongolian (Traditional)
            "my", // Myanmar
            "ne", // Nepali
            "nb", // Norwegian
            "or", // Odia
            "ps", // Pashto
            "fa", // Persian
            "pl", // Polish
            "pt", // Portuguese (Brazil)
            "pt-pt", // Portuguese (Portugal)
            "pa", // Punjabi
            "otq", // Queretaro Otomi
            "ro", // Romanian
            "ru", // Russian
            "sm", // Samoan (Latin)
            "sr-Cyrl", // Serbian (Cyrillic)
            "sr-Latn", // Serbian (Latin)
            "sk", // Slovak
            "sl", // Slovenian
            "so", // Somali (Arabic)
            "es", // Spanish
            "sw", // Swahili (Latin)
            "sv", // Swedish
            "ty", // Tahitian
            "ta", // Tamil
            "tt", // Tatar (Latin)
            "te", // Telugu
            "th", // Thai
            "bo", // Tibetan
            "ti", // Tigrinya
            "to", // Tongan
            "tr", // Turkish
            "tk", // Turkmen (Latin)
            "uk", // Ukrainian
            "hsb", // Upper Sorbian
            "ur", // Urdu
            "ug", // Uyghur (Arabic)
            "uz", // Uzbek (Latin)
            "vi", // Vietnamese
            "cy", // Welsh
            "yua", // Yucatec Maya
            "zu", // Zulu
        )

        val supportedSourceLanguageCodes: List<String> = mutableListOf(*supportedLanguageCodes)
            .apply { add(0, "auto") }
            .toList()

        val supportedTargetLanguageCodes: List<String> = listOf(*supportedLanguageCodes)
    }

    override val supportedSourceLanguageCodes: List<String> = AzureTranslator.supportedSourceLanguageCodes

    override val supportedTargetLanguageCodes: List<String> = AzureTranslator.supportedTargetLanguageCodes

    override suspend fun translate(transRequest: TransRequest, onCompleted: (transResponse: TransResponse) -> Unit) {
        val transResponse = TransResponse(transRequest)

        val url = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=${transRequest.targetLanguageCode}${if (transRequest.sourceLanguageCode == "auto") "" else "&from=${transRequest.sourceLanguageCode}"}"
        //println("url : $url")
        val requestBody = "[{\n\t\"Text\": \"${transRequest.sourceText}\"\n}]".toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request: Request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Ocp-Apim-Subscription-Key", azure_cloud_translation_api_key)
            .addHeader("Ocp-Apim-Subscription-Region", azure_cloud_translation_region)
            .post(requestBody)
            .build()

        var response: Response? = null

        try {
            response = OkHttpClient().newCall(request).execute()
            //println("response.code() : ${response.code}")

            /*
                [
                  {
                    "detectedLanguage": {
                      "language": "ko",
                      "score": 1
                    },
                    "translations": [
                      {
                        "text": "A tiger runs through the mountains.\nIt runs very fast.",
                        "to": "en"
                      }
                    ]
                  }
                ]
             */
            if (response.code == 200) {
                val responseData = response.body!!.string()
                //println("responseData $responseData")
                val responseJsonArr = JSONArray(responseData)
                //println("responseJsonArr $responseJsonArr")

                transResponse.transText = responseJsonArr.getJSONObject(0).getJSONArray("translations").getJSONObject(0).getString("text")
                //println("transResponse.transText ${transResponse.transText}")

                try {
                    transResponse.detectedLanguageCode = responseJsonArr.getJSONObject(0).getJSONObject("detectedLanguage").getString("language")
                    //println("transResponse.detectedLanguageCode ${transResponse.detectedLanguageCode}")
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            response?.close()
        }

        onCompleted(transResponse)
    }

}