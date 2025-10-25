package com.example.financialapp.dashboard

// --- Compose & UI imports ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.financialapp.R
import com.example.financialapp.repo.ExpenseDomain
import com.example.financialapp.ui.settings.BackgroundFixedViewModel
import com.example.financialapp.ui.settings.ProfileViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialapp.wallet.AddCardDialog
import com.example.financialapp.wallet.WalletCard
import com.example.financialapp.wallet.WalletListViewModel

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
    onReportClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCardsClick: (Long) -> Unit,               // navigate to card details
    walletVm: WalletListViewModel = viewModel(),
    enableBackground: Boolean = true,
    bgVm: BackgroundFixedViewModel,
    function: () -> Unit
) {
    val cards by walletVm.cards.collectAsState()

    var showAdd by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1) Background layer at bottom-most
        if (enableBackground) {
            AppBackgroundLayerFixed(bgVm)
        }

        // 2) Main scrollable content
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

            // Wallet cards section
            if (cards.isNotEmpty()) {
                item {
                    Text(
                        "Your cards:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items(cards, key = { it.id }) { c ->
                    WalletCardRow(
                        item = c,
                        onClick = { onCardsClick(c.id) }
                    )
                }
            }

        }

        // 3) Top-right small profile avatar (opens Settings)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 33.dp, end = 14.dp)
        ) {
            SmallProfileAvatar(onClick = onSettingsClick)
        }

        // 4) Bottom navigation bar (common)
        ButtonNavBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(80.dp),
            onItemSelected = { itemId ->
                when (itemId) {
                    R.id.chatBot -> onChatClick()
                    R.id.settings -> onSettingsClick()
                    R.id.report -> onReportClick()
                    R.id.logout -> onLogoutClick()
                }
            }
        )

        // FAB: Add Card (team feature)
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
private fun WalletCardRow(
    item: WalletCard,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
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
            Spacer(Modifier.height(8.dp))
        }
    }
}

/**
 * Background image layer that listens to the shared BackgroundFixedViewModel.
 * If no selection exists, it shows nothing.
 * Reacts instantly when SettingsScreen.applyTemp() is called.
 */
@Composable
private fun AppBackgroundLayerFixed(vm: BackgroundFixedViewModel) {
    val selected by vm.selected.collectAsState()

    if (selected != null) {
        Image(
            painter = painterResource(id = selected!!),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Optional: subtle dark overlay for better readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.12f)
                .background(Color.Black)
        )
    }
}

/**
 * Small circular avatar on the top-right corner that opens the Settings screen.
 * Uses the ProfileViewModel to display the chosen profile image.
 */
@Composable
private fun SmallProfileAvatar(onClick: () -> Unit) {
    val context = LocalContext.current
    val vm = remember { ProfileViewModel(context) }
    val imageUri by vm.imageUri.collectAsState()

    IconButton(onClick = onClick, modifier = Modifier.size(56.dp)) {
        AsyncImage(
            model = imageUri ?: R.mipmap.ic_launcher,
            contentDescription = "Profile",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Preview (mocked data, disables background to avoid using viewModel)
 */
@Composable
@Preview(showBackground = true)
fun MainScreenPreview() {
    val expenses = listOf(
        ExpenseDomain("Restaurant", 1000.00, "restaurant", "25 sept 2025 2:06"),
        ExpenseDomain("Investment", 2000.00, "market", "26 sept 2025 2:06"),
        ExpenseDomain("Connors", 3000.00, "trade", "27 sept 2025 2:06"),
        ExpenseDomain("PB Tech", 4000.00, "cinema", "28 sept 2025 2:06"),
        ExpenseDomain("KFC", 5000.00, "mcdonald", "29 sept 2025 2:06"),
    )
    // Preview skips background layer
    MainScreen(
        expenses = expenses,
        onConvertClick = {},
        onInvestClick = {},
        onSubsClick = {},
        onGoalsClick = {},
        onSettingsClick = {},
        onChatClick = {},
        onReportClick = {},
        onLogoutClick = {},
        onCardsClick = { _ -> },
        enableBackground = false,
        bgVm = BackgroundFixedViewModel(LocalContext.current.applicationContext as android.app.Application),
        function = {}
    )
}