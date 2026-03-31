package com.example.studentonboarding.presentation.screens.stage2_payment

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentonboarding.data.remote.dto.PaymentStatusData
import com.example.studentonboarding.data.repository.StudentRepositoryImpl
import com.example.studentonboarding.domain.model.Resource
import com.example.studentonboarding.presentation.PaymentEventHandler
import com.razorpay.Checkout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class Stage2ViewModel : ViewModel() {

    private val repository = StudentRepositoryImpl()

    private val _paymentStatus = MutableStateFlow<Resource<PaymentStatusData>?>(null)
    val paymentStatus: StateFlow<Resource<PaymentStatusData>?> = _paymentStatus.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    // Tracks if we are currently loading an order or verifying
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    init {
        fetchPaymentStatus()
    }

    fun fetchPaymentStatus() {
        viewModelScope.launch {
            _paymentStatus.value = Resource.Loading
            _paymentStatus.value = repository.getPaymentStatus()
        }
    }

    fun clearMessage() { _uiMessage.value = null }

    /**
     * Step 1: Tell Backend to create an order
     * Step 2: Open Razorpay SDK
     */
    fun initiatePayment(activity: Activity) {
        viewModelScope.launch {
            _isProcessing.value = true

            val orderResult = repository.createPaymentOrder()
            if (orderResult is Resource.Success) {
                val orderData = orderResult.data
                launchRazorpay(activity, orderData.orderId, orderData.amount, orderData.currency)
            } else if (orderResult is Resource.Error) {
                _uiMessage.value = "Failed to create order: ${orderResult.message}"
                _isProcessing.value = false
            }
        }
    }

    private fun launchRazorpay(activity: Activity, orderId: String, amountPaise: Long, currency: String) {
        // Set up the listeners to catch the result from MainActivity
        PaymentEventHandler.onPaymentSuccess = { returningOrderId, paymentId, signature ->
            verifyPaymentSignature(returningOrderId, paymentId, signature)
        }

        PaymentEventHandler.onPaymentError = { _, description ->
            _uiMessage.value = "Payment Failed: $description"
            _isProcessing.value = false
        }

        try {
            val checkout = Checkout()
            // NOTE: Use your own Razorpay Test Key ID here in production
            checkout.setKeyID("rzp_test_1234567890ABCDEF")

            val options = JSONObject().apply {
                put("name", "Saraswati College of Engineering")
                put("description", "Student Onboarding Fee")
                put("theme.color", "#00E5FF") // Matches our AI Cyan UI
                put("currency", currency)
                put("amount", amountPaise)
                put("order_id", orderId)

                // Prefill user details if needed
                val prefill = JSONObject()
                prefill.put("email", "student@test.com")
                put("prefill", prefill)
            }

            checkout.open(activity, options)
        } catch (e: Exception) {
            _uiMessage.value = "Error launching Razorpay: ${e.message}"
            _isProcessing.value = false
        }
    }

    /**
     * Step 3: Send Razorpay signature to backend for strict cryptographic verification
     */
    private fun verifyPaymentSignature(orderId: String, paymentId: String, signature: String) {
        viewModelScope.launch {
            _uiMessage.value = "Verifying payment securely..."

            val verifyResult = repository.verifyPayment(orderId, paymentId, signature)

            if (verifyResult is Resource.Success) {
                _uiMessage.value = "Payment Verified! Stage 2 Complete."
                fetchPaymentStatus() // Refresh UI
            } else if (verifyResult is Resource.Error) {
                _uiMessage.value = "Verification failed: ${verifyResult.message}"
            }
            _isProcessing.value = false
        }
    }
}