package com.example.financialapp.ui.goal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialapp.util.formatAsDmyOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    nav: NavController,
    vm: GoalDetailViewModel = viewModel()
) {
    val goalState by vm.goal.collectAsState()
    val goal = goalState

    var menuOpen by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(goal?.name ?: "Goal") },
                navigationIcon = { TextButton(onClick = { nav.popBackStack() }) { Text("Back") } },
                actions = {
                    TextButton(onClick = { menuOpen = true }) { Text("⋮") }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { menuOpen = false; showEdit = true }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = { menuOpen = false; showDeleteConfirm = true }
                        )
                    }
                }
            )
        }
    ) { pad ->
        if (goal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad),
                contentAlignment = Alignment.Center
            ) { Text("Goal not found") }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(pad)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("${"%.2f".format(goal.saved)} / ${"%.2f".format(goal.target)} saved")

                LinearProgressIndicator(
                    progress = { goal.progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                )

                AssistChip(
                    onClick = {},
                    label = { Text(if (goal.onTrack) "On Track" else "Behind") },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = if (goal.onTrack) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onError,
                        containerColor = if (goal.onTrack) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                )

                goal.dueDate.formatAsDmyOrNull()?.let { d ->
                    Text("Due: $d", style = MaterialTheme.typography.bodyMedium)
                }

                HorizontalDivider()

                // Quick action: ADD saving (accumulates)
                var addAmount by remember { mutableStateOf("") }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = addAmount,
                        onValueChange = { addAmount = it },
                        label = { Text("Add saving") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { vm.addSaving(addAmount); addAmount = "" }) { Text("Add") }
                }
            }
        }
    }

    // EDIT dialog (name + target + due date in DD/MM/YYYY, with "clear due date")
    if (showEdit && goal != null) {
        EditGoalDialog(
            currentName = goal.name,
            currentTarget = goal.target,
            currentDueDmy = goal.dueDate.formatAsDmyOrNull(),
            onDismiss = { showEdit = false },
            onSave = { name, target, due, clearDue ->
                vm.updateAll(name, target, due, clearDue)
                showEdit = false
            }
        )
    }

    // DELETE confirm
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete goal?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { vm.delete(); showDeleteConfirm = false; nav.popBackStack() }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun EditGoalDialog(
    currentName: String,
    currentTarget: Double,
    currentDueDmy: String?,
    onDismiss: () -> Unit,
    onSave: (name: String, targetText: String, dueText: String?, clearDue: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var target by remember { mutableStateOf("%.2f".format(currentTarget)) }
    var due by remember { mutableStateOf(currentDueDmy.orEmpty()) }
    var clearDue by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(
                    value = target, onValueChange = { target = it },
                    label = { Text("Goal amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = due, onValueChange = { due = it },
                    enabled = !clearDue,
                    label = { Text("Due date (DD/MM/YYYY)") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = clearDue, onCheckedChange = { checked -> clearDue = checked })
                    Spacer(Modifier.width(8.dp))
                    Text("Clear due date")
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(name, target, due.ifBlank { null }, clearDue) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}