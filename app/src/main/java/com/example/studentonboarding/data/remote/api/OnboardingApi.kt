package com.example.studentonboarding.data.remote.api

import com.example.studentonboarding.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OnboardingApi {

    // -- Auth -------------------
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): ApiResponse<LoginData>

    @GET("auth/me")
    suspend fun getProfile(): ApiResponse<Map<String, StudentProfile>>

    // -- FSM State --------------
    @GET("onboarding/state")
    suspend fun getOnboardingState(): ApiResponse<OnboardingState>

    // -- Courses ----------------
    @GET("courses")
    suspend fun getCourses(): ApiResponse<CourseListData>

    // -- Stage 1: Documents -----
    @Multipart
    @POST("stages/1/upload")
    suspend fun uploadDocument(
        @Part file: MultipartBody.Part,
        @Part docType: MultipartBody.Part, // <-- Change this from RequestBody to MultipartBody.Part
        @Header("Idempotency-Key") idempotencyKey: String
    ): ApiResponse<UploadResult>

    @GET("stages/1/status")
    suspend fun getDocumentStatus(): ApiResponse<DocStatusData>

    // -- Stage 2: Payment -------
    @POST("stages/2/create-order")
    suspend fun createPaymentOrder(): ApiResponse<OrderData>

    @POST("stages/2/verify")
    suspend fun verifyPayment(@Body body: VerifyPaymentRequest): ApiResponse<Map<String, Any>>

    @GET("stages/2/status")
    suspend fun getPaymentStatus(): ApiResponse<PaymentStatusData>

    // -- Stages 3-8 -------------
    @POST("stages/3/submit")
    suspend fun submitCourseRegistration(@Body body: CourseRegRequest): ApiResponse<Map<String, Any>>

    @GET("stages/3/status")
    suspend fun getCourseRegStatus(): ApiResponse<Map<String, Any>>

    @POST("stages/4/submit")
    suspend fun submitAccommodation(@Body body: AccommodationRequest): ApiResponse<Map<String, Any>>

    @GET("stages/4/status")
    suspend fun getAccommodationStatus(): ApiResponse<Map<String, Any>>

    @POST("stages/5/submit")
    suspend fun submitItSetup(): ApiResponse<Map<String, Any>>

    @GET("stages/5/status")
    suspend fun getItStatus(): ApiResponse<Map<String, Any>>

    @POST("stages/6/submit")
    suspend fun submitCompliance(@Body body: ComplianceRequest): ApiResponse<Map<String, Any>>

    @GET("stages/6/status")
    suspend fun getComplianceStatus(): ApiResponse<Map<String, Any>>

    @POST("stages/7/generate")
    suspend fun generateIdCard(): ApiResponse<Map<String, Any>>

    @GET("stages/7/status")
    suspend fun getIdCardStatus(): ApiResponse<Map<String, Any>>

    @POST("stages/8/submit")
    suspend fun submitFinalReview(): ApiResponse<Map<String, Any>>

    @GET("stages/8/status")
    suspend fun getReviewStatus(): ApiResponse<Map<String, Any>>
}