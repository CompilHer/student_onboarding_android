package com.example.studentonboarding.presentation.screens.stage1_docs

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
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

@Composable
fun Stage1DocsScreen(
    onNavigateNext: () -> Unit,
    viewModel: Stage1ViewModel = viewModel()
) {
    val context = LocalContext.current
    val docStatus by viewModel.docStatus.collectAsState()
    val uploadingDocType by viewModel.uploadingDocType.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val surfaceColor = Color(0xFF1E1E1E)
    val accentColor = Color(0xFF00E5FF)
    val successColor = Color(0xFF00E676)

    // State to track which document the user just clicked "Upload" for
    var pendingDocType by remember { mutableStateOf<String?>(null) }

    // Android Photo Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && pendingDocType != null) {
            viewModel.uploadFile(context, uri, pendingDocType!!)
        }
        pendingDocType = null // Reset after selection
    }

    // Show temporary messages (like success or error)
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
    ) {
        // 1. The Persistent FSM Progress Tracker
        FsmProgressTracker(currentStage = 1)

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Main Content Area
        Box(modifier = Modifier.fillMaxSize()) {
            when (val status = docStatus) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        color = accentColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Error -> {
                    Text(
                        text = status.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is Resource.Success -> {
                    val data = status.data
                    val verifiedDocs = data.uploads.filter { it.isVerified }.map { it.docType }

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Required Documents",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Please upload clear images for AI OCR verification.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Render a card for each required document
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(data.requiredDocs) { docType ->
                                val isVerified = verifiedDocs.contains(docType)
                                val isUploadingThis = uploadingDocType == docType

                                DocUploadCard(
                                    docType = docType,
                                    isVerified = isVerified,
                                    isUploading = isUploadingThis,
                                    surfaceColor = surfaceColor,
                                    accentColor = accentColor,
                                    successColor = successColor,
                                    onUploadClick = {
                                        pendingDocType = docType
                                        galleryLauncher.launch("image/*") // Opens image picker
                                    }
                                )
                            }
                        }

                        // Next Stage Button (Only enabled if all docs are verified)
                        Button(
                            onClick = onNavigateNext,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .height(50.dp),
                            enabled = data.isComplete,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                disabledContainerColor = Color.DarkGray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Proceed to Fee Payment",
                                color = if (data.isComplete) backgroundColor else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                null -> {}
            }
        }
    }
}

@Composable
fun DocUploadCard(
    docType: String,
    isVerified: Boolean,
    isUploading: Boolean,
    surfaceColor: Color,
    accentColor: Color,
    successColor: Color,
    onUploadClick: () -> Unit
) {
    // Format the raw backend string (e.g., "income_cert" -> "Income Cert")
    val formattedTitle = docType.split("_").joinToString(" ") { it.capitalize() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isVerified) successColor else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = formattedTitle,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (isUploading) {
                CircularProgressIndicator(color = accentColor, modifier = Modifier.size(24.dp))
            } else if (!isVerified) {
                OutlinedButton(
                    onClick = onUploadClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor)
                ) {
                    Text("Upload")
                }
            }
        }
    }
}