package com.example.financialapp.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
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
    var editId by remember { mutableStateOf<Long?>(null) }

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
                        .clickable { editId = c.id }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(android.graphics.Color.parseColor(c.colorHex)))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.name, style = MaterialTheme.typography.titleMedium)
                        Text("Goal: $${"%.2f".format(c.goal)}",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
                HorizontalDivider()
            }
        }
    }

    if (showAdd) AddCategoryDialog(
        onDismiss = { showAdd = false },
        onConfirm = { name, color, goal ->
            vm.create(name, color, goal); showAdd = false
        }
    )

    val currentEdit = items.firstOrNull { it.id == editId }
    if (currentEdit != null) EditGoalDialog(
        category = currentEdit,
        onDismiss = { editId = null },
        onConfirm = { newGoal ->
            vm.updateGoal(currentEdit.id, newGoal); editId = null
        }
    )
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, colorHex: String, goalText: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    val palette = listOf("#FF6D00", "#2962FF", "#00C853", "#AA00FF", "#D50000")
    var selected by remember { mutableStateOf(palette.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(
                    value = goal, onValueChange = { goal = it },
                    label = { Text("Goal amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    palette.forEach { hex ->
                        val c = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(if (hex == selected) 28.dp else 24.dp)
                                .background(c)
                                .clickable { selected = hex }
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(name, selected, goal) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun EditGoalDialog(
    category: CategoryUi,
    onDismiss: () -> Unit,
    onConfirm: (goalText: String) -> Unit
) {
    var goal by remember { mutableStateOf(category.goal.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Goal • ${category.name}") },
        text = {
            OutlinedTextField(
                value = goal, onValueChange = { goal = it },
                label = { Text("New goal amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = { TextButton(onClick = { onConfirm(goal) }) { Text("Update") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
