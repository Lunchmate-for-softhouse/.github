package com.example.lunchmate.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color = Color.White,   // Use containerColor in Material 3
    textColor: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),  // Material 3 uses containerColor
        modifier = modifier
            .fillMaxWidth(0.6f) // Default to 60% width
            .padding(8.dp)
    ) {
        Text(text = text, color = textColor, fontWeight = FontWeight.Bold)
    }
}
