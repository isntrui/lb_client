/*
package ru.isntrui.lb.client.ui.views

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OutlinedTextFieldButton(text: String, onButtonClick: @Composable () -> Unit) {
    val buttonText by remember { mutableStateOf(text) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
            onClick = { ModalDateInput() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Set height to match TextField height
                .border(1.dp, Color.Gray) // Border to resemble TextField outline
                .padding(16.dp), // Padding inside the button
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent, // Transparent background
                contentColor = Color.Black // Text color
            )
        ) {
            Text(text = buttonText, fontSize = 16.sp) // Button text
        }
    }
}*/
