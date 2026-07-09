package com.novashop.app.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novashop.app.data.model.User
import com.novashop.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _userRole = MutableStateFlow("customer")
    val userRole: StateFlow<String> = _userRole

    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(fullName, email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                _currentUser.value = user
                _userRole.value = user.role
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error(
                    result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                _currentUser.value = user
                _userRole.value = user.role
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error(
                    result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _userRole.value = "customer"
        _authState.value = AuthState.Idle
    }

    fun checkCurrentUser() {
        viewModelScope.launch {
            val firebaseUser = repository.getCurrentUser()
            if (firebaseUser != null) {
                val result = repository.getUserProfile(firebaseUser.uid)
                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    _currentUser.value = user
                    _userRole.value = user.role
                }
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }



}