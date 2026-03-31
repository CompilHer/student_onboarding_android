package com.example.studentonboarding.presentation.screens.stage4_accommodation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.remote.dto.AccommodationRequest
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Stage4ViewModel : ViewModel() {

    private val repository = StudentRepositoryImpl()

    // "hostel", "transport", or "day_scholar"
    private val _selectedType = MutableStateFlow("day_scholar")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    // Hostel fields
    private val _hostelBlock = MutableStateFlow("")
    val hostelBlock: StateFlow<String> = _hostelBlock.asStateFlow()

    private val _roomNumber = MutableStateFlow("")
    val roomNumber: StateFlow<String> = _roomNumber.asStateFlow()

    // Transport fields
    private val _busRouteId = MutableStateFlow("")
    val busRouteId: StateFlow<String> = _busRouteId.asStateFlow()

    private val _busStop = MutableStateFlow("")
    val busStop: StateFlow<String> = _busStop.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _stageComplete = MutableStateFlow(false)
    val stageComplete: StateFlow<Boolean> = _stageComplete.asStateFlow()

    fun onTypeChange(type: String) { _selectedType.value = type }
    fun onHostelBlockChange(block: String) { _hostelBlock.value = block }
    fun onRoomNumberChange(room: String) { _roomNumber.value = room }
    fun onBusRouteChange(route: String) { _busRouteId.value = route }
    fun onBusStopChange(stop: String) { _busStop.value = stop }

    fun clearMessage() { _uiMessage.value = null }

    fun submitAccommodation() {
        viewModelScope.launch {
            _isSubmitting.value = true

            // Build the specific request based on the selected type
            val request = when (_selectedType.value) {
                "hostel" -> AccommodationRequest(
                    type = "hostel",
                    hostelBlock = _hostelBlock.value.ifBlank { null },
                    roomNumber = _roomNumber.value.ifBlank { null }
                )
                "transport" -> AccommodationRequest(
                    type = "transport",
                    busRouteId = _busRouteId.value.ifBlank { null },
                    busStop = _busStop.value.ifBlank { null }
                )
                else -> AccommodationRequest(type = "day_scholar")
            }

            val result = repository.submitAccommodation(request)

            if (result is Resource.Success) {
                _uiMessage.value = "Accommodation details saved successfully."
                _stageComplete.value = true
            } else if (result is Resource.Error) {
                _uiMessage.value = "Failed to save details: ${result.message}"
            }
            _isSubmitting.value = false
        }
    }
}