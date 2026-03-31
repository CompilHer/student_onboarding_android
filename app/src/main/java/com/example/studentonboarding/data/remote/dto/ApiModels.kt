package com.example.studentonboarding.data.remote.dto

import com.google.gson.annotations.SerializedName

// -- Response Envelope ------------------------------------
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null,
    val duplicate: Boolean? = null
)

data class ApiError(
    val code: String,
    val message: String
)

// -- Auth -------------------------------------------------
data class LoginRequest(
    @SerializedName("roll_number") val rollNumber: String,
    val dob: String   // "YYYY-MM-DD"
)

data class LoginData(
    val token: String,
    val student: StudentSummary
)

data class StudentSummary(
    val id: String,
    val rollNumber: String,
    val fullName: String,
    val branch: String,
    val semester: Int
)

data class StudentProfile(
    val id: String,
    @SerializedName("roll_number") val rollNumber: String,
    @SerializedName("full_name") val fullName: String,
    val email: String?,
    val phone: String?,
    val branch: String,
    val semester: Int,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    val gender: String?,
    @SerializedName("photo_url") val photoUrl: String?
)

// -- FSM State --------------------------------------------
data class OnboardingState(
    val currentStage: Int?,
    val isFullyEnrolled: Boolean,
    val enrolledAt: String?,
    val stages: List<StageInfo>
)

data class StageInfo(
    val stage: Int,
    val name: String,
    val status: String   // "locked" | "pending" | "in_progress" | "done" | "failed"
)

// -- Stage 1 ----------------------------------------------
data class UploadResult(
    val uploadId: String,
    val docType: String,
    val isVerified: Boolean,
    val stageComplete: Boolean,
    val remainingDocs: List<String>
)

data class DocStatusData(
    val requiredDocs: List<String>,
    val uploads: List<DocUpload>,
    val remainingDocs: List<String>,
    val isComplete: Boolean
)

data class DocUpload(
    @SerializedName("doc_type") val docType: String,
    @SerializedName("is_verified") val isVerified: Boolean,
    @SerializedName("verified_at") val verifiedAt: String?,
    @SerializedName("rejection_reason") val rejectionReason: String?,
    @SerializedName("created_at") val createdAt: String
)

// -- Stage 2 ----------------------------------------------
data class OrderData(
    val orderId: String,
    val amount: Long,        // paise
    val currency: String
)

data class VerifyPaymentRequest(
    @SerializedName("razorpay_order_id") val orderId: String,
    @SerializedName("razorpay_payment_id") val paymentId: String,
    @SerializedName("razorpay_signature") val signature: String
)

data class PaymentStatusData(
    val payments: List<PaymentRecord>,
    val isPaid: Boolean
)

data class PaymentRecord(
    @SerializedName("razorpay_order_id") val orderId: String?,
    @SerializedName("razorpay_payment_id") val paymentId: String?,
    @SerializedName("amount_paise") val amountPaise: Long,
    val currency: String,
    @SerializedName("fee_type") val feeType: String?,
    val status: String,
    @SerializedName("verified_at") val verifiedAt: String?,
    @SerializedName("created_at") val createdAt: String
)

// -- Stage 3 ----------------------------------------------
data class CourseListData(
    val core: List<Course>,
    val electives: List<Course>,
    val total: Int
)

data class Course(
    val id: String,
    val code: String,
    val name: String,
    val credits: Int,
    @SerializedName("is_elective") val isElective: Boolean,
    val branch: String?,
    val semester: Int?,
    @SerializedName("max_seats") val maxSeats: Int?
)

data class CourseRegRequest(
    @SerializedName("course_ids") val courseIds: List<String>,
    @SerializedName("elective_ids") val electiveIds: List<String>
)

// -- Stage 4 ----------------------------------------------
data class AccommodationRequest(
    val type: String,             // "hostel" | "day_scholar" | "transport"
    @SerializedName("hostel_block") val hostelBlock: String? = null,
    @SerializedName("room_number") val roomNumber: String? = null,
    @SerializedName("bus_route_id") val busRouteId: String? = null,
    @SerializedName("bus_stop") val busStop: String? = null
)

// -- Stage 6 ----------------------------------------------
data class ComplianceRequest(
    @SerializedName("anti_ragging") val antiRagging: Boolean,
    @SerializedName("code_of_conduct") val codeOfConduct: Boolean,
    @SerializedName("data_consent") val dataConsent: Boolean
)

// -- Generic stage completion -----------------------------
data class StageCompleteData(
    val message: String
    // Each stage may add extra fields - handled via Map or specific data classes
)