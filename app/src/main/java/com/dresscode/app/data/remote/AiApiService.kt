
package com.dresscode.app.data.remote

import com.dresscode.app.data.model.ApiResponse
import com.dresscode.app.data.model.FileUploadResponse
import com.dresscode.app.data.model.VirtualTryOnRequest
import com.dresscode.app.data.model.VirtualTryOnResponse
import com.dresscode.app.data.model.VirtualTryOnStatus
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AiApiService {

    @Multipart
    @POST("upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<ApiResponse<FileUploadResponse>>

    @POST("ai/virtual-try-on/start")
    suspend fun startVirtualTryOn(@Body request: VirtualTryOnRequest): Response<ApiResponse<VirtualTryOnResponse>>

    @GET("ai/virtual-try-on/status/{id}")
    suspend fun getVirtualTryOnStatus(@Path("id") taskId: Long): Response<ApiResponse<VirtualTryOnStatus>>
}
