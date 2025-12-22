
package com.dresscode.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dresscode.app.data.model.LoginRequest
import com.dresscode.app.data.model.RegisterRequest
import com.dresscode.app.data.repository.AuthRepository
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.model.LoginResponse
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                val response = authRepository.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body() != null && response.body()!!.code == 0) {
                    _loginResult.value = Result.Success(response.body()!!.data!!)
                } else {
                    val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Login Failed"
                    _loginResult.value = Result.Error(Exception(errorMessage))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e)
            }
        }
    }

    fun register(username: String, nickname: String, password: String, gender: String) {
        viewModelScope.launch {
            _registerResult.value = Result.Loading
            try {
                val response = authRepository.register(RegisterRequest(username, password, nickname, gender))
                if (response.isSuccessful && response.body() != null && response.body()!!.code == 0) {
                    _registerResult.value = Result.Success(Unit)
                } else {
                    val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Registration Failed"
                    _registerResult.value = Result.Error(Exception(errorMessage))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.Error(e)
            }
        }
    }
}
