package com.example.financialapp.Investment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun InvestPage(viewModel: InvestViewModel) {
    val portfolio by viewModel.portfolio.collectAsState()

    Column {
        Text("My Portfolio", style = MaterialTheme.typography.headlineMedium)

        LazyColumn {
            items(portfolio) { inv ->
                Text("${inv.shares} shares of ${inv.nameInvest} @ ${inv.price}")
            }
        }

        // Total value (with dummy prices for now)
        val total = viewModel.totalInvested(mapOf("AAPL" to 190.0))
        Text("Total Value: $${"%.2f".format(total)}")
    }
}
