package com.example.studentonboarding.presentation.screens.stage3_courses

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.studentonboarding.data.remote.dto.Course
import com.example.studentonboarding.domain.model.Resource
import com.example.studentonboarding.presentation.components.FsmProgressTracker

@Composable
fun Stage3CoursesScreen(
    onNavigateNext: () -> Unit,
    viewModel: Stage3ViewModel = viewModel()
) {
    val context = LocalContext.current
    val courseData by viewModel.courseData.collectAsState()
    val selectedCoreIds by viewModel.selectedCoreIds.collectAsState()
    val selectedElectiveIds by viewModel.selectedElectiveIds.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val registrationComplete by viewModel.registrationComplete.collectAsState()

    val backgroundColor = Color(0xFF121212)
    val surfaceColor = Color(0xFF1E1E1E)
    val accentColor = Color(0xFF00E5FF)
    val successColor = Color(0xFF00E676)

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(registrationComplete) {
        if (registrationComplete) {
            onNavigateNext()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        FsmProgressTracker(currentStage = 3)

        Box(modifier = Modifier.fillMaxSize()) {
            when (val dataState = courseData) {
                is Resource.Loading -> CircularProgressIndicator(color = accentColor, modifier = Modifier.align(Alignment.Center))
                is Resource.Error -> Text(dataState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is Resource.Success -> {
                    val courses = dataState.data

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Course Registration", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Select your electives for this semester.", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item { Text("Core Courses (Mandatory)", color = accentColor, fontWeight = FontWeight.Bold) }
                            items(courses.core) { course ->
                                CourseCard(
                                    course = course,
                                    isSelected = selectedCoreIds.contains(course.id),
                                    surfaceColor = surfaceColor,
                                    accentColor = accentColor,
                                    onToggle = { /* Core courses cannot be toggled off */ }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(8.dp)) }
                            item { Text("Elective Courses (Choose Optional)", color = accentColor, fontWeight = FontWeight.Bold) }

                            items(courses.electives) { course ->
                                CourseCard(
                                    course = course,
                                    isSelected = selectedElectiveIds.contains(course.id),
                                    surfaceColor = surfaceColor,
                                    accentColor = accentColor,
                                    onToggle = { viewModel.toggleElective(course.id) }
                                )
                            }
                        }

                        // Submit Button
                        Button(
                            onClick = { viewModel.submitRegistration() },
                            modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 8.dp),
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(containerColor = successColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = backgroundColor, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Confirm Registration", color = backgroundColor, fontWeight = FontWeight.Bold)
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
fun CourseCard(
    course: Course,
    isSelected: Boolean,
    surfaceColor: Color,
    accentColor: Color,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = course.code, color = accentColor, style = MaterialTheme.typography.labelMedium)
                Text(text = course.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Credits: ${course.credits}", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                // Empty circle placeholder for unselected
                Box(modifier = Modifier.size(28.dp))
            }
        }
    }
}