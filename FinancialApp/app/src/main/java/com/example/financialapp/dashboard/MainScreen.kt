package com.example.financialapp.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financialapp.R
import com.example.financialapp.repo.ExpenseDomain
import coil.compose.AsyncImage
import com.example.financialapp.ui.settings.ProfileViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext



@Composable
@Preview (showBackground = true)
fun MainScreenPreview(){
    val expenses = listOf(
        ExpenseDomain("Restaurant", 1000.00, "restaurant", "25 sept 2025 2:06"),
        ExpenseDomain("Investment", 2000.00, "market", "26 sept 2025 2:06"),
        ExpenseDomain("Connors", 3000.00, "trade", "27 sept 2025 2:06"),
        ExpenseDomain("PB Tech", 4000.00, "cinema", "28 sept 2025 2:06"),
        ExpenseDomain("KFC", 5000.00, "mcdonald", "29 sept 2025 2:06"),
        )
    MainScreen(
        expenses = expenses,
        onConvertClick = {},
        onInvestClick = {},
        onSubsClick = {},
        onGoalsClick = {},
        onSettingsClick = {},
        onChatClick = {},
        onCategoryClick = {},
        onLogoutClick = {}
    )
}

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
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Header() }

            item {
                UserCard(onClick = onCardClick)
            }

            item {
                ButtonRow(
                    onConvertClick = onConvertClick,
                    onInvestClick = onInvestClick,
                    onSubsClick = onSubsClick,
                    onGoalsClick = onGoalsClick
                )
            }

            items(
                items = expenses
            ) { item ->
                ExpenseList(item)
            }
        }
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 33.dp, end = 14.dp)
        ) {
            SmallProfileAvatar(onClick = onSettingsClick)
        }
        ButtonNavBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(80.dp),
            onItemSelected = { itemId ->
                when (itemId){
                    R.id.chatBot -> onChatClick()
                    R.id.settings -> onSettingsClick()
                    R.id.categories -> onCategoryClick()
                    R.id.logout -> onLogoutClick()
                }
            }
        )
    }
}

@Composable
private fun SmallProfileAvatar(
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val vm = remember { ProfileViewModel(context) }
    val imageUri by vm.imageUri.collectAsState()

    IconButton(onClick = onClick,modifier = Modifier.size(56.dp)) {
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