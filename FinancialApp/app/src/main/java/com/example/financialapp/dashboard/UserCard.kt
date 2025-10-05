package com.example.financialapp.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialapp.R
import com.example.financialapp.wallet.WalletCard

@Composable
//@Preview
fun UserCard(
    card: WalletCard?,
    onClick: () -> Unit = { },

){
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(230.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .clickable{onClick()}
    ){
        Image(
            painter = painterResource(R.drawable.card),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize(),
        )
        Image(
            painter = painterResource(R.drawable.sim),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
        Image(
            painter = painterResource(R.drawable.visa),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .offset(y = (-80).dp)
        )
        Text(
            text  = "**** ${card?.last4 ?: "1234"}",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier
                .align (Alignment.BottomStart)
                .padding(16.dp)
                .offset(y = (-80).dp)
        )
        Text(
            text  = (card?.network ?: "DEBIT").uppercase(),
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .align (Alignment.TopStart)
                .padding(16.dp)
                .offset(y = 10.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .align (Alignment.BottomCenter)
                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Balance",
                    color = Color.White,
                    fontSize = 17.sp
                )
                Text(
                    text = "$ 123,456,789.00",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}