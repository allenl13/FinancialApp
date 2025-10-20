package com.example.financialapp.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    nav: NavController,
    cardId: Long,
    vm: WalletListViewModel = viewModel()
) {
    val card by vm.observeById(cardId).collectAsState(initial = null)
    var menuOpen by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(card?.name ?: "Card") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) { Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Home",
                        tint = MaterialTheme.colorScheme.primary) }
                },
                actions = {
                    IconButton(onClick = { menuOpen = true }) { Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Edit/Delete",
                        tint = MaterialTheme.colorScheme.primary)}
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(text = { Text("Edit") }, onClick = { menuOpen = false; showEdit = true })
                        DropdownMenuItem(text = { Text("Delete") }, onClick = { menuOpen = false; showDelete = true })
                    }
                }
            )
        }
    ) { pad ->
        if (card == null) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                Text("Card not found")
            }
        } else {
            Column(Modifier.fillMaxSize().padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("${card!!.network} •••• ${card!!.last4}", style = MaterialTheme.typography.titleLarge)
                Text("Expires: ${card!!.expire}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    // Edit dialog
    if (showEdit && card != null) {
        EditCardDialog(
            card = card!!,
            onDismiss = { showEdit = false },
            onConfirm = { name, network, last4, expire ->
                vm.update(card!!.id, name, network, last4, expire)
                showEdit = false
            }
        )
    }

    // Delete confirm
    if (showDelete && card != null) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Delete card?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.delete(card!!.id)
                    showDelete = false
                    nav.popBackStack()
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } }
        )
    }
}