package com.example.financialapp.dashboard

// --- Compose & UI imports ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

/**
 * Main screen entry.
 * - Displays the selected background image (shared with Settings via the same ViewModel).
 * - Renders header, user card, actions, and expense list.
 * - Top-right avatar opens Settings; bottom navigation at bottom.
 *
 * @param bgVm Shared BackgroundFixedViewModel (same instance as SettingsScreen)
 */
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
    enableBackground: Boolean = true,
    bgVm: BackgroundFixedViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // optional base color
    ) {
        // 1) Background layer at bottom-most
        if (enableBackground) {
            AppBackgroundLayerFixed(bgVm)
        }

        // 2) Main scrollable content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Header() }
            item { UserCard(onClick = onCardClick) }
            item {
                ButtonRow(
                    onConvertClick = onConvertClick,
                    onInvestClick = onInvestClick,
                    onSubsClick = onSubsClick,
                    onGoalsClick = onGoalsClick
                )
            }
            items(items = expenses) { item ->
                ExpenseList(item)
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

        // 4) Bottom navigation bar
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
    }
}

/**
 * Background image layer that listens to the shared BackgroundFixedViewModel.
 * If no selection exists, it shows nothing.
 * ✅ Reacts instantly when SettingsScreen.applyTemp() is called.
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
    // preview skips background layer
    MainScreen(
        expenses = expenses,
        onConvertClick = {},
        onInvestClick = {},
        onSubsClick = {},
        onGoalsClick = {},
        onSettingsClick = {},
        onChatClick = {},
        onCategoryClick = {},
        onLogoutClick = {},
        enableBackground = false,
        bgVm = BackgroundFixedViewModel(LocalContext.current.applicationContext as android.app.Application)
    )
}
