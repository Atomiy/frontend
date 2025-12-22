
package com.dresscode.app.ui.tryon

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.repository.AiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class TryOnViewModel : ViewModel() {

    sealed class TryOnState {
        object Idle : TryOnState()
        object Uploading : TryOnState()
        data class Processing(val taskId: Long) : TryOnState()
        data class Success(val resultUrl: String) : TryOnState()
        data class Error(val message: String) : TryOnState()
    }

    private val repository = AiRepository()

    private val _tryOnState = MutableLiveData<TryOnState>(TryOnState.Idle)
    val tryOnState: LiveData<TryOnState> = _tryOnState

    private val _personImageUrl = MutableLiveData<String?>()
    val personImageUrl: LiveData<String?> = _personImageUrl

    private val _clothingImageUrl = MutableLiveData<String?>()
    val clothingImageUrl: LiveData<String?> = _clothingImageUrl

    fun setInitialClothingImage(url: String?) {
        _clothingImageUrl.value = url
    }

    fun uploadImage(type: String, content: ByteArray) {
        viewModelScope.launch {
            _tryOnState.value = TryOnState.Uploading
            try {
                val part = MultipartBody.Part.createFormData("file", "image.jpg", content.toRequestBody("image/jpeg".toMediaTypeOrNull()))
                val response = repository.uploadFile(part)

                if (response.isSuccessful && response.body()?.code == 0) {
                    val url = response.body()?.data?.url
                    if (type == "person") {
                        _personImageUrl.value = url
                    } else {
                        _clothingImageUrl.value = url
                    }
                    _tryOnState.value = TryOnState.Idle // Back to idle after upload
                } else {
                    _tryOnState.value = TryOnState.Error(response.body()?.message ?: "Upload failed")
                }
            } catch (e: Exception) {
                _tryOnState.value = TryOnState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun startTryOnProcess() {
        val pUrl = personImageUrl.value
        val cUrl = clothingImageUrl.value

        if (pUrl.isNullOrBlank()) {
            _tryOnState.value = TryOnState.Error("Please upload your photo first.")
            return
        }
        if (cUrl.isNullOrBlank()) {
            _tryOnState.value = TryOnState.Error("Please select a clothing item.")
            return
        }

        viewModelScope.launch {
            try {
                val request = com.dresscode.app.data.model.VirtualTryOnRequest(person_image_url = pUrl, clothing_image_url = cUrl)
                val response = repository.startVirtualTryOn(request)
                if (response.isSuccessful && response.body()?.code == 0) {
                    val taskId = response.body()?.data?.internal_task_id
                    if (taskId != null) {
                        _tryOnState.value = TryOnState.Processing(taskId)
                        pollForStatus(taskId)
                    } else {
                        _tryOnState.value = TryOnState.Error("Failed to get task ID.")
                    }
                } else {
                     _tryOnState.value = TryOnState.Error(response.body()?.message ?: "Failed to start process")
                }
            } catch (e: Exception) {
                _tryOnState.value = TryOnState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun pollForStatus(taskId: Long) {
        viewModelScope.launch {
            while (true) {
                try {
                    val response = repository.getVirtualTryOnStatus(taskId)
                    if (response.isSuccessful && response.body()?.code == 0) {
                        val status = response.body()?.data
                        when (status?.status) {
                            "SUCCEED" -> {
                                _tryOnState.value = TryOnState.Success(status.result_url ?: "")
                                return@launch // Stop polling
                            }
                            "FAILED" -> {
                                _tryOnState.value = TryOnState.Error("Processing failed.")
                                return@launch // Stop polling
                            }
                            "PENDING" -> {
                                // Continue polling
                            }
                        }
                    } else {
                         _tryOnState.value = TryOnState.Error(response.body()?.message ?: "Failed to get status")
                         return@launch
                    }
                } catch (e: Exception) {
                    _tryOnState.value = TryOnState.Error(e.message ?: "Unknown error")
                    return@launch
                }
                delay(3000) // Poll every 3 seconds
            }
        }
    }
}
