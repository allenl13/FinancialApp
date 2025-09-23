package com.example.financialapp.subscriptions


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(navController: NavController) {
    var subs by remember {
        mutableStateOf(
            listOf(
                "Netflix\nDue: 28 Sep\n$9.99 / month",
                "Spotify\nDue: 01 Oct\n$15.49 / month",
                "Discord Nitro\nDue: 13 Oct\n$9.99 / month",
                "CityFitness\nDue: 23 Oct\n$15.49 / month"
            )
        )
    }
    val newSubFlow =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("newSub", "")
    val newSub = newSubFlow?.collectAsState(initial = "")?.value

    var showManageDialog by remember { mutableStateOf(false) }
    var selectedSub by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(newSub) {
        if (!newSub.isNullOrBlank()) {
            subs = subs + newSub
            navController.currentBackStackEntry?.savedStateHandle?.set("newSub", "")
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Subscriptions", fontSize = 25.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("add") },
                modifier = Modifier.padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF475D92),
                    contentColor = Color.White
                )
            ) {
                Text("Add")
            }
            Column(
                modifier = Modifier.padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                subs.forEach { sub ->
                    val lines = sub.split("\n")
                    val name = lines.getOrNull(0) ?: ""
                    val due = lines.getOrNull(1) ?: ""
                    val amountRec = lines.getOrNull(2) ?: ""
                    val category = lines.getOrNull(3) ?: ""

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, fontSize = 20.sp)
                                Text(due, fontSize = 16.sp)
                                Text(amountRec, fontSize = 16.sp)
                                Text(category, fontSize = 16.sp)
                            }


                            Button(
                                onClick = {
                                    selectedSub = sub
                                    showManageDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF475D92),
                                    contentColor = Color.White
                                )

                            ) {
                                Text("Manage")
                            }
                        }
                    }
                }
            }
        }
    }
    if (showManageDialog && selectedSub != null) {
        ManageDialog(
            sub = selectedSub!!, onDismiss = {
                showManageDialog = false
                selectedSub = null
            },
            onSave = { updated: String ->
                subs = subs.map { if (it == selectedSub) updated else it }
                showManageDialog = false
                selectedSub = null
            },
            onDelete = {
                subs = subs.filter { it != selectedSub }
                showManageDialog = false
                selectedSub = null
            })
    }
}

@Composable
fun ManageDialog(
    sub: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onDelete: () -> Unit
) {
    var editName by remember { mutableStateOf("") }
    var editAmount by remember { mutableStateOf("") }
    var editDueDate by remember { mutableStateOf("") }
    var editRecurring by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("") }

    LaunchedEffect(sub) {
        val lines = sub.split("\n")

        if (lines.isNotEmpty()) {
            editName = lines[0]
        }

        val dueLine = lines.firstOrNull { it.startsWith("Due:") }
        if (dueLine != null) {
            editDueDate = dueLine.removePrefix("Due:").trim()
        }

        val amountLine = lines.firstOrNull { it.contains("$") && it.contains("/") }
        if (amountLine != null) {
            val parts = amountLine.split("/")
            if (parts.isNotEmpty()) {
                val left = parts[0].trim()
                editAmount = left.removePrefix("$")
            }

            if (parts.size >= 2) {
                editRecurring = parts[1].trim()
            }
        }
        val categoryLine = lines.firstOrNull { it.startsWith("Category:") }
        if (categoryLine != null) {
            editCategory = categoryLine.removePrefix("Category:").trim()
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Subscriptions") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                OutlinedTextField(
                    value = editAmount,
                    onValueChange = { editAmount = it },
                    label = { Text("Amount eg: 9.99") },
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                DueDate(selected = editDueDate) { picked -> editDueDate = picked }

                RecurringDropDown(selected = editRecurring) { choice -> editRecurring = choice }

                CategoryDropDown(selected = editCategory) { choice -> editCategory = choice }
            }
        },
        confirmButton = {
            Button(onClick = {
                val name = if (editName.isBlank()) "Unknown" else editName
                val due = if (editDueDate.isBlank()) "N/A" else editDueDate
                val rec = if (editRecurring.isBlank()) "Monthly" else editRecurring
                val amount = if (editAmount.isBlank()) "N/A" else editAmount
                val cat = if (editCategory.isBlank()) "Other" else editCategory


                val updated = buildString {
                    appendLine(name)
                    appendLine("Due: $due")
                    appendLine("$$amount  / $rec")
                    append("Category: $cat")

                }
                onSave(updated)
            }) { Text("Save") }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDelete) { Text("Delete") }
            }
        }
    )
}