package com.example.financialapp.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financialapp.R
import com.example.financialapp.repo.ExpenseDomain
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
    onAddCardClick: () -> Unit,
    walletVm: WalletListViewModel,
    function: () -> Unit
) {
    val cards by walletVm.card.collectAsState() // list comes from VM
    val current = cards.lastOrNull()

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
                    card = current,
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
            onClick = onAddCardClick,
            text = { Text("Add Card") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp + 16.dp)
                .navigationBarsPadding(),
            icon = { Icon(Icons.Rounded.Add, contentDescription = null) }
        )
    }
}