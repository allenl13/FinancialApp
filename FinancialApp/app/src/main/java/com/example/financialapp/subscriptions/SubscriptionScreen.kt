package com.example.financialapp.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(navController: NavController) {
    var subs by remember {
        mutableStateOf(
            listOf(
                "Netflix\nDue: 28 Sep\n$9.99 / month",
                "Spotify\nDue: 01 Oct\n$15.49 / month",
                "Discord Nitro\nDue: 13 Oct\n$9.99 / month",
                "CityFitness\nDue: 23 Oct\n$15.49 / month"
            )
        )
    }
    val newSubFlow =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("newSub", "")
    val newSub = newSubFlow?.collectAsState(initial = "")?.value

    LaunchedEffect(newSub) {
        if (!newSub.isNullOrBlank()) {
            subs = subs + newSub
            navController.currentBackStackEntry?.savedStateHandle?.set("newSub", "")
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Subscriptions", fontSize = 25.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {navController.navigate("add")},
                modifier = Modifier.padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF475D92),
                    contentColor = Color.White
                )
            ) {
                Text("Add")
            }
            Column(
                modifier = Modifier.padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                subs.forEach { sub ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Text(text = sub, fontSize = 20.sp)
                            Button(
                                onClick = { /*add manage popout*/},
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF475D92),
                                    contentColor = Color.White
                                )

                            ) {
                                Text("Manage")
                            }
                        }
                    }
                }
            }
        }
    }
}