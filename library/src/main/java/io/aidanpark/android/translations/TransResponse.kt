package io.aidanpark.android.translations

import java.lang.Exception

data class TransResponse (
    val request: TransRequest,
    var detectedLanguageCode: String = "",
    var transText: String = "",
    var exception: Exception? = null
) {
    override fun toString(): String {
        return "TransResponse(" +
                "request=$request, " +
                "detectedLanguageCode='$detectedLanguageCode', " +
                "transText='$transText'" +
                ")"
    }
}