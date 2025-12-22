
package com.dresscode.app.data.repository

import com.dresscode.app.data.model.VirtualTryOnRequest
import com.dresscode.app.data.remote.AiApiService
import com.dresscode.app.data.remote.RetrofitClient
import okhttp3.MultipartBody

class AiRepository {

    private val aiService: AiApiService by lazy {
        RetrofitClient.instance.create(AiApiService::class.java)
    }

    suspend fun uploadFile(file: MultipartBody.Part) = aiService.uploadFile(file)
    
    suspend fun startVirtualTryOn(request: VirtualTryOnRequest) = aiService.startVirtualTryOn(request)

    suspend fun getVirtualTryOnStatus(taskId: Long) = aiService.getVirtualTryOnStatus(taskId)
}
