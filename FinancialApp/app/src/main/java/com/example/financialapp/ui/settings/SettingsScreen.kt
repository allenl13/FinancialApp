package com.example.financialapp.ui.settings



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.financialapp.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    currentMode: ThemeMode,
    currentPrimary: Long,
    onChangeMode: (ThemeMode) -> Unit,
    onChangeColor: (Long) -> Unit
) {
    val swatches = listOf(0xFF6750A4, 0xFF1E88E5, 0xFF43A047, 0xFFFB8C00, 0xFFE53935, 0xFF00897B)
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Appearance", style = MaterialTheme.typography.titleLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Radio("System", currentMode == ThemeMode.SYSTEM) { onChangeMode(ThemeMode.SYSTEM) }
            Radio("Light", currentMode == ThemeMode.LIGHT) { onChangeMode(ThemeMode.LIGHT) }
            Radio("Dark", currentMode == ThemeMode.DARK) { onChangeMode(ThemeMode.DARK) }
        }

        Text("Primary color", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            swatches.forEach { argb ->
                Box(
                    Modifier.size(if (argb == currentPrimary) 36.dp else 28.dp)
                        .clip(CircleShape)
                        .background(Color(argb))
                        .clickable { onChangeColor(argb) }
                )
            }
        }

        Button(onClick = {}) { Text("Primary sample") }
    }
}

@Composable private fun Radio(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.clickable { onClick() }, Arrangement.spacedBy(4.dp)) {
        RadioButton(selected = selected, onClick = onClick); Text(label)
    }
}
