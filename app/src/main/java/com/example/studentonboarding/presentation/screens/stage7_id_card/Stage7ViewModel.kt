package com.example.studentonboarding.presentation.screens.stage7_id_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Stage7ViewModel : ViewModel() {

    private val repository = StudentRepositoryImpl()

    private val _idCardStatus = MutableStateFlow<Resource<Map<String, Any>>?>(null)
    val idCardStatus: StateFlow<Resource<Map<String, Any>>?> = _idCardStatus.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _stageComplete = MutableStateFlow(false)
    val stageComplete: StateFlow<Boolean> = _stageComplete.asStateFlow()

    init {
        fetchIdCardStatus()
    }

    fun fetchIdCardStatus() {
        viewModelScope.launch {
            _idCardStatus.value = Resource.Loading
            _idCardStatus.value = repository.getIdCardStatus()
        }
    }

    fun clearMessage() { _uiMessage.value = null }

    fun generateIdCard() {
        viewModelScope.launch {
            _isGenerating.value = true

            val result = repository.generateIdCard()

            if (result is Resource.Success) {
                _uiMessage.value = "Digital ID Card generated successfully!"
                _stageComplete.value = true
                fetchIdCardStatus() // Refresh to grab the newly created ID card data
            } else if (result is Resource.Error) {
                _uiMessage.value = "Generation failed: ${result.message}"
            }
            _isGenerating.value = false
        }
    }
}