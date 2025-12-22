
package com.dresscode.app.ui.createpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dresscode.app.data.model.CreatePostRequest
import com.dresscode.app.data.repository.AiRepository
import com.dresscode.app.data.repository.PostsRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreatePostViewModel : ViewModel() {

    sealed class CreatePostState {
        object Idle : CreatePostState()
        object ImageUploading : CreatePostState()
        object PostPublishing : CreatePostState()
        object Success : CreatePostState()
        data class Error(val message: String) : CreatePostState()
    }

    private val postsRepository = PostsRepository()
    private val aiRepository = AiRepository() // For file upload

    private val _createState = MutableLiveData<CreatePostState>(CreatePostState.Idle)
    val createState: LiveData<CreatePostState> = _createState

    private var uploadedImageUrl: String? = null

    fun uploadPostImage(content: ByteArray) {
        viewModelScope.launch {
            _createState.value = CreatePostState.ImageUploading
            try {
                val requestBody = content.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", "post_image.jpg", requestBody)
                
                val response = aiRepository.uploadFile(part)

                if (response.isSuccessful && response.body()?.code == 0) {
                    uploadedImageUrl = response.body()?.data?.url
                    _createState.value = CreatePostState.Idle // Back to idle after upload
                } else {
                    _createState.value = CreatePostState.Error(response.body()?.message ?: "Image upload failed")
                }
            } catch (e: Exception) {
                _createState.value = CreatePostState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createPost(title: String, content: String, style: String, season: String, scene: String, tags: List<String>) {
        val imageUrl = uploadedImageUrl
        if (imageUrl == null) {
            _createState.value = CreatePostState.Error("Please select an image for your post.")
            return
        }

        viewModelScope.launch {
            _createState.value = CreatePostState.PostPublishing
            try {
                val request = CreatePostRequest(
                    title = title,
                    content = content,
                    style = style,
                    season = season,
                    scene = scene,
                    images = listOf(imageUrl),
                    tags = tags
                )
                val response = postsRepository.createPost(request)
                if (response.isSuccessful && response.body()?.code == 0) {
                    _createState.value = CreatePostState.Success
                } else {
                    _createState.value = CreatePostState.Error(response.body()?.message ?: "Failed to publish post")
                }
            } catch (e: Exception) {
                _createState.value = CreatePostState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun getUploadedImageUrl(): String? {
        return uploadedImageUrl
    }
}
