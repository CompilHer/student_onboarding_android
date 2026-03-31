package com.example.studentonboarding.presentation.screens.stage4_accommodation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentonboarding.presentation.components.FsmProgressTracker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Stage4AccommodationScreen(
    onNavigateNext: () -> Unit,
    viewModel: Stage4ViewModel = viewModel()
) {
    val context = LocalContext.current
    val selectedType by viewModel.selectedType.collectAsState()
    val hostelBlock by viewModel.hostelBlock.collectAsState()
    val roomNumber by viewModel.roomNumber.collectAsState()
    val busRouteId by viewModel.busRouteId.collectAsState()
    val busStop by viewModel.busStop.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val stageComplete by viewModel.stageComplete.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val surfaceColor = Color(0xFF1E1E1E)
    val accentColor = Color(0xFF00E5FF)

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(stageComplete) {
        if (stageComplete) onNavigateNext()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        FsmProgressTracker(currentStage = 4)

        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Text("Logistics & Accommodation", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Select your primary mode of campus attendance.", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            // Selection Cards
            AccommodationOptionCard("day_scholar", "Day Scholar", "I will manage my own daily commute.", selectedType, surfaceColor, accentColor) { viewModel.onTypeChange("day_scholar") }
            Spacer(modifier = Modifier.height(12.dp))
            AccommodationOptionCard("hostel", "Campus Hostel", "I require on-campus living accommodations.", selectedType, surfaceColor, accentColor) { viewModel.onTypeChange("hostel") }
            Spacer(modifier = Modifier.height(12.dp))
            AccommodationOptionCard("transport", "College Transport", "I need to register for the college bus fleet.", selectedType, surfaceColor, accentColor) { viewModel.onTypeChange("transport") }

            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic Form Fields
            AnimatedVisibility(visible = selectedType == "hostel") {
                Column {
                    OutlinedTextField(
                        value = hostelBlock,
                        onValueChange = { viewModel.onHostelBlockChange(it) },
                        label = { Text("Preferred Block (e.g. A, B)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedIndicatorColor = accentColor)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = { viewModel.onRoomNumberChange(it) },
                        label = { Text("Preferred Room Number") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedIndicatorColor = accentColor)
                    )
                }
            }

            AnimatedVisibility(visible = selectedType == "transport") {
                Column {
                    OutlinedTextField(
                        value = busRouteId,
                        onValueChange = { viewModel.onBusRouteChange(it) },
                        label = { Text("Bus Route Number") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedIndicatorColor = accentColor)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = busStop,
                        onValueChange = { viewModel.onBusStopChange(it) },
                        label = { Text("Boarding Bus Stop") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedIndicatorColor = accentColor)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.submitAccommodation() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSubmitting) CircularProgressIndicator(color = backgroundColor, modifier = Modifier.size(24.dp))
                else Text("Confirm Logistics", color = backgroundColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AccommodationOptionCard(
    typeId: String,
    title: String,
    subtitle: String,
    selectedType: String,
    surfaceColor: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    val isSelected = typeId == selectedType
    val borderColor = if (isSelected) accentColor else Color.Transparent

    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = accentColor, unselectedColor = Color.Gray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}