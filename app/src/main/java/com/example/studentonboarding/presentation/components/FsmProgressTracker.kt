package com.example.studentonboarding.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FsmProgressTracker(
    currentStage: Int,
    modifier: Modifier = Modifier
) {
    val totalStages = 8
    val activeColor = Color(0xFF00E5FF) // Cyberpunk / AI Cyan
    val inactiveColor = Color(0xFF333333) // Dark Gray

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..totalStages) {
                val isCompleted = i < currentStage
                val isCurrent = i == currentStage
                val isPending = i > currentStage

                // Animate the size and color for a smooth transition when moving between stages
                val circleSize by animateDpAsState(targetValue = if (isCurrent) 32.dp else 24.dp, label = "size")
                val circleColor by animateColorAsState(targetValue = if (isCompleted || isCurrent) activeColor else inactiveColor, label = "color")
                val elevation by animateDpAsState(targetValue = if (isCurrent) 8.dp else 0.dp, label = "elevation")

                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .shadow(elevation, CircleShape, spotColor = activeColor)
                        .clip(CircleShape)
                        .background(circleColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = i.toString(),
                        color = if (isCompleted || isCurrent) Color.Black else Color.Gray,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Draw the connecting line between circles (except after the 8th stage)
                if (i < totalStages) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(if (isCompleted) activeColor else inactiveColor)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic Stage Title
        Text(
            text = getStageName(currentStage),
            color = activeColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Helper function to display the correct title based on the integer
private fun getStageName(stage: Int): String {
    return when (stage) {
        1 -> "Document Upload & Verification"
        2 -> "Fee Payment"
        3 -> "Course & Elective Registration"
        4 -> "Accommodation & Transport"
        5 -> "IT Setup & Credentials"
        6 -> "Mandatory Compliance"
        7 -> "ID Card Generation"
        8 -> "Final Review & Enrollment"
        else -> "Enrollment Complete"
    }
}