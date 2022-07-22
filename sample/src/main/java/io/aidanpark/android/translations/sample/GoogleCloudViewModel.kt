package io.aidanpark.android.translations.sample

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import androidx.annotation.NonNull
import androidx.lifecycle.*
import com.google.common.io.BaseEncoding
import dagger.hilt.android.lifecycle.HiltViewModel
import io.aidanpark.android.translations.TransRequest
import io.aidanpark.android.translations.TransResponse
import io.aidanpark.android.translations.google.GoogleCloudTranslator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject


@HiltViewModel
class GoogleCloudViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val translator: GoogleCloudTranslator = GoogleCloudTranslator(
        applicationName = "Translation Sample",
        packageName = getApplication<Application>().packageName,
        signature = getSignature(),
        google_cloud_translation_api_key = "google_cloud_translation_api_key"
    )

    private val _transResponse: MutableLiveData<TransResponse> = MutableLiveData()

    val transResponse: LiveData<TransResponse> get() = _transResponse

    fun request(transRequest: TransRequest) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                translator.translate(transRequest) {
                    _transResponse.postValue(it)
                }
            }
        }
    }

    private fun getSignature(): String {
        return try {
            val signatures: List<String?> = getSignatures(getApplication<Application>().packageManager, getApplication<Application>().packageName)
            signatures.filterNotNull().first()
        } catch (e: NoSuchElementException) {
            "invalid_signature"
        }
    }

    private fun getSignatures(@NonNull pm: PackageManager, @NonNull packageName: String): List<String> {
        val returnThis = mutableListOf<String>()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                if (packageInfo?.signingInfo != null) {
                    if (packageInfo.signingInfo.hasMultipleSigners()) {
                        returnThis.addAll(signatureDigest(packageInfo.signingInfo.apkContentsSigners))
                    } else {
                        returnThis.addAll(signatureDigest(packageInfo.signingInfo.signingCertificateHistory))
                    }
                }
            } else {
                @SuppressLint("PackageManagerGetSignatures") val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                if (packageInfo?.signatures != null && packageInfo.signatures.size > 0 && packageInfo.signatures[0] != null) {
                    signatureDigest(packageInfo.signatures)
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return returnThis.toList()
    }

    private fun signatureDigest(sigList: Array<Signature>): List<String> {
        val signaturesList = mutableListOf<String>()
        for (signature in sigList) {
            signatureDigest(signature)?.let { signaturesList.add(it) }
        }
        return signaturesList
    }

    private fun signatureDigest(sig: Signature): String? {
        val signature: ByteArray = sig.toByteArray()
        return try {
            val md: MessageDigest = MessageDigest.getInstance("SHA1")
            val digest: ByteArray = md.digest(signature)
            BaseEncoding.base16().lowerCase().encode(digest)
        } catch (e: NoSuchAlgorithmException) {
            null
        }
    }


}
