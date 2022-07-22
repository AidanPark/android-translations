package io.aidanpark.android.translations.sample

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.aidanpark.android.translations.TransRequest
import io.aidanpark.android.translations.TransResponse
import io.aidanpark.android.translations.google.GoogleMLKitTranslator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GoogleMLKitViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val translator: GoogleMLKitTranslator = GoogleMLKitTranslator()

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
