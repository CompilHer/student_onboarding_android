package com.example.studentonboarding.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentonboarding.domain.model.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val rollNumber by viewModel.rollNumber.collectAsState()
    val dob by viewModel.dob.collectAsState()
    val loginState by viewModel.loginState.collectAsState()

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val backgroundColor = Color(0xFF121212)
    val surfaceColor = Color(0xFF1E1E1E)
    val accentColor = Color(0xFF00E5FF)

    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) {
            onNavigateToDashboard()
        }
    }

    // Material 3 Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Format the milliseconds into YYYY-MM-DD
                        // Setting TimeZone to UTC prevents the "off-by-one-day" bug common in date pickers
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        viewModel.onDobChange(formatter.format(Date(millis)))
                    }
                    showDatePicker = false
                }) {
                    Text("Select", color = accentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = surfaceColor)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = surfaceColor,
                    titleContentColor = accentColor,
                    headlineContentColor = Color.White,
                    weekdayContentColor = Color.LightGray,
                    dayContentColor = Color.White,
                    selectedDayContainerColor = accentColor,
                    selectedDayContentColor = backgroundColor,
                    todayContentColor = accentColor,
                    todayDateBorderColor = accentColor
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AI Onboarding Agent",
                    style = MaterialTheme.typography.headlineSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Authenticate to begin enrollment",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Roll Number Field (Now forces uppercase)
                OutlinedTextField(
                    value = rollNumber,
                    onValueChange = { viewModel.onRollNumberChange(it.uppercase()) }, // Force transformation
                    label = { Text("Roll Number", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters), // Auto-shifts keyboard
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = accentColor,
                        unfocusedIndicatorColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // DOB Field (Read-only, clickable to trigger calendar)
                val interactionSource = remember { MutableInteractionSource() }
                if (interactionSource.collectIsPressedAsState().value) {
                    showDatePicker = true
                }

                OutlinedTextField(
                    value = dob,
                    onValueChange = { },
                    readOnly = true, // Prevents manual typing
                    interactionSource = interactionSource,
                    label = { Text("Date of Birth", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date",
                                tint = if (dob.isNotEmpty()) accentColor else Color.Gray
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = if (showDatePicker) accentColor else Color.DarkGray,
                        unfocusedIndicatorColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (loginState is Resource.Error) {
                    Text(
                        text = (loginState as Resource.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = { viewModel.submitLogin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        disabledContainerColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    // Only enable button if both fields have text and we aren't currently loading
                    enabled = loginState !is Resource.Loading && rollNumber.isNotBlank() && dob.isNotBlank()
                ) {
                    if (loginState is Resource.Loading) {
                        CircularProgressIndicator(color = backgroundColor, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Secure Login",
                            color = if (rollNumber.isNotBlank() && dob.isNotBlank()) backgroundColor else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}