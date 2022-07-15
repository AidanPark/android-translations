package io.aidanpark.android.translations

data class TransResponse (
    val request: TransRequest,
    var detectedLanguageCode: String = "",
    var transText: String = ""
) {
    override fun toString(): String {
        return "TransResponse(" +
                "request=$request, " +
                "detectedLanguageCode='$detectedLanguageCode', " +
                "transText='$transText'" +
                ")"
    }
}