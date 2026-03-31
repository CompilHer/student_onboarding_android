package com.example.studentonboarding.presentation.screens.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean
)

class ChatbotViewModel : ViewModel() {

    private val repository = StudentRepositoryImpl()

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage(text = "System Initialized. I am your Campus AI Assistant. How can I help you with your onboarding process?", isFromUser = false))
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // 1. Add user message to UI immediately
        val userMsg = ChatMessage(text = text.trim(), isFromUser = true)
        _messages.value = _messages.value + userMsg

        // 2. Trigger AI Loading state
        viewModelScope.launch {
            _isTyping.value = true

            // 3. Call backend
            val result = repository.sendChatMessage(text)

            // 4. Add AI response to UI
            if (result is Resource.Success) {
                val aiMsg = ChatMessage(text = result.data, isFromUser = false)
                _messages.value = _messages.value + aiMsg
            } else if (result is Resource.Error) {
                val errorMsg = ChatMessage(text = "Error: ${result.message}", isFromUser = false)
                _messages.value = _messages.value + errorMsg
            }

            _isTyping.value = false
        }
    }
}