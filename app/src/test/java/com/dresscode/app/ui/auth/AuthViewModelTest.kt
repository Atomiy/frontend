
package com.dresscode.app.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dresscode.app.data.model.ApiResponse
import com.dresscode.app.data.model.LoginRequest
import com.dresscode.app.data.model.LoginResponse
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.model.User
import com.dresscode.app.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AuthViewModel
    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        // This is a bit of a hack. A better way would be to use dependency injection.
        // For this example, we will assume this is how the ViewModel gets its repository.
        // I will use reflection to set the private repository field.
        viewModel = AuthViewModel()
        val repositoryField = viewModel::class.java.getDeclaredField("authRepository")
        repositoryField.isAccessible = true
        repositoryField.set(viewModel, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success sets loginResult to Success`() = runTest {
        // Given
        val user = User(1, "test", "Test", "male", null)
        val loginResponse = LoginResponse("token", user)
        val apiResponse = ApiResponse(0, "Success", loginResponse)
        val successResponse = Response.success(apiResponse)
        
        whenever(repository.login(LoginRequest("test", "pass"))).thenReturn(successResponse)

        // When
        viewModel.login("test", "pass")
        
        // Then
        // The first value emitted should be Loading
        assertEquals(Result.Loading, viewModel.loginResult.value)
        
        // Advance the dispatcher to execute the coroutine
        testDispatcher.scheduler.advanceUntilIdle()
        
        // The next value should be Success
        val finalResult = viewModel.loginResult.value as Result.Success
        assertEquals("token", finalResult.data.token)
    }

    @Test
    fun `login failure sets loginResult to Error`() = runTest {
        // Given
        val errorResponse = Response.error<ApiResponse<LoginResponse>>(401, mock())
        whenever(repository.login(LoginRequest("test", "wrongpass"))).thenReturn(errorResponse)

        // When
        viewModel.login("test", "wrongpass")

        // Then
        assertEquals(Result.Loading, viewModel.loginResult.value)
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.loginResult.value is Result.Error)
    }
}
