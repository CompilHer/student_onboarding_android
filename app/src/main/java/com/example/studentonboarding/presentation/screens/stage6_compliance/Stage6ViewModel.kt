package com.example.studentonboarding.presentation.screens.stage6_compliance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.remote.dto.ComplianceRequest
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Stage6ViewModel : ViewModel() {

    private val repository = StudentRepositoryImpl()

    private val _antiRagging = MutableStateFlow(false)
    val antiRagging: StateFlow<Boolean> = _antiRagging.asStateFlow()

    private val _codeOfConduct = MutableStateFlow(false)
    val codeOfConduct: StateFlow<Boolean> = _codeOfConduct.asStateFlow()

    private val _dataConsent = MutableStateFlow(false)
    val dataConsent: StateFlow<Boolean> = _dataConsent.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _stageComplete = MutableStateFlow(false)
    val stageComplete: StateFlow<Boolean> = _stageComplete.asStateFlow()

    fun toggleAntiRagging(checked: Boolean) { _antiRagging.value = checked }
    fun toggleCodeOfConduct(checked: Boolean) { _codeOfConduct.value = checked }
    fun toggleDataConsent(checked: Boolean) { _dataConsent.value = checked }

    fun clearMessage() { _uiMessage.value = null }

    fun submitCompliance() {
        // Double-check validation before sending
        if (!_antiRagging.value || !_codeOfConduct.value || !_dataConsent.value) {
            _uiMessage.value = "You must agree to all terms to proceed."
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true

            val request = ComplianceRequest(
                antiRagging = _antiRagging.value,
                codeOfConduct = _codeOfConduct.value,
                dataConsent = _dataConsent.value
            )

            val result = repository.submitCompliance(request)

            if (result is Resource.Success) {
                _uiMessage.value = "Compliance agreements digitally signed."
                _stageComplete.value = true
            } else if (result is Resource.Error) {
                _uiMessage.value = "Failed to submit: ${result.message}"
            }
            _isSubmitting.value = false
        }
    }
}