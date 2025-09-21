package com.example.financialapp.Investment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun InvestPage(viewModel: InvestViewModel) {
    val portfolio by viewModel.portfolio.collectAsState()
    val prices by viewModel.prices.collectAsState()

    LaunchedEffect(portfolio) {
        val symbols = portfolio.map { it.nameInvest }.distinct()
        for (s in symbols) {
            viewModel.refreshQuote(s)
            delay(1500) // Alpha Vantage free tier: ~5 calls/min → be nice
        }
    }

    Column {
        Text("My Portfolio", style = MaterialTheme.typography.headlineMedium)

        LazyColumn {
            items(portfolio) { inv ->
                Text("${inv.shares} shares of ${inv.nameInvest} @ ${inv.price}")
            }
        }

        Text("Cost basis: $${"%.2f".format(viewModel.cBasis())}")
        Text("Market value: $${"%.2f".format(viewModel.marktValue())}")
        Text("Unrealised P/L: $${"%.2f".format(viewModel.unrealisedPL())}")
        var sym by remember { mutableStateOf("") }
        var qty by remember { mutableStateOf("") }
        var px by remember { mutableStateOf("") }

        OutlinedTextField(value = sym, onValueChange = { sym = it }, label = { Text("Stock name / symbol (e.g., AAPL)") })
        OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Amount of Shares bought") })
        OutlinedTextField(value = px, onValueChange = { px = it }, label = { Text("Buy price") })
        Button (onClick = {
            val q = qty.toDoubleOrNull()
            val p = px.toDoubleOrNull()
            if (sym.isNotBlank() && q != null && q > 0 && p != null && p >= 0.0) {
                viewModel.addInvest(sym, q, p)
                sym = ""; qty = ""; px = ""
            }
        }) { Text("Add investment") }
    }
}
