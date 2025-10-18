package com.example.financialapp.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.HorizontalDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    vm: SavingCategoryViewModel = viewModel()
) {
    val items by vm.categories.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Saving Categories") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Text("+") }
        }
    ) { pad ->
        LazyColumn(
            contentPadding = pad,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items, key = { it.id }) { c ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(safeColor(c.colorHex))
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(c.name, style = MaterialTheme.typography.titleMedium)
                }
                HorizontalDivider()
            }
            if (items.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("No categories yet. Tap + to create one.") }
                }
            }
        }
    }

    if (showAdd) AddCategoryDialog(
        onDismiss = { showAdd = false },
        onConfirm = { name, color ->
            vm.create(name, color); showAdd = false
        }
    )
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val palette = listOf("#FF6D00", "#2962FF", "#00C853", "#AA00FF", "#D50000")
    var selected by remember { mutableStateOf(palette.first()) }

    val canSave = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    palette.forEach { hex ->
                        val size = if (hex == selected) 28.dp else 24.dp
                        Box(
                            modifier = Modifier
                                .size(size)
                                .background(safeColor(hex))
                                .clickable { selected = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = canSave, onClick = { onConfirm(name, selected) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun safeColor(hex: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(hex))
}.getOrElse { Color.Gray }
