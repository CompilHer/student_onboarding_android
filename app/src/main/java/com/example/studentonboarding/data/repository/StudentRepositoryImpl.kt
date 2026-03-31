package com.example.studentonboarding.data.repository

import com.example.studentonboarding.data.remote.api.NetworkModule
import com.example.studentonboarding.domain.model.AppState
import com.example.studentonboarding.domain.model.Resource
import com.example.studentonboarding.domain.model.StageStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.studentonboarding.data.remote.dto.LoginData
import com.example.studentonboarding.data.remote.dto.LoginRequest

class StudentRepositoryImpl {

    // In the future, this will be injected via Dagger/Hilt
    private val api = NetworkModule.onboardingApi

    /**
     * Fetches the current FSM state and maps the raw API response
     * into a clean Domain Model for the Compose UI.
     */
    suspend fun getOnboardingState(): Resource<AppState> {
        return withContext(Dispatchers.IO) { // Run network calls on a background thread
            try {
                val response = api.getOnboardingState()

                if (response.success && response.data != null) {
                    val rawData = response.data

                    // Map the List of stages into a Map for easier UI lookups
                    val statusMap = rawData.stages.associate { stageInfo ->
                        stageInfo.stage to StageStatus.fromString(stageInfo.status)
                    }

                    val cleanState = AppState(
                        currentStage = rawData.currentStage ?: 1,
                        isFullyEnrolled = rawData.isFullyEnrolled,
                        stageStatuses = statusMap
                    )

                    Resource.Success(cleanState)
                } else {
                    // Handle the standard error envelope from the API Contract
                    val errorMsg = response.error?.message ?: "Unknown server error"
                    val errorCode = response.error?.code
                    Resource.Error(errorMsg, errorCode)
                }
            } catch (e: Exception) {
                // Catch network drops, timeouts, or JSON parsing crashes
                Resource.Error(e.localizedMessage ?: "Network connection failed")
            }
        }
    }
    suspend fun login(rollNumber: String, dob: String): Resource<LoginData> {

        return withContext(Dispatchers.IO) {
            try {
                // Call the API
                val response = api.login(LoginRequest(rollNumber, dob))

                if (response.success && response.data != null) {
                    // SUCCESS: We got the token!
                    // Let's immediately give it to the Interceptor so all future API calls are authenticated.
                    NetworkModule.authInterceptor.setToken(response.data.token)
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Login failed", response.error?.code)
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Network connection failed")
            }
        }
    }
}