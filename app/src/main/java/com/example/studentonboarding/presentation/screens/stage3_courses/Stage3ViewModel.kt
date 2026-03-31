package com.example.studentonboarding.presentation.screens.stage3_courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.remote.dto.CourseListData
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Stage3ViewModel : ViewModel() {

    private val repository = StudentRepositoryImpl()

    private val _courseData = MutableStateFlow<Resource<CourseListData>?>(null)
    val courseData: StateFlow<Resource<CourseListData>?> = _courseData.asStateFlow()

    // Sets to hold the IDs of the selected courses
    private val _selectedCoreIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedCoreIds: StateFlow<Set<String>> = _selectedCoreIds.asStateFlow()

    private val _selectedElectiveIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedElectiveIds: StateFlow<Set<String>> = _selectedElectiveIds.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    // Will be set to true when the backend confirms success
    private val _registrationComplete = MutableStateFlow(false)
    val registrationComplete: StateFlow<Boolean> = _registrationComplete.asStateFlow()

    init {
        fetchCourses()
    }

    fun fetchCourses() {
        viewModelScope.launch {
            _courseData.value = Resource.Loading
            val result = repository.getCourses()
            _courseData.value = result

            // Auto-select all core courses to save the student time
            if (result is Resource.Success) {
                _selectedCoreIds.value = result.data.core.map { it.id }.toSet()
            }
        }
    }

    fun toggleElective(courseId: String) {
        val currentSet = _selectedElectiveIds.value.toMutableSet()
        if (currentSet.contains(courseId)) {
            currentSet.remove(courseId)
        } else {
            currentSet.add(courseId)
        }
        _selectedElectiveIds.value = currentSet
    }

    fun clearMessage() { _uiMessage.value = null }

    fun submitRegistration() {
        viewModelScope.launch {
            _isSubmitting.value = true
            val core = _selectedCoreIds.value.toList()
            val electives = _selectedElectiveIds.value.toList()

            val result = repository.submitCourseRegistration(core, electives)

            if (result is Resource.Success) {
                _uiMessage.value = "Courses registered successfully!"
                _registrationComplete.value = true
            } else if (result is Resource.Error) {
                _uiMessage.value = "Failed to register: ${result.message}"
            }

            _isSubmitting.value = false
        }
    }
}