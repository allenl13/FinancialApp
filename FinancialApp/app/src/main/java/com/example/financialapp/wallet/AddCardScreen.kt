package com.example.financialapp.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddCardScreen(
    onDone: () -> Unit,
    vm: WalletListViewModel = viewModel()
) {
    AddCardDialog(
        onDismiss = onDone,
        onConfirm = { name, network, last4, expire ->
            vm.create(
                name = name.trim(),
                network = network.trim(),
                last4 = last4.trim(),
                expire = expire.trim()
            )
            onDone()
        }
    )
}

@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        network: String,
        last4: String,
        expire: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var network by remember { mutableStateOf("Visa") }
    var last4 by remember { mutableStateOf("") }
    var expire by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Card") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Card nickname") }
                )
                OutlinedTextField(
                    value = network,
                    onValueChange = { network = it },
                    label = { Text("Network (Visa/Mastercard)") }
                )
                OutlinedTextField(
                    value = last4,
                    onValueChange = { last4 = it.take(4) }, // keep at most 4 chars
                    label = { Text("Last 4") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = expire,
                    onValueChange = { expire = it },
                    label = { Text("Expiry (MM/YY)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, network, last4, expire) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditCardDialog(
    card: WalletCard,
    onDismiss: () -> Unit,
    onConfirm: (name: String, network: String, last4: String, expire: String) -> Unit
) {
    var name by remember { mutableStateOf(card.name) }
    var network by remember { mutableStateOf(card.network) }
    var last4 by remember { mutableStateOf(card.last4) }
    var expire by remember { mutableStateOf(card.expire) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Card") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Card nickname") }
                )
                OutlinedTextField(
                    value = network,
                    onValueChange = { network = it },
                    label = { Text("Network (Visa/Mastercard)") }
                )
                OutlinedTextField(
                    value = last4,
                    onValueChange = { last4 = it.take(4) },
                    label = { Text("Last 4") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = expire,
                    onValueChange = { expire = it },
                    label = { Text("Expiry (MM/YY)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    name.trim(),
                    network.trim(),
                    last4.trim(),
                    expire.trim()
                )
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}