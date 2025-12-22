package com.dresscode.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData // 需要 lifecycle-livedata-ktx
import androidx.lifecycle.viewModelScope
import com.dresscode.app.data.model.Post
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.repository.PostsRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // 建议：实际项目中通常使用依赖注入 (Hilt/Koin) 注入 Repository，而不是直接实例化
    private val postsRepository = PostsRepository()

    // 假设 postsRepository.allPosts 返回的是 Flow<List<Post>>
    val posts: LiveData<List<Post>> = postsRepository.allPosts.asLiveData()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _favoriteResult = MutableLiveData<Result<Long>>()
    val favoriteResult: LiveData<Result<Long>> = _favoriteResult
    
    private val currentFilters = mutableMapOf<String, String>()

    init {
        refreshPosts()
    }

    fun refreshPosts() {
        viewModelScope.launch {
            try {
                postsRepository.refreshPosts(currentFilters)
            } catch (e: Exception) {
                _error.value = "Failed to load posts: ${e.message}"
            }
        }
    }

    fun applyFilter(key: String, value: String?) {
        if (value.isNullOrBlank()) {
            currentFilters.remove(key)
        } else {
            currentFilters[key] = value
        }
        refreshPosts()
    }

    fun toggleFavorite(post: Post) {
        viewModelScope.launch {
            _favoriteResult.value = Result.Loading
            try {
                val response = if (post.isFavorited) {
                    postsRepository.unfavoritePost(post.id)
                } else {
                    postsRepository.favoritePost(post.id)
                }

                if (response.isSuccessful && response.body()?.code == 0) {
                    _favoriteResult.value = Result.Success(post.id)
                    refreshPosts()
                } else {
                    val error = response.body()?.message ?: "Favorite operation failed"
                    _favoriteResult.value = Result.Error(Exception(error))
                }
            } catch (e: Exception) {
                _favoriteResult.value = Result.Error(e)
            }
        }
    }
}