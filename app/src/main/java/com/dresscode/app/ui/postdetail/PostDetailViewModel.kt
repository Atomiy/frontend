
package com.dresscode.app.ui.postdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dresscode.app.data.model.Post
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.repository.PostsRepository
import kotlinx.coroutines.launch

class PostDetailViewModel : ViewModel() {

    private val repository = PostsRepository()

    private val _post = MutableLiveData<Result<Post>>()
    val post: LiveData<Result<Post>> = _post

    fun fetchPost(postId: Long) {
        viewModelScope.launch {
            _post.value = Result.Loading
            try {
                val response = repository.getPostById(postId)
                if (response.isSuccessful && response.body()?.code == 0) {
                    _post.value = Result.Success(response.body()!!.data!!)
                } else {
                    val error = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to fetch post"
                    _post.value = Result.Error(Exception(error))
                }
            } catch (e: Exception) {
                _post.value = Result.Error(e)
            }
        }
    }
}
