package com.snipergold.v9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class CandleData(val open: Float, val high: Float, val low: Float, val close: Float)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SniperGoldV13Fix() }
    }
}

@Composable
fun SniperGoldV13Fix() {
    var price by remember { mutableStateOf(4294.70f) }
    var change by remember { mutableStateOf(12.36f) }
    var guideHigh by remember { mutableStateOf(4302f) }
    var guideLow by remember { mutableStateOf(4287f) }
    var pattern by remember { mutableStateOf("M") }
    var signal by remember { mutableStateOf("WAIT") }
    var marketTrend by remember { mutableStateOf("UPTREND") }
    var direction by remember { mutableStateOf("BULLISH") }
    var nextSec by remember { mutableStateOf(46) }

    var candles by remember { mutableStateOf(List(25) { i ->
        val base = 4291f + Random.nextFloat()*8f -4f + i*0.25f
        val o = base + Random.nextFloat()*1.5f -0.75f
        val c = base + Random.nextFloat()*1.5f -0.75f
        val h = maxOf(o,c) + Random.nextFloat()*1.2f
        val l = minOf(o,c) - Random.nextFloat()*1.2f
        CandleData(o,h,l,c)
    })}

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            val newPrice = (price + (Random.nextFloat()-0.5f)*1.2f).coerceIn(4286f,4303f)
            price = newPrice
            change = newPrice - 4282.34f
            nextSec = if (nextSec<=1) 60 else nextSec-1
            val last = candles.last()
            candles = candles.dropLast(1) + last.copy(close=newPrice, high=maxOf(last.high,newPrice), low=minOf(last.low,newPrice))
            guideHigh = candles.maxOf { it.high } -0.5f
            guideLow = candles.minOf { it.low } +0.5f
            val up = candles.takeLast(5).map{it.close}.zipWithNext().all{it.second>it.first}
            val down = candles.takeLast(5).map{it.close}.zipWithNext().all{it.second<it.first}
            if (up) { marketTrend="UPTREND"; direction="BULLISH"; pattern="M" }
            else if (down) { marketTrend="DOWNTREND"; direction="BEARISH"; pattern="W" }
            signal = when {
                newPrice >= guideHigh-0.8f -> "SELL"
                newPrice <= guideLow+0.8f -> "BUY"
                else -> "WAIT"
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF080C12)).verticalScroll(rememberScrollState()).padding(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF121821), RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("XAU/USD", color=Color(0xFFFFD700), fontWeight=FontWeight.Black, fontSize=16.sp)
                    Spacer(modifier=Modifier.width(6.dp))
                    Box(modifier=Modifier.background(Color(0xFF0A2E1F), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFF00FF88), RoundedCornerShape(12.dp)).padding(horizontal=6.dp, vertical=2.dp)) {
                        Text("LIVE", color=Color(0xFF00FF88), fontSize=8.sp, fontWeight=FontWeight.Bold)
                    }
                }
                Text("Gradle 8.4 FIXED - Same as GREEN #17", color=Color.Gray, fontSize=9.sp)
            }
            Column(horizontalAlignment=Alignment.End) {
                Text("$"+String.format("%.2f", price), color=Color(0xFF00FF88), fontSize=20.sp, fontWeight=FontWeight.Black)
                Text("+"+String.format("%.2f", change), color=Color(0xFF00FF88), fontSize=10.sp, fontWeight=FontWeight.Bold)
            }
        }
        Spacer(modifier=Modifier.height(8.dp))
        Box(modifier=Modifier.fillMaxWidth().height(280.dp).background(Color(0xFF0F141E), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFF1E2A3A), RoundedCornerShape(12.dp)).padding(8.dp)) {
            Canvas(modifier=Modifier.fillMaxSize()) {
                val w=size.width; val h=size.height
                val allHigh=candles.maxOf{it.high}; val allLow=candles.minOf{it.low}
                val range=(allHigh-allLow+10f).coerceAtLeast(1f); val minVal=allLow-5f
                fun mapY(v:Float)=h-((v-minVal)/range*h)
                for(i in 0..4){ val y=h*i/4f; drawLine(Color(0xFF1A2535), Offset(0f,y), Offset(w,y),0.5f) }
                drawLine(Color(0xFFFFD700), Offset(0f,mapY(guideHigh)), Offset(w,mapY(guideHigh)),2f)
                drawLine(Color.Red, Offset(0f,mapY(guideLow)), Offset(w,mapY(guideLow)),2f)
                val candleW=w/candles.size*0.6f
                candles.forEachIndexed { i,c ->
                    val x=(i.toFloat()/candles.size*w)+candleW
                    val col=if(c.close>=c.open) Color(0xFF00FF88) else Color(0xFFFF4444)
                    drawLine(col, Offset(x,mapY(c.high)), Offset(x,mapY(c.low)),1f)
                    val top=mapY(maxOf(c.open,c.close)); val bottom=mapY(minOf(c.open,c.close))
                    drawRect(col, Offset(x-candleW/2,top), androidx.compose.ui.geometry.Size(candleW, (bottom-top).coerceAtLeast(2f)))
                }
                drawLine(Color(0xFF00FF88), Offset(0f,mapY(price)), Offset(w,mapY(price)),1f)
            }
            Column(modifier=Modifier.fillMaxSize()) {
                Text("BOS high "+String.format("%.2f", guideHigh), color=Color(0xFFFFD700), fontSize=8.sp)
                Spacer(modifier=Modifier.weight(1f))
                Text("BOS low "+String.format("%.2f", guideLow), color=Color.Red, fontSize=8.sp)
            }
        }
        Spacer(modifier=Modifier.height(6.dp))
        Row(modifier=Modifier.fillMaxWidth().background(Color(0xFF121821), RoundedCornerShape(8.dp)).padding(10.dp), horizontalArrangement=Arrangement.SpaceBetween) {
            Text(marketTrend, color=Color(0xFF00FF88), fontSize=10.sp, fontWeight=FontWeight.Bold)
            Text(direction, color=Color(0xFF00FF88), fontSize=10.sp, fontWeight=FontWeight.Bold)
            Text("Next: 00:"+String.format("%02d", nextSec), color=Color.White, fontSize=9.sp)
        }
        Spacer(modifier=Modifier.height(10.dp))
        Box(modifier=Modifier.fillMaxWidth().background(Color(0xFF121821), RoundedCornerShape(12.dp)).padding(12.dp)) {
            Column {
                Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
                    Column{ Text("GUIDE HIGH", color=Color.Gray, fontSize=8.sp); Text(String.format("%.2f", guideHigh), color=Color.Red, fontSize=14.sp, fontWeight=FontWeight.Bold) }
                    Column{ Text("GUIDE LOW", color=Color.Gray, fontSize=8.sp); Text(String.format("%.2f", guideLow), color=Color(0xFF00FF88), fontSize=14.sp, fontWeight=FontWeight.Bold) }
                    Column{ Text("PATTERN", color=Color.Gray, fontSize=8.sp); Text(pattern, color=Color.White, fontSize=14.sp, fontWeight=FontWeight.Bold) }
                }
                Spacer(modifier=Modifier.height(8.dp))
                Box(modifier=Modifier.fillMaxWidth().background(Color(0xFF0F0F0F), RoundedCornerShape(8.dp)).padding(12.dp), contentAlignment=Alignment.Center) {
                    Column(horizontalAlignment=Alignment.CenterHorizontally) {
                        Text(signal, color=when(signal){"BUY"->Color(0xFF00FF88);"SELL"->Color.Red;else->Color.Gray}, fontSize=26.sp, fontWeight=FontWeight.Black)
                        Text("Gradle 8.4 FIXED - Based on GREEN #17", color=Color.Gray, fontSize=9.sp)
                    }
                }
            }
        }
        Spacer(modifier=Modifier.height(20.dp))
        Text("V13 FIX GRADLE 9.7.1 ERROR - PINNED TO 8.4 - GREEN #17 METHOD", color=Color(0xFF00FF88), fontSize=8.sp, modifier=Modifier.align(Alignment.CenterHorizontally))
    }
}
