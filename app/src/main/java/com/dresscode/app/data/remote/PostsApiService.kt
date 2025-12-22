
package com.dresscode.app.data.remote

import com.dresscode.app.data.model.ApiResponse
import com.dresscode.app.data.model.CreatePostRequest
import com.dresscode.app.data.model.PaginatedResponse
import com.dresscode.app.data.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface PostsApiService {

    @GET("posts")
    suspend fun getPosts(@QueryMap filters: Map<String, String>): Response<ApiResponse<PaginatedResponse<Post>>>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") postId: Long): Response<ApiResponse<Post>>

    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<ApiResponse<Unit>>

    @POST("posts/{id}/favorite")
    suspend fun favoritePost(@Path("id") postId: Long): Response<ApiResponse<Unit>>

    @DELETE("posts/{id}/favorite")
    suspend fun unfavoritePost(@Path("id") postId: Long): Response<ApiResponse<Unit>>
}
