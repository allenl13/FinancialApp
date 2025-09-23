package com.example.financialapp.ui.goal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.example.financialapp.util.formatAsDmyOrNull
import androidx.compose.material3.HorizontalDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsListScreen(
    nav: NavController,
    vm: GoalsListViewModel = viewModel()
) {
    val items by vm.goals.collectAsState()

    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Saving Goals") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Text("+") }
        }
    ) { pad ->
        LazyColumn(
            contentPadding = pad,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items, key = { it.id }) { g ->
                GoalRow(
                    item = g,
                    onClick = { nav.navigate("goal/${g.id}") }
                )
                HorizontalDivider()
            }
            if (items.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No goals yet. Tap + to create one.")
                    }
                }
            }
        }
    }

    if (showAdd) AddGoalDialog(
        onDismiss = { showAdd = false },
        onConfirm = { name, target, due ->
            vm.create(name, target, due, categoryId = null)
            showAdd = false
        }
    )
}

@Composable
private fun GoalRow(item: GoalListItemUi, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(item.name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = { item.progress }, modifier = Modifier.fillMaxWidth().height(8.dp))
        Spacer(Modifier.height(6.dp))
        Text("${"%.2f".format(item.saved)} / ${"%.2f".format(item.target)} saved",
            style = MaterialTheme.typography.bodyMedium)

        item.dueDateMillis.formatAsDmyOrNull()?.let { d ->
            Spacer(Modifier.height(4.dp))
            Text("Due: $d", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, targetText: String, dueDateText: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var due by remember { mutableStateOf("") } // YYYY-MM-DD (optional)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Saving Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(
                    value = target, onValueChange = { target = it },
                    label = { Text("Target amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = due, onValueChange = { due = it },
                    label = { Text("Due date (DD/MM/YYYY, optional)") }
                )
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(name, target, due.ifBlank { null }) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}