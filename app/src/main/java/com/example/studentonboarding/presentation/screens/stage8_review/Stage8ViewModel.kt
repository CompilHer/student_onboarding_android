package com.example.studentonboarding.presentation.screens.stage8_review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Stage8ViewModel : ViewModel() {

    private val repository = StudentRepositoryImpl()

    private val _reviewStatus = MutableStateFlow<Resource<Map<String, Any>>?>(null)
    val reviewStatus: StateFlow<Resource<Map<String, Any>>?> = _reviewStatus.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _isEnrolled = MutableStateFlow(false)
    val isEnrolled: StateFlow<Boolean> = _isEnrolled.asStateFlow()

    init {
        fetchReviewStatus()
    }

    private fun fetchReviewStatus() {
        viewModelScope.launch {
            _reviewStatus.value = Resource.Loading
            _reviewStatus.value = repository.getReviewStatus()
        }
    }

    fun clearMessage() { _uiMessage.value = null }

    fun submitApplication() {
        viewModelScope.launch {
            _isSubmitting.value = true

            val result = repository.submitFinalReview()

            if (result is Resource.Success) {
                _uiMessage.value = "Application Approved! Welcome to the College."
                _isEnrolled.value = true
            } else if (result is Resource.Error) {
                _uiMessage.value = "Submission failed: ${result.message}"
            }
            _isSubmitting.value = false
        }
    }
}