package com.example.studentonboarding.data.repository

import retrofit2.HttpException
import org.json.JSONObject
import com.example.studentonboarding.data.remote.api.NetworkModule
import com.example.studentonboarding.domain.model.AppState
import com.example.studentonboarding.domain.model.Resource
import com.example.studentonboarding.domain.model.StageStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.studentonboarding.data.remote.dto.LoginData
import com.example.studentonboarding.data.remote.dto.LoginRequest
import com.example.studentonboarding.data.remote.dto.DocStatusData
import com.example.studentonboarding.data.remote.dto.UploadResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.example.studentonboarding.data.remote.dto.OrderData
import com.example.studentonboarding.data.remote.dto.PaymentStatusData
import com.example.studentonboarding.data.remote.dto.VerifyPaymentRequest
import com.example.studentonboarding.data.remote.dto.CourseListData
import com.example.studentonboarding.data.remote.dto.CourseRegRequest
import com.example.studentonboarding.data.remote.dto.AccommodationRequest

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
                parseNetworkError(e)
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
                parseNetworkError(e)
            }
        }
    }

    suspend fun getDocumentStatus(): Resource<DocStatusData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getDocumentStatus()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Failed to fetch document status", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun uploadDocument(file: File, docType: String, idempotencyKey: String): Resource<UploadResult> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Tell the Node.js backend this is explicitly a JPEG image
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

                // 2. The field MUST be named "file" to match upload.single('file')
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // 3. Format the docType as clean Form Data to prevent Retrofit quote wrapping
                val docTypePart = MultipartBody.Part.createFormData("doc_type", docType)

                val response = api.uploadDocument(filePart, docTypePart, idempotencyKey)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Upload failed", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun getPaymentStatus(): Resource<PaymentStatusData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getPaymentStatus()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Failed to fetch payment status", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun createPaymentOrder(): Resource<OrderData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.createPaymentOrder()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Failed to create order", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun verifyPayment(orderId: String, paymentId: String, signature: String): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val request = VerifyPaymentRequest(orderId, paymentId, signature)
                val response = api.verifyPayment(request)
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Payment verification failed", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun getCourses(): Resource<CourseListData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getCourses()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Failed to fetch courses", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun submitCourseRegistration(courseIds: List<String>, electiveIds: List<String>): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CourseRegRequest(courseIds, electiveIds)
                val response = api.submitCourseRegistration(request)
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Registration failed", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun getAccommodationStatus(): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getAccommodationStatus()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Failed to fetch status", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun submitAccommodation(request: AccommodationRequest): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.submitAccommodation(request)
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Submission failed", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    private fun parseNetworkError(e: Exception): Resource.Error {
        if (e is HttpException) {
            try {
                // Extract the JSON string from the server's error response
                val errorString = e.response()?.errorBody()?.string()
                if (errorString != null) {
                    val json = JSONObject(errorString)
                    val errorObj = json.getJSONObject("error")
                    return Resource.Error(
                        message = errorObj.getString("message"), // The exact message from Node.js!
                        code = errorObj.getString("code")
                    )
                }
            } catch (parseEx: Exception) {
                parseNetworkError(e)
            }
        }
        // Fallback for no internet connection, timeouts, etc.
        return Resource.Error(e.localizedMessage ?: "Network connection failed")
    }
}