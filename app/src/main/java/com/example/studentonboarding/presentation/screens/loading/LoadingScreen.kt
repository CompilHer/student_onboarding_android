package com.example.studentonboarding.presentation.screens.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentonboarding.domain.model.Resource

@Composable
fun LoadingScreen(
    onNavigateToDashboard: () -> Unit, // Only one destination now!
    viewModel: LoadingViewModel = viewModel()
) {
    val appState by viewModel.appState.collectAsState()
    val backgroundColor = Color(0xFF121212)
    val accentColor = Color(0xFF00E5FF)

    // Listen for the API response and route to the dashboard
    LaunchedEffect(appState) {
        if (appState is Resource.Success) {
            onNavigateToDashboard()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        when (appState) {
            is Resource.Loading, null -> {
                CircularProgressIndicator(color = accentColor)
            }
            is Resource.Error -> {
                Text(
                    text = (appState as Resource.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {} // Success is handled by the LaunchedEffect
        }
    }
}