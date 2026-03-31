package com.example.studentonboarding.domain.model

// This strictly mirrors the PostgreSQL enum for rock-solid type safety in the UI
enum class StageStatus {
    LOCKED,
    PENDING,
    IN_PROGRESS,
    DONE,
    FAILED;

    companion object {
        fun fromString(status: String?): StageStatus {
            return entries.find { it.name.equals(status, ignoreCase = true) } ?: LOCKED
        }
    }
}

// A clean model for the UI to observe.
// Notice there are no @SerializedName annotations here. This is pure Kotlin.
data class AppState(
    val currentStage: Int = 1,
    val isFullyEnrolled: Boolean = false,
    val stageStatuses: Map<Int, StageStatus> = emptyMap()
)

// A wrapper to safely pass UI states from the ViewModel to the Compose screen
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val code: String? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}