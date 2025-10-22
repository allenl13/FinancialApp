package com.example.financialapp.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialapp.R


@Composable
@Preview
fun Header(){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 38.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column{
            Text(
                text = "Hello User",
                color = colorResource(R.color.purple_700),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "User@email.com",
                color = colorResource(R.color.purple_700),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

    }

}
