package com.example.financialapp.subscriptions

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction

@Composable
fun SearchBar(onSearchChange: (String) -> Unit, modifier: Modifier = Modifier) {

    var search by remember { mutableStateOf("") }

    OutlinedTextField(
        value = search,
        onValueChange = {
            search = it
            onSearchChange(it)
        },
        placeholder = { Text(" Search for a Subscription ") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
        trailingIcon = {
            if (search.isNotEmpty()) {
                IconButton(onClick = {
                    search = ""
                    onSearchChange("")
                }) { Icon(Icons.Default.Close, contentDescription = "Clear icon") }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        modifier = Modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF475D92)
        )
    )
}