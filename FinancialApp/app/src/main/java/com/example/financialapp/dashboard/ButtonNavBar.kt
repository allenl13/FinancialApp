package com.example.financialapp.dashboard

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialapp.R
import com.google.ai.client.generativeai.type.content

@Composable
fun ButtonNavBar (
    onItemSelected:(Int) -> Unit,
    modifier: Modifier
    ){
    NavigationBar (
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ){
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor   = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedTextColor   = MaterialTheme.colorScheme.onSecondaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor      = MaterialTheme.colorScheme.secondaryContainer
        )
        NavigationBarItem(
            selected = true,
            onClick = { onItemSelected(R.id.categories) },
            icon = {
                Icon(painter = painterResource(R.drawable.btn_3), contentDescription = null)
                   },
            label = {
                Text(
                    text = "Saving Categories",
                    fontSize = 10.sp
                )
            },
            colors = itemColors
        )
        NavigationBarItem(
            selected = true,
            onClick = { onItemSelected(R.id.chatBot) },
            icon = {
                Icon(painter = painterResource(R.drawable.profile), contentDescription = null)
            },
            label = {
                Text(
                    text = "Chat Bot",
                )
            },
            colors = itemColors
        )
        NavigationBarItem(
            selected = true,
            onClick = { onItemSelected(R.id.settings) },
            icon = {
                Icon(painter = painterResource(R.drawable.futures), contentDescription = null)
            },
            label = {
                Text(
                    text = "Settings"
                )
            },
            colors = itemColors
        )
        NavigationBarItem(
            selected = true,
            onClick = { onItemSelected(R.id.logout) },
            icon = {
                Icon(painter = painterResource(R.drawable.btn_4), contentDescription = null)
            },
            label = {
                Text(
                    text = "Logout"
                )
            },
            colors = itemColors
        )
    }
}