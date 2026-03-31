package com.example.studentonboarding.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentonboarding.domain.model.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    onNavigateToStage: (Int) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val appState by viewModel.appState.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val surfaceColor = Color(0xFF1E1E1E)
    val accentColor = Color(0xFF00E5FF)
    val successColor = Color(0xFF00E676)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Portal", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                actions = {
                    IconButton(onClick = { viewModel.logout(onNavigateToLogin) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Gray)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToChatbot,
                containerColor = accentColor,
                contentColor = backgroundColor
            ) {
                Text("AI", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (val state = appState) {
                is Resource.Loading -> CircularProgressIndicator(color = accentColor, modifier = Modifier.align(Alignment.CenterHorizontally))
                is Resource.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is Resource.Success -> {
                    val currentStage = state.data.currentStage
                    val isFullyEnrolled = state.data.isFullyEnrolled

                    // 1. Profile Summary Card (Placeholder for now until we fetch the /profile endpoint)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Welcome back,", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                                Text("Student", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 2. Onboarding Status Card
                    Text("Onboarding Status", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, if (isFullyEnrolled) successColor else accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (isFullyEnrolled) {
                                Text("Status: Enrolled ✅", color = successColor, fontWeight = FontWeight.Bold)
                                Text("You have completed all mandatory stages. You can now access your digital ID and college services.", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                            } else {
                                Text("Status: In Progress", color = accentColor, fontWeight = FontWeight.Bold)
                                Text("You are currently on Stage $currentStage of 8.", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onNavigateToStage(currentStage) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                                ) {
                                    Text("Resume Onboarding", color = backgroundColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                null -> {}
            }
        }
    }
}