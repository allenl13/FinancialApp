package com.example.financialapp.subscriptions

/*
* this shows the main subscriptions page
*/

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(navController: NavController, vm: SubViewModel = viewModel()) {
    val subs by vm.readAllData.observeAsState(emptyList())
    var showManageDialog by remember { mutableStateOf(false) }
    var selectedSub by remember { mutableStateOf<SubEntity?>(null) }

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
                })
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("add") },
                modifier = Modifier.padding(top = 16.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF475D92), contentColor = Color.White
                )
            ) {
                Text("Add")
            }

            if (subs.isEmpty()) {
                Spacer(Modifier.height(100.dp))
                Text("No current subscriptions", fontSize = 20.sp, color = Color.DarkGray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(subs, key = { it.id }) { sub ->
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
                                    Text(sub.name, fontSize = 20.sp)
                                    Text("Due: ${sub.dueDate}", fontSize = 16.sp)
                                    Text(
                                        "$" + "%.2f".format(sub.amount / 100.0) + " / ${sub.recurrence}",
                                        fontSize = 16.sp
                                    )
                                    Text("${sub.category}", fontSize = 16.sp)
                                }


                                Button(
                                    onClick = {
                                        selectedSub = sub
                                        showManageDialog = true
                                    }, colors = ButtonDefaults.buttonColors(
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
            ManageDialog(sub = selectedSub!!, onDismiss = {
                showManageDialog = false
                selectedSub = null
            }, onSave = { updated ->
                vm.update(updated)
                showManageDialog = false
                selectedSub = null
            }, onDelete = {
                vm.delete(selectedSub!!)
                showManageDialog = false
                selectedSub = null
            })
        }
    }

}


@Composable
fun ManageDialog(
    sub: SubEntity, onDismiss: () -> Unit, onSave: (SubEntity) -> Unit, onDelete: () -> Unit
) {
    var editName by remember { mutableStateOf(sub.name) }
    var editAmount by remember { mutableStateOf("%.2f".format(sub.amount / 100.0)) }
    var editDueDate by remember { mutableStateOf(sub.dueDate) }
    var editRecurring by remember { mutableStateOf(sub.recurrence) }
    var editCategory by remember { mutableStateOf(sub.category) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Manage Subscriptions") }, text = {
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
    }, confirmButton = {
        Button(onClick = {
            val formatAmount =
                editAmount.replace(",", ".").toDoubleOrNull()
                    ?.let { (it * 100).roundToInt() }
                    ?: sub.amount

            onSave(
                sub.copy(
                    name = editName.ifBlank { "Unknown" },
                    dueDate = editDueDate.ifBlank { "N/A" },
                    recurrence = editRecurring.ifBlank { "Monthly" },
                    amount = formatAmount,
                    category = editCategory.ifBlank { "Other" })
            )

        }) { Text("Save") }
    }, dismissButton = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onDelete) { Text("Delete") }
        }
    })
}