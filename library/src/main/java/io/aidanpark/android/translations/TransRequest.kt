package io.aidanpark.android.translations

data class TransRequest(
    val sourceLanguageCode: String,
    val targetLanguageCode: String,
    val sourceText: String
) {
    override fun toString(): String {
        return "TransRequest(" +
                "sourceLanguageCode='$sourceLanguageCode', " +
                "targetLanguageCode='$targetLanguageCode', " +
                "sourceText='$sourceText'" +
                ")"
    }
}