package com.example.financialapp.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financialapp.repo.ExpenseDomain

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
    MainScreen(expenses = expenses)
}

@Composable
fun MainScreen(
    onCardClick: () -> Unit = { },
    expenses: List<ExpenseDomain>
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            item {
                Header()
            }
            item {
                UserCard(onClick = onCardClick)
            }
            item {
                ButtonRow()
            }

            items(
                items = expenses
            ){
                item -> ExpenseList(item)
            }
        }
    }
}