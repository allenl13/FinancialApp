package com.example.financialapp.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddCardScreen (
    onDone: () -> Unit,
    vm: WalletListViewModel = viewModel()
){
    AddCardDialog(
        onDismiss = onDone,
        onConfirm = {
                    name,
                    network,
                    last4,
                    expire ->
            vm.create(
                name,
                network,
                last4,
                expire
            )
            onDone()
        }
    )
}

@Composable
fun AddCardDialog (
    onDismiss: () -> Unit,
    onConfirm: (
            name: String,
            network: String,
            last4: String,
            expire: String
            ) -> Unit
){
    var name by remember { mutableStateOf("") }
    var network by remember { mutableStateOf("Visa")}
    var last4 by remember { mutableStateOf("") }
    var expire by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Card")},
        text = {
            Column (
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                OutlinedTextField(name, { name = it }, label = { Text("Card nickname") })
                OutlinedTextField(network,  { network  = it }, label = { Text("Network (Visa/Mastercard)") })
                OutlinedTextField(last4,    { last4    = it }, label = { Text("Last 4") })
                OutlinedTextField(expire,   { expire   = it }, label = { Text("Expiry (MM/YY)") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, network, last4, expire)}) { Text("Save")}
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {Text("Cancel")}
        }
    )
}