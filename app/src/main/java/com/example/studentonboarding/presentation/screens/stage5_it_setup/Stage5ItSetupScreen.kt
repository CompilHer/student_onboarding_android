package com.example.studentonboarding.presentation.screens.stage5_it_setup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentonboarding.domain.model.Resource
import com.example.studentonboarding.presentation.components.FsmProgressTracker

@Composable
fun Stage5ItSetupScreen(
    onNavigateNext: () -> Unit,
    viewModel: Stage5ViewModel = viewModel()
) {
    val context = LocalContext.current
    val itStatus by viewModel.itStatus.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val terminalBgColor = Color(0xFF0D1117) // Slightly darker/blueish for terminal
    val accentColor = Color(0xFF00E5FF)
    val terminalTextColor = Color(0xFF00FF41) // Hacker green

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        FsmProgressTracker(currentStage = 5)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val status = itStatus) {
                is Resource.Loading -> CircularProgressIndicator(color = accentColor)
                is Resource.Error -> Text(status.message, color = MaterialTheme.colorScheme.error)
                is Resource.Success -> {
                    // Check if credentials exist in the nested map
                    val credentialsMap = status.data["credentials"] as? Map<*, *>
                    val isProvisioned = credentialsMap != null && credentialsMap.isNotEmpty()

                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Security",
                            tint = accentColor,
                            modifier = Modifier.size(64.dp).padding(bottom = 16.dp)
                        )
                        Text(
                            "Digital Identity Provisioning",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isProvisioned) {
                            Text(
                                "Your institutional access has been secured.",
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(32.dp))

                            // Terminal Window UI
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(terminalBgColor)
                                    .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text("> INITIALIZING SECURE READOUT...", color = Color.Gray, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.labelSmall)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    TerminalRow("EMAIL", credentialsMap?.get("institutional_email").toString(), terminalTextColor)
                                    TerminalRow("WIFI_USER", credentialsMap?.get("wifi_username").toString(), terminalTextColor)
                                    TerminalRow("LIB_CARD_ID", credentialsMap?.get("library_card_id").toString(), terminalTextColor)

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("> STATUS: ACTIVE", color = accentColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = onNavigateNext,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Acknowledge & Proceed", color = backgroundColor, fontWeight = FontWeight.Bold)
                            }

                        } else {
                            Text(
                                "Generate your official college email, WiFi access, and library ID.",
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { viewModel.generateCredentials() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                enabled = !isSubmitting,
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isSubmitting) CircularProgressIndicator(color = backgroundColor, modifier = Modifier.size(24.dp))
                                else Text("Generate Credentials", color = backgroundColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                null -> {}
            }
        }
    }
}

@Composable
fun TerminalRow(key: String, value: String, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$key:", color = Color.Gray, fontFamily = FontFamily.Monospace)
        Text(text = value.replace("null", "PENDING"), color = textColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}