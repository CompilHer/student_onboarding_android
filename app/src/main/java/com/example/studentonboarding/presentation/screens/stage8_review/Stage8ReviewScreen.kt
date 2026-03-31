package com.example.studentonboarding.presentation.screens.stage8_review

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentonboarding.domain.model.Resource
import com.example.studentonboarding.presentation.components.FsmProgressTracker

@Composable
fun Stage8ReviewScreen(
    onNavigateToSuccess: () -> Unit,
    viewModel: Stage8ViewModel = viewModel()
) {
    val context = LocalContext.current
    val reviewStatus by viewModel.reviewStatus.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val isEnrolled by viewModel.isEnrolled.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val surfaceColor = Color(0xFF1E1E1E)
    val accentColor = Color(0xFF00E5FF)
    val successColor = Color(0xFF00E676)

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(isEnrolled) {
        if (isEnrolled) {
            onNavigateToSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        FsmProgressTracker(currentStage = 8)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (reviewStatus) {
                is Resource.Loading -> CircularProgressIndicator(color = accentColor)
                is Resource.Error -> Text((reviewStatus as Resource.Error).message, color = MaterialTheme.colorScheme.error)
                is Resource.Success -> {
                    Card(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Complete",
                                tint = successColor,
                                modifier = Modifier.size(72.dp).padding(bottom = 16.dp)
                            )

                            Text(
                                "Final Review",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "All 7 previous stages have been completed and verified. Your application is ready for final submission and official enrollment.",
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { viewModel.submitApplication() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                enabled = !isSubmitting,
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isSubmitting) CircularProgressIndicator(color = backgroundColor, modifier = Modifier.size(24.dp))
                                else Text("Submit Final Application", color = backgroundColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                null -> {}
            }
        }
    }
}