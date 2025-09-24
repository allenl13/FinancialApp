package com.example.financialapp.subscriptions

/*
* This manages the screen when you press 'add'
*/

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(navController: NavController, vm: SubViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var due by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var recurring by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Subscription") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (name.isNotBlank() && due.isNotBlank() && amount.isNotBlank()) {

                                val formatAmount =
                                    amount.replace(",", ".").toDoubleOrNull()
                                        ?.let { (it * 100).roundToInt() }
                                        ?: 0
                                vm.insert(
                                    SubEntity(
                                        name = name.ifBlank { "Unknown" },
                                        amount = formatAmount,
                                        dueDate = due.ifBlank { "N/A" },
                                        recurrence = recurring.ifBlank { "Monthly" },
                                        category = category.ifBlank { "Other" }
                                    )
                                )
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF475D92),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.padding(end = 8.dp),
                    ) { Text("Save") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter Name*") },
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF475D92),
                    unfocusedBorderColor = Color.Gray, focusedLabelColor = Color(0xFF475D92),
                    unfocusedLabelColor = Color.DarkGray
                )
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Enter Amount (eg: 9.99)*") },
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF475D92),
                    unfocusedBorderColor = Color.Gray, focusedLabelColor = Color(0xFF475D92),
                    unfocusedLabelColor = Color.DarkGray
                )
            )

            DueDate(selected = due) { due = it }

            RecurringDropDown(selected = recurring) { recurring = it }

            CategoryDropDown(selected = category) { category = it }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DueDate(selected: String, onSelected: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState()
    val focusManager = LocalFocusManager.current
    val displayText: String

    if (selected.isBlank()) {
        displayText = "Choose Date"
    } else {
        displayText = selected
    }

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        readOnly = true,
        label = { Text("Due Date") },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.DateRange, null)
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .onFocusChanged {
                if (it.isFocused) {
                    showDialog = true
                    focusManager.clearFocus(force = true)
                }
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF475D92),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF475D92),
            unfocusedLabelColor = Color.DarkGray
        )

    )
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = dateState.selectedDateMillis
                    if (millis != null) {
                        val formatted =
                            SimpleDateFormat(
                                "dd MMM yyyy",
                                Locale.getDefault()
                            ).format(Date(millis))
                        onSelected(formatted)
                    }
                    showDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringDropDown(selected: String, onSelected: (String) -> Unit) {

    val options = listOf("day", "week", "month", "year")
    var isExpanded by remember { mutableStateOf(false) }
    val displayText: String

    if (selected.isBlank()) {
        displayText = "Choose Billing Cycle"
    } else {
        displayText = selected
    }
    ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = !isExpanded })
    {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Billing Cycle") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(0.9f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF475D92),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF475D92),
                unfocusedLabelColor = Color.DarkGray
            )

        )
        ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            options.forEach { text ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelected(text)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropDown(selected: String, onSelected: (String) -> Unit) {

    val options = listOf(
        "Entertainment",
        "Software & Apps",
        "Utilities",
        "Fitness & Health",
        "Insurance",
        "Other"
    )
    var isExpanded by remember { mutableStateOf(false) }
    val displayText: String

    if (selected.isBlank()) {
        displayText = "Choose Category"
    } else {
        displayText = selected
    }

    ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = !isExpanded })
    {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(0.9f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF475D92),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF475D92),
                unfocusedLabelColor = Color.DarkGray
            )

        )
        ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            options.forEach { text ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelected(text)
                        isExpanded = false
                    }
                )
            }
        }
    }
}