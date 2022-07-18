package io.aidanpark.android.translations.google.mlkit

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import io.aidanpark.android.translations.TransRequest
import io.aidanpark.android.translations.TransResponse
import io.aidanpark.android.translations.Translator

/**
 * Translation function using Google MLKIT library.
 * @version 1.0
 */
class GoogleMLKitTranslator : Translator {

    private var _translatorOptions: TranslatorOptions? = null

    private var mlkitTranslator: com.google.mlkit.nl.translate.Translator? = null

    private fun getMLkitTranslator(translatorOptions: TranslatorOptions): com.google.mlkit.nl.translate.Translator {
        if (_translatorOptions != translatorOptions) _translatorOptions = translatorOptions
        if (mlkitTranslator == null) mlkitTranslator = Translation.getClient(translatorOptions)
        return mlkitTranslator!!
    }

    override val supportedSourceLanguageCodes: List<String> by lazy {
        TranslateLanguage.getAllLanguages()
    }

    override val supportedTargetLanguageCodes: List<String> by lazy {
        TranslateLanguage.getAllLanguages()
    }

    override suspend fun translate(transRequest: TransRequest, onCompleted: (transResponse: TransResponse) -> Unit) {
        val transResponse = TransResponse(transRequest)

        getMLkitTranslator(
            TranslatorOptions.Builder()
                .setSourceLanguage(transRequest.sourceLanguageCode)
                .setTargetLanguage(transRequest.targetLanguageCode)
                .build()
        ).run {
            downloadModelIfNeeded()
                .continueWith { downloadModelTask ->
                    if (downloadModelTask.isSuccessful) {
                        translate(transRequest.sourceText).addOnCompleteListener { translateTask ->
                            if (translateTask.isSuccessful) {
                                transResponse.transText = translateTask.result
                            } else {
                                transResponse.exception = translateTask.exception ?: Exception("[TranslateTask] Unknown error occurred.")
                            }
                            onCompleted(transResponse)
                        }
                    } else {
                        transResponse.exception = downloadModelTask.exception ?: Exception("[DownloadModelTask] Unknown error occurred.")
                        onCompleted(transResponse)
                    }
                }
        }
    }
}