package com.example.studentonboarding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding // Add this import!
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.studentonboarding.presentation.navigation.OnboardingNavGraph
import com.example.studentonboarding.presentation.PaymentEventHandler
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Checkout.preload(applicationContext)

        setContent {
            val navController = rememberNavController()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
                    .systemBarsPadding() // <-- THIS MAGIC LINE FIXES THE OVERLAP
            ) {
                OnboardingNavGraph(navController = navController)
            }
        }
    }

    override fun onPaymentSuccess(paymentId: String?, response: PaymentData?) {
        PaymentEventHandler.onPaymentSuccess?.invoke(
            response?.orderId ?: "",
            paymentId ?: "",
            response?.signature ?: ""
        )
    }

    override fun onPaymentError(code: Int, description: String?, response: PaymentData?) {
        PaymentEventHandler.onPaymentError?.invoke(
            code,
            description ?: "Payment failed or canceled"
        )
    }
}