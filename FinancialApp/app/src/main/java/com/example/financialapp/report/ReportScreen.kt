package com.example.financialapp.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

@Preview
@Composable
fun ReportScreen(){
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (header, card) = createRefs()

        Text(
            text = "Report",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(vertical = 24.dp)
                .constrainAs(header) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        CenterStatsCard(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .constrainAs(card) {
                    top.linkTo(header.bottom, margin = 60.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}

//@Composable
//fun ReportContent(){
//    LazyColumn (
//        modifier = Modifier
//            .background(MaterialTheme.colorScheme.primary)
//    ){
//        item {
//            ConstraintLayout (modifier = Modifier
//                .fillMaxWidth()
//                .height(420.dp)
//            ){
//                val (header, card) = createRefs()
//                CenterStatsCard(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentHeight()
//                        .padding(horizontal = 24.dp)
//                        .constrainAs(card){
//                            top.linkTo(header.bottom)
//                            bottom.linkTo(header.bottom)
//                            start.linkTo(parent.start)
//                            end.linkTo(parent.end)
//                        }
//                )
//            }
//        }
//    }
//}
