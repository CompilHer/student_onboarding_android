package com.example.studentonboarding.presentation.screens.stage7_id_card

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentonboarding.domain.model.Resource
import com.example.studentonboarding.presentation.components.FsmProgressTracker

@Composable
fun Stage7IdCardScreen(
    onNavigateNext: () -> Unit,
    viewModel: Stage7ViewModel = viewModel()
) {
    val context = LocalContext.current
    val idCardStatus by viewModel.idCardStatus.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val accentColor = Color(0xFF00E5FF)

    // Gradient for the Digital ID Card
    val cardGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1E3C40), Color(0xFF0D1B2A))
    )

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        FsmProgressTracker(currentStage = 7)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val status = idCardStatus) {
                is Resource.Loading -> CircularProgressIndicator(color = accentColor)
                is Resource.Error -> Text(status.message, color = MaterialTheme.colorScheme.error)
                is Resource.Success -> {
                    val idCardMap = status.data["idCard"] as? Map<*, *>
                    val isGenerated = idCardMap != null && idCardMap.isNotEmpty()

                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Institutional ID Card",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        if (isGenerated) {
                            // Render the Digital ID Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.58f) // Standard credit card/ID ratio
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(cardGradient)
                                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .padding(24.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text("SARASWATI COLLEGE", color = accentColor, fontWeight = FontWeight.Bold, letterSpacing = androidx.compose.ui.unit.TextUnit(2f, androidx.compose.ui.unit.TextUnitType.Sp))
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    Text("ID NUMBER", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        text = idCardMap?.get("card_number").toString(),
                                        color = Color.White,
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("VALID UNTIL", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        text = idCardMap?.get("valid_until").toString(),
                                        color = accentColor,
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(48.dp))

                            Button(
                                onClick = onNavigateNext,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Proceed to Final Review", color = backgroundColor, fontWeight = FontWeight.Bold)
                            }

                        } else {
                            Text(
                                "Your profile has been verified. You may now generate your official digital ID card.",
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { viewModel.generateIdCard() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                enabled = !isGenerating,
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isGenerating) CircularProgressIndicator(color = backgroundColor, modifier = Modifier.size(24.dp))
                                else Text("Generate Digital ID", color = backgroundColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                null -> {}
            }
        }
    }
}