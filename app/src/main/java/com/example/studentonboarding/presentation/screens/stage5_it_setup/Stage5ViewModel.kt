package com.example.studentonboarding.presentation.screens.stage5_it_setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Stage5ViewModel : ViewModel() {

    private val repository = StudentRepositoryImpl()

    private val _itStatus = MutableStateFlow<Resource<Map<String, Any>>?>(null)
    val itStatus: StateFlow<Resource<Map<String, Any>>?> = _itStatus.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _stageComplete = MutableStateFlow(false)
    val stageComplete: StateFlow<Boolean> = _stageComplete.asStateFlow()

    init {
        fetchItStatus()
    }

    fun fetchItStatus() {
        viewModelScope.launch {
            _itStatus.value = Resource.Loading
            _itStatus.value = repository.getItStatus()
        }
    }

    fun clearMessage() { _uiMessage.value = null }

    fun generateCredentials() {
        viewModelScope.launch {
            _isSubmitting.value = true

            val result = repository.submitItSetup()

            if (result is Resource.Success) {
                _uiMessage.value = "Digital identity provisioned successfully!"
                _stageComplete.value = true
                fetchItStatus() // Refresh to grab the newly created data
            } else if (result is Resource.Error) {
                _uiMessage.value = "Provisioning failed: ${result.message}"
            }
            _isSubmitting.value = false
        }
    }
}