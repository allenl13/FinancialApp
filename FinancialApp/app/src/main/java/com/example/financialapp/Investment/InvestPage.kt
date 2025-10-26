package com.example.financialapp.Investment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun InvestPage(viewModel: InvestViewModel) {
    val portfolio by viewModel.portfolio.collectAsState()
    val prices by viewModel.prices.collectAsState()
    var symbol by remember { mutableStateOf("") }
    var numOfShares by remember { mutableStateOf("") }
    var buyPrice by remember { mutableStateOf("") }

    // Refresh quotes for all symbols (Alpha Vantage ~5 calls/min -> 12s per call safe;
    LaunchedEffect(portfolio) {
        val symbols = portfolio.map { it.nameInvest }.distinct()
        for (s in symbols) {
            viewModel.refreshQuote(s)
            delay(1500) // throttle
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Investment Tracking",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("My Portfolio", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            }

            // Holdings list
            items(portfolio) { inv ->
                Text("${inv.shares} shares of ${inv.nameInvest} @ ${inv.price}", color = MaterialTheme.colorScheme.primary)
            }

            // Stock symbol
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Enter Stock Name / Symbol (e.g. AAPL)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = symbol,
                            onValueChange = { symbol = it.uppercase() },
                            label = { Text("Stock name / symbol (e.g., AAPL)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // Shares
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Amount of Shares bought",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = numOfShares,
                            onValueChange = { numOfShares = it },
                            label = { Text("Amount of Shares bought") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Buy price
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Buy Price",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = buyPrice,
                            onValueChange = { buyPrice = it },
                            label = { Text("Buy price") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Add button
            item {
                Button(
                    onClick = {
                        val q = numOfShares.toDoubleOrNull()
                        val p = buyPrice.toDoubleOrNull()
                        if (symbol.isNotBlank() && q != null && q > 0 && p != null && p >= 0.0) {
                            viewModel.addInvest(symbol.trim(), q, p)
                            symbol = ""; numOfShares = ""; buyPrice = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(50.dp)
                ) {
                    Text("Add investment")
                }
            }

            // Totals
            item {
                Text("Cost basis: $${"%.2f".format(viewModel.cBasis())}", color = MaterialTheme.colorScheme.primary)
                Text("Market value: $${"%.2f".format(viewModel.marktValue())}", color = MaterialTheme.colorScheme.primary)
                Text("Unrealised P/L: $${"%.2f".format(viewModel.unrealisedPL())}", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
