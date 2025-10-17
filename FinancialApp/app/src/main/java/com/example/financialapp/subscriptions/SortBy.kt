package com.example.financialapp.subscriptions

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortByDropDown(selected: String, onSelected: (String) -> Unit) {
    val options = listOf(
        "Name (Alphabetical)",
        "Due Date (Upcoming)",
        "Amount (High to Low)",
        "Amount (Low to High)"
    )
    var isExpended by remember { mutableStateOf(false) }
    val displayText: String

    if (selected.isBlank()) {
        displayText = " "
    } else {
        displayText = selected
    }
    ExposedDropdownMenuBox(expanded = isExpended, onExpandedChange = { isExpended = !isExpended })
    {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpended) },
            modifier = Modifier
                .menuAnchor()
                .widthIn(170.dp)
                .heightIn(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF475D92),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF475D92),
                unfocusedLabelColor = Color.DarkGray
            )

        )
        ExposedDropdownMenu(
            expanded = isExpended,
            onDismissRequest = { isExpended = false },
            modifier = Modifier.widthIn(180.dp)
        ) {
            options.forEach { text ->
                DropdownMenuItem(
                    text = { Text(text, maxLines = 1) },
                    onClick = {
                        onSelected(text)
                        isExpended = false
                    }
                )
            }
        }
    }
}
