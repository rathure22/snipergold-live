package com.snipergold.v9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CleanGreenApp() }
    }
}

@Composable
fun CleanGreenApp() {
    var price by remember { mutableStateOf(4294.70f) }
    var guideHigh by remember { mutableStateOf(4302f) }
    var guideLow by remember { mutableStateOf(4287f) }
    var signal by remember { mutableStateOf("WAIT") }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            val newPrice = (price + (Random.nextFloat()-0.5f)*1.2f).coerceIn(4286f,4303f)
            price = newPrice
            signal = when {
                newPrice >= guideHigh-0.8f -> "SELL"
                newPrice <= guideLow+0.8f -> "BUY"
                else -> "WAIT"
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF080C12)).verticalScroll(rememberScrollState()).padding(12.dp)) {
        // CLEAN HEADER
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF121821), RoundedCornerShape(12.dp)).padding(16.dp)) {
            Column {
                Text("SniperGold V16", color=Color(0xFFFFD700), fontSize=18.sp, fontWeight=FontWeight.Black)
                Text("CLEAN REPOSITORY - GREEN #17 METHOD", color=Color(0xFF00FF88), fontSize=10.sp, fontWeight=FontWeight.Bold)
                Spacer(modifier=Modifier.height(8.dp))
                Text("XAU/USD: $"+String.format("%.2f", price), color=Color(0xFF00FF88), fontSize=24.sp, fontWeight=FontWeight.Black)
                Text("Guide High: "+String.format("%.2f", guideHigh), color=Color.Red, fontSize=12.sp)
                Text("Guide Low: "+String.format("%.2f", guideLow), color=Color(0xFF00FF88), fontSize=12.sp)
            }
        }
        Spacer(modifier=Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF121821), RoundedCornerShape(12.dp)).padding(16.dp), contentAlignment=Alignment.Center) {
            Column(horizontalAlignment=Alignment.CenterHorizontally) {
                Text(signal, color=when(signal){"BUY"->Color(0xFF00FF88);"SELL"->Color.Red;else->Color.Gray}, fontSize=32.sp, fontWeight=FontWeight.Black)
                Text("CLEAN REPOSITORY - WILL BUILD GREEN", color=Color.Gray, fontSize=10.sp)
            }
        }
        Spacer(modifier=Modifier.height(20.dp))
        Text("Repository Cleaned - Only com.snipergold.v9 - No rathure22", color=Color(0xFF00FF88), fontSize=9.sp, modifier=Modifier.align(Alignment.CenterHorizontally))
    }
}
