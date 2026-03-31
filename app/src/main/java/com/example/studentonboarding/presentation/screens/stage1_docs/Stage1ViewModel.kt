package com.example.studentonboarding.presentation.screens.stage1_docs

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.remote.dto.DocStatusData
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class Stage1ViewModel : ViewModel() {

    private val repository = StudentRepositoryImpl()

    // Holds the overall status of the 4 required documents
    private val _docStatus = MutableStateFlow<Resource<DocStatusData>?>(null)
    val docStatus: StateFlow<Resource<DocStatusData>?> = _docStatus.asStateFlow()

    // Tracks which document is currently being uploaded to show a loading spinner
    private val _uploadingDocType = MutableStateFlow<String?>(null)
    val uploadingDocType: StateFlow<String?> = _uploadingDocType.asStateFlow()

    // For showing temporary error/success messages on the screen
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    init {
        fetchDocumentStatus()
    }

    fun fetchDocumentStatus() {
        viewModelScope.launch {
            _docStatus.value = Resource.Loading
            _docStatus.value = repository.getDocumentStatus()
        }
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    /**
     * Android File System logic: We must copy the Uri from the Photo Picker
     * into a temporary File in the app's cache directory before Retrofit can upload it.
     */
    fun uploadFile(context: Context, uri: Uri, docType: String) {
        viewModelScope.launch {
            _uploadingDocType.value = docType

            try {
                // 1. Create a temporary file in the cache
                val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(tempFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                // 2. Generate the required Idempotency Key
                val idempotencyKey = UUID.randomUUID().toString()

                // 3. Send to Repository
                val result = repository.uploadDocument(tempFile, docType, idempotencyKey)

                when (result) {
                    is Resource.Success -> {
                        if (result.data.stageComplete) {
                            _uiMessage.value = "Stage 1 Complete! Next stage unlocked."
                        } else {
                            _uiMessage.value = "$docType uploaded successfully."
                        }
                        // Refresh the list to show the new checkmarks
                        fetchDocumentStatus()
                    }
                    is Resource.Error -> {
                        _uiMessage.value = "Failed to upload $docType: ${result.message}"
                    }
                    else -> {}
                }

                // 4. Clean up the temp file to save space
                tempFile.delete()

            } catch (e: Exception) {
                _uiMessage.value = "Error processing file: ${e.localizedMessage}"
            } finally {
                _uploadingDocType.value = null
            }
        }
    }
}