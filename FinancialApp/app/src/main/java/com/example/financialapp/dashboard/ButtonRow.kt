package com.example.financialapp.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialapp.R

@Composable
//@Preview
fun ButtonRow(
    onConvertClick: () -> Unit,
    onInvestClick: () -> Unit,
    onSubsClick: () -> Unit,
    onGoalsClick: () -> Unit
) {

    // icon grid row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ButtonItem(R.drawable.wallet, "Saving Goal", onClick = onGoalsClick)
        ButtonItem(R.drawable.btn_2, "Conversion Rate", onClick = onConvertClick)
        ButtonItem(R.drawable.btn_1, "Subscription", onClick = onSubsClick)
        ButtonItem(R.drawable.trade, "Portfolio", onClick = onInvestClick)
    }
}

@Composable
fun RowScope.ButtonItem(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .height(78.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.teal_200))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(32.dp))
        Text(
            text = text,
            color = colorResource(R.color.purple_500),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}