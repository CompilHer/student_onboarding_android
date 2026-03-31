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
import com.example.studentonboarding.data.remote.dto.ComplianceRequest

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
                return Resource.Error("Server Error ${e.code()}: ${e.message()}")
            }
        }
        // Fallback for no internet connection, timeouts, etc.
        return Resource.Error(e.localizedMessage ?: "Network connection failed")
    }

    suspend fun getItStatus(): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getItStatus()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Failed to fetch IT status", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e) // Using our new error parser!
            }
        }
    }

    suspend fun submitItSetup(): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.submitItSetup()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "IT Setup failed", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun getComplianceStatus(): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getComplianceStatus()
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

    suspend fun submitCompliance(request: ComplianceRequest): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.submitCompliance(request)
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

    suspend fun getIdCardStatus(): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getIdCardStatus()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Failed to fetch ID Card status", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun generateIdCard(): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.generateIdCard()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "ID Card generation failed", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun getReviewStatus(): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getReviewStatus()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Failed to fetch review status", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun submitFinalReview(): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.submitFinalReview()
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.error?.message ?: "Final submission failed", response.error?.code)
                }
            } catch (e: Exception) {
                parseNetworkError(e)
            }
        }
    }

    suspend fun sendChatMessage(message: String): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                // In the future, this will be: api.sendChatMessage(ChatRequest(message))
                // For now, we simulate a 1-second network delay
                kotlinx.coroutines.delay(1000)

                // Simulated AI Responses based on keywords
                val reply = when {
                    message.lowercase().contains("hostel") -> "Hostel allocation is based on your Stage 4 logistics submission. Room numbers are assigned 1 week before the semester starts."
                    message.lowercase().contains("fee") || message.lowercase().contains("pay") -> "You can view your payment receipt for the ₹50,000 tuition fee in your Student Dashboard."
                    message.lowercase().contains("course") -> "You are currently enrolled in all mandatory core courses. Elective changes can be made during the first two weeks of the semester."
                    else -> "I am your Campus AI Assistant. I have access to your enrollment profile. How can I help you today?"
                }
                Resource.Success(reply)
            } catch (e: Exception) {
                Resource.Error("AI Connection lost.")
            }
        }
    }
}