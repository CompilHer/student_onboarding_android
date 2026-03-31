package com.example.studentonboarding.presentation.screens.stage2_payment

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentonboarding.domain.model.Resource
import com.example.studentonboarding.presentation.components.FsmProgressTracker
import androidx.compose.foundation.layout.systemBarsPadding

@Composable
fun Stage2PaymentScreen(
    onNavigateNext: () -> Unit,
    viewModel: Stage2ViewModel = viewModel()
) {
    val context = LocalContext.current
    val paymentStatus by viewModel.paymentStatus.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val surfaceColor = Color(0xFF1E1E1E)
    val accentColor = Color(0xFF00E5FF)

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
            .systemBarsPadding()
    ) {
        FsmProgressTracker(currentStage = 2)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val status = paymentStatus) {
                is Resource.Loading -> CircularProgressIndicator(color = accentColor)
                is Resource.Success -> {
                    val isPaid = status.data.isPaid

                    Card(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Tuition & Enrollment Fees", color = Color.White, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            // Format: 5000000 paise -> ₹50,000.00
                            Text("Total Amount: ₹${status.data.payments.firstOrNull()?.amountPaise?.div(100) ?: "50,000"}",
                                color = accentColor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(32.dp))

                            if (isPaid) {
                                Text("Payment Received ✅", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = onNavigateNext,
                                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                                ) {
                                    Text("Proceed to Course Selection", color = backgroundColor)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.initiatePayment(context as Activity) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isProcessing,
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                                ) {
                                    if (isProcessing) CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = backgroundColor
                                    )
                                    else Text("Pay with Razorpay", color = backgroundColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> Text(status.message, color = Color.Red)
                else -> {}
            }
        }
    }
}