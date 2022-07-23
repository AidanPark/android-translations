package io.aidanpark.android.translations.sample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.aidanpark.android.translations.TransRequest
import io.aidanpark.android.translations.TransResponse
import io.aidanpark.android.translations.ms.AzureTranslator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AzureViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val translator: AzureTranslator = AzureTranslator(
        azure_cloud_translation_api_key = "azure_cloud_translation_api_key",
        azure_cloud_translation_region = "azure_cloud_translation_region"
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
}
