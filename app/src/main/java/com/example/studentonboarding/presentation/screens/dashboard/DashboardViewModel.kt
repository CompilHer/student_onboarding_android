package com.example.studentonboarding.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.remote.api.NetworkModule
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.AppState
import com.example.studentonboarding.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val repository = StudentRepositoryImpl()

    private val _appState = MutableStateFlow<Resource<AppState>?>(null)
    val appState: StateFlow<Resource<AppState>?> = _appState.asStateFlow()

    init {
        refreshDashboard()
    }

    fun refreshDashboard() {
        viewModelScope.launch {
            _appState.value = Resource.Loading
            _appState.value = repository.getOnboardingState()
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        // Clear the JWT token from the interceptor
        NetworkModule.authInterceptor.setToken("")
        onLogoutComplete()
    }
}