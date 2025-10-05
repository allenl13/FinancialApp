package com.example.financialapp.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.financialapp.R
import com.example.financialapp.repo.ExpenseDomain
import com.example.financialapp.wallet.AddCardDialog
import com.example.financialapp.wallet.WalletCard
import com.example.financialapp.wallet.WalletListViewModel

//
//@Composable
//@Preview (showBackground = true)
//fun MainScreenPreview(){
//    val expenses = listOf(
//        ExpenseDomain("Restaurant", 1000.00, "restaurant", "25 sept 2025 2:06"),
//        ExpenseDomain("Investment", 2000.00, "market", "26 sept 2025 2:06"),
//        ExpenseDomain("Connors", 3000.00, "trade", "27 sept 2025 2:06"),
//        ExpenseDomain("PB Tech", 4000.00, "cinema", "28 sept 2025 2:06"),
//        ExpenseDomain("KFC", 5000.00, "mcdonald", "29 sept 2025 2:06"),
//        )
//    MainScreen(
//        expenses = expenses,
//        onConvertClick = {},
//        onInvestClick = {},
//        onSubsClick = {},
//        onGoalsClick = {},
//        onSettingsClick = {},
//        onChatClick = {},
//        onCategoryClick = {},
//        onCardClick = {},
//        onLogoutClick = {},
//        onAddCardClick = {},
//        walletVm = ,
//    ) {}
//}

@Composable
fun MainScreen(
    onCardClick: () -> Unit = { },
    expenses: List<ExpenseDomain>,
    onConvertClick: () -> Unit,
    onInvestClick: () -> Unit,
    onSubsClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onChatClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onLogoutClick: () -> Unit,
    function: () -> Unit,
) {
    val walletVm: WalletListViewModel = viewModel()
    val cards by walletVm.card.collectAsState()

    var showAdd by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp + 72.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Header() }

            item {
                UserCard(
                    onClick = onCardClick
                )
            }

            item {
                ButtonRow(
                    onConvertClick = onConvertClick,
                    onInvestClick = onInvestClick,
                    onSubsClick = onSubsClick,
                    onGoalsClick = onGoalsClick
                )
            }

            if (cards.isNotEmpty()) {
                item {
                    Text(
                        "Your cards",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items(cards, key = { it.id }) { c ->
                    WalletCardRow(c)
                }
            }

        }

        ButtonNavBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(80.dp),
            onItemSelected = { itemId ->
                when (itemId) {
                    R.id.chatBot -> onChatClick()
                    R.id.settings -> onSettingsClick()
                    R.id.categories -> onCategoryClick()
                    R.id.logout -> onLogoutClick()
                }
            }
        )

        ExtendedFloatingActionButton(
            onClick = { showAdd = true },
            text = { Text("Add Card") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp + 16.dp)
                .navigationBarsPadding(),
            icon = { Icon(Icons.Rounded.Add, contentDescription = null) }
        )
    }

    if (showAdd) {
        AddCardDialog(
            onDismiss = { showAdd = false },
            onConfirm = { name, network, last4, expire ->
                walletVm.create(name, network, last4, expire)
                showAdd = false
            }
        )
    }
}

@Composable
private fun WalletCardRow(item: WalletCard) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "${item.network} •••• ${item.last4} | Exp ${item.expire}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Spacer(Modifier.height(8.dp))
}

