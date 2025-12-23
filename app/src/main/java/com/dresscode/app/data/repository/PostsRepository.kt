
package com.dresscode.app.data.repository

import com.dresscode.app.DressCodeApp
import com.dresscode.app.data.local.AppDatabase
import com.dresscode.app.data.remote.PostsApiService
import com.dresscode.app.data.remote.RetrofitClient

class PostsRepository {

    private val postsService: PostsApiService by lazy {
        RetrofitClient.instance.create(PostsApiService::class.java)
    }
    
    private val postDao = AppDatabase.getDatabase(DressCodeApp.appContext).postDao()
    
    val allPosts = postDao.getAllPosts()

    suspend fun refreshPosts(filters: Map<String, String>) {
        val response = postsService.getPosts(filters)
        if (response.isSuccessful && response.body()?.code == 0) {
            val posts = response.body()?.data?.data ?: emptyList()
            postDao.deleteAll()
            postDao.insertAll(posts)
        }
        // If the response is not successful, the exception will be thrown by Retrofit
        // and caught by the ViewModel.
    }

    suspend fun getFavoritePosts(pagination: Map<String, String>) = postsService.getFavoritePosts(pagination)

    suspend fun getPostById(postId: Long) = postsService.getPostById(postId)

    suspend fun createPost(request: com.dresscode.app.data.model.CreatePostRequest) = postsService.createPost(request)

    suspend fun favoritePost(postId: Long) = postsService.favoritePost(postId)

    suspend fun unfavoritePost(postId: Long) = postsService.unfavoritePost(postId)
}
