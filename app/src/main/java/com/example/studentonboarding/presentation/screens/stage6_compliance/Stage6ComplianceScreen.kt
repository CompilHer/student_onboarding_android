package com.example.studentonboarding.presentation.screens.stage6_compliance

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentonboarding.presentation.components.FsmProgressTracker

@Composable
fun Stage6ComplianceScreen(
    onNavigateNext: () -> Unit,
    viewModel: Stage6ViewModel = viewModel()
) {
    val context = LocalContext.current
    val antiRagging by viewModel.antiRagging.collectAsState()
    val codeOfConduct by viewModel.codeOfConduct.collectAsState()
    val dataConsent by viewModel.dataConsent.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val stageComplete by viewModel.stageComplete.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val accentColor = Color(0xFF00E5FF)
    val cardBg = Color(0xFF1E1E1E)

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(stageComplete) {
        if (stageComplete) {
            onNavigateNext()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        FsmProgressTracker(currentStage = 6)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Compliance",
                tint = accentColor,
                modifier = Modifier.size(64.dp).padding(bottom = 16.dp)
            )

            Text(
                "Digital Compliance & Ethics",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Please review and digitally sign the following mandatory agreements to complete your onboarding.",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            ComplianceItem(
                title = "Anti-Ragging Affidavit",
                description = "I solemnly affirm that I will not indulge in any form of ragging or harassment within the campus.",
                checked = antiRagging,
                onCheckedChange = { viewModel.toggleAntiRagging(it) },
                cardBg = cardBg,
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            ComplianceItem(
                title = "Institutional Code of Conduct",
                description = "I agree to abide by the rules, regulations, and behavioral expectations of the institution.",
                checked = codeOfConduct,
                onCheckedChange = { viewModel.toggleCodeOfConduct(it) },
                cardBg = cardBg,
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            ComplianceItem(
                title = "Data Privacy & Consent",
                description = "I permit the institution to process my personal and academic data for official and educational purposes.",
                checked = dataConsent,
                onCheckedChange = { viewModel.toggleDataConsent(it) },
                cardBg = cardBg,
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.submitCompliance() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = backgroundColor, modifier = Modifier.size(24.dp))
                } else {
                    Text("Digitally Sign & Complete", color = backgroundColor, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ComplianceItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    cardBg: Color,
    accentColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = { onCheckedChange(it) },
                role = Role.Checkbox
            ),
        shape = RoundedCornerShape(12.dp),
        color = cardBg
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Checkbox(
                checked = checked,
                onCheckedChange = null, // Handled by toggleable on surface
                colors = CheckboxDefaults.colors(
                    checkedColor = accentColor,
                    uncheckedColor = Color.Gray,
                    checkmarkColor = Color.Black
                )
            )
        }
    }
}
