package io.aidanpark.android.translations

/**
 * The interface that Translator must implement.
 * See sample app for implementation instructions.
 *
 * todo 번역 실패시 실패 메시지를 반환해야 함.
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