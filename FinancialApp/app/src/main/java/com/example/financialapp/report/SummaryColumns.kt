package com.example.financialapp.report

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryColumns(modifier: Modifier){
    Row (
        modifier = modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ){
        SummaryColumn(
            title = "Total Balance",
            value = "$ 3, 671.00",
            percent = "+25%",
            percentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        VerticalDivider()
        SummaryColumn(
            title = "Income",
            value = "420.67",
            percent = "-10%",
            percentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        VerticalDivider()
        SummaryColumn(
            title = "Saving",
            value = "900.22",
            percent = "+16%",
            percentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun Prev(){
    SummaryColumns(Modifier.padding(16.dp))
}

@Composable
fun SummaryColumn(
    title: String,
    value: String,
    percent: String,
    percentColor: Color,
    modifier: Modifier = Modifier
){
    Column (modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp)){
        Text(title, color = MaterialTheme.colorScheme.primary)
        Text(
            value, color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(text = percent, color = percentColor)
    }
}