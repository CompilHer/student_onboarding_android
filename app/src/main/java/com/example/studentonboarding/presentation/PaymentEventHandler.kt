package com.example.studentonboarding.presentation

object PaymentEventHandler {
    // Callbacks that our ViewModel will listen to
    var onPaymentSuccess: ((orderId: String, paymentId: String, signature: String) -> Unit)? = null
    var onPaymentError: ((code: Int, description: String) -> Unit)? = null
}