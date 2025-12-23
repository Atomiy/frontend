
package com.dresscode.app.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dresscode.app.data.model.Post
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.repository.PostsRepository
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val postsRepository = PostsRepository()

    private val _favoritePosts = MutableLiveData<Result<List<Post>>>()
    val favoritePosts: LiveData<Result<List<Post>>> = _favoritePosts

    fun fetchFavoritePosts() {
        viewModelScope.launch {
            _favoritePosts.value = Result.Loading
            try {
                // For now, load first page without pagination controls
                val response = postsRepository.getFavoritePosts(emptyMap())
                if (response.isSuccessful && response.body()?.code == 0) {
                    val posts = response.body()?.data?.data ?: emptyList()
                    _favoritePosts.value = Result.Success(posts)
                } else {
                    val error = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to fetch favorites"
                    _favoritePosts.value = Result.Error(Exception(error))
                }
            } catch (e: Exception) {
                _favoritePosts.value = Result.Error(e)
            }
        }
    }
}
