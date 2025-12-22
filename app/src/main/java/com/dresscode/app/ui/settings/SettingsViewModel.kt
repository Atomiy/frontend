
package com.dresscode.app.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.model.UpdateUserRequest
import com.dresscode.app.data.model.User
import com.dresscode.app.data.repository.UserRepository
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _user = MutableLiveData<Result<User>>()
    val user: LiveData<Result<User>> = _user

    private val _updateResult = MutableLiveData<Result<User>>()
    val updateResult: LiveData<Result<User>> = _updateResult

    fun fetchCurrentUser() {
        viewModelScope.launch {
            _user.value = Result.Loading
            try {
                val response = userRepository.getCurrentUser()
                if (response.isSuccessful && response.body() != null && response.body()!!.code == 0) {
                    _user.value = Result.Success(response.body()!!.data!!)
                } else {
                    val error = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to fetch user"
                    _user.value = Result.Error(Exception(error))
                }
            } catch (e: Exception) {
                _user.value = Result.Error(e)
            }
        }
    }

    fun updateCurrentUser(nickname: String?, gender: String?) {
        viewModelScope.launch {
            _updateResult.value = Result.Loading
            try {
                val request = UpdateUserRequest(nickname, gender)
                val response = userRepository.updateUser(request)
                if (response.isSuccessful && response.body() != null && response.body()!!.code == 0) {
                    _updateResult.value = Result.Success(response.body()!!.data!!)
                    // Also update the main user LiveData
                    _user.value = Result.Success(response.body()!!.data!!)
                } else {
                    val error = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to update user"
                    _updateResult.value = Result.Error(Exception(error))
                }
            } catch (e: Exception) {
                _updateResult.value = Result.Error(e)
            }
        }
    }
}
