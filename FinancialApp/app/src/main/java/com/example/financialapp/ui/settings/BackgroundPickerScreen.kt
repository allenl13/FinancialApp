package com.example.financialapp.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundPickerScreen(
    vm: BackgroundFixedViewModel,
    navController: NavController,
    onApply: () -> Unit,
    onCancel: () -> Unit
) {
    val temp by vm.tempChoice.collectAsState()
    val options = vm.options

    Scaffold(
        topBar = { TopAppBar(title = { Text("Choose Background") }) },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }

                Button(
                    onClick = {
                        vm.applyTemp()
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("bg_refresh", System.currentTimeMillis())

                        onApply()
                    },
                    enabled = temp != null,
                    modifier = Modifier.weight(1f)
                ) { Text("Apply") }
            }
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Simple 2x3 grid of background previews
            val rows = options.chunked(3)
            rows.forEach { row ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { resId ->
                        val isSel = (temp == resId)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { vm.chooseTemp(resId) },
                            border = if (isSel)
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            else
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    if (row.size < 3) repeat(3 - row.size) {
                        Spacer(Modifier.weight(1f).height(110.dp))
                    }
                }
            }
        }
    }
}
