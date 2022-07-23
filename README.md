# android-translations [![](https://jitpack.io/v/AidanPark/android-translations.svg)](https://jitpack.io/#AidanPark/android-translations)
Provides free/paid translations in an Android environment.
Free translation is based on http requests made on the translation site and may not work properly without notification at any time.

```
/**
 * The interface that Translator must implement.
 * See sample app for implementation instructions.
 */
interface Translator {

    /**
     * Language codes for source languages supported by Translator.
     */
    val supportedSourceLanguageCodes : List<String>

    fun isSupportedAsSource(code: String): Boolean {
        return supportedSourceLanguageCodes.contains(code)
    }

    /**
     * Language codes for target languages supported by Translator.
     */
    val supportedTargetLanguageCodes : List<String>

    fun isSupportedAsTarget(code: String): Boolean {
        return supportedTargetLanguageCodes.contains(code)
    }

    /**
     * Request translation.
     * This function would block current thread and coroutine cannot be properly suspended.
     * Therefore, it must be used within 'withContext(Dispatchers.IO)' syntax.
     *
     * @param transRequest Translation request object
     * @param onCompleted Translation complete callback
     */
    suspend fun translate(transRequest: TransRequest, onCompleted: (transResponse :TransResponse) -> Unit)
}
```

