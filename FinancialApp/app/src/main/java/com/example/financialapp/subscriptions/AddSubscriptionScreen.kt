package com.example.financialapp.subscriptions


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(navController: NavController) {
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
                                val result = "$name\n Due: $due\n$$amount / $recurring"
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    "newSub",
                                    result
                                )
                                navController.popBackStack()
                            }
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF475D92),
                            contentColor = Color.White
                        )
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
                label = { Text("Enter Name") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Enter Amount (eg: $9.99)") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            OutlinedTextField(
                value = due,
                onValueChange = { due = it },
                label = { Text("Due Date (eg: DD/MM/YYYY)") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            OutlinedTextField(
                value = recurring,
                onValueChange = { recurring = it },
                label = { Text("Recurring (eg: Weekly/Monthly/Yearly)") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Enter Category (eg: Entertainment)") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )

        }
    }
}