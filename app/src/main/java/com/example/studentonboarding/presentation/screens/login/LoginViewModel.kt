package com.example.studentonboarding.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // For now, we will instantiate the repository directly.
    // We can upgrade this to Dagger/Hilt Dependency Injection later!
    private val repository = StudentRepositoryImpl()

    // Holds the text field inputs
    var rollNumber = MutableStateFlow("")
        private set
    var dob = MutableStateFlow("")
        private set

    // Holds the current state of the login process (Idle, Loading, Success, Error)
    private val _loginState = MutableStateFlow<Resource<Unit>?>(null)
    val loginState: StateFlow<Resource<Unit>?> = _loginState.asStateFlow()

    fun onRollNumberChange(newValue: String) {
        rollNumber.value = newValue
    }

    fun onDobChange(newValue: String) {
        dob.value = newValue
    }

    fun submitLogin() {
        viewModelScope.launch {
            _loginState.value = Resource.Loading

            val result = repository.login(rollNumber.value, dob.value)

            when (result) {
                is Resource.Success -> {
                    // We map the Success<LoginData> to a generic Success<Unit> just to tell the UI to navigate
                    _loginState.value = Resource.Success(Unit)
                }
                is Resource.Error -> {
                    _loginState.value = Resource.Error(result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }
}