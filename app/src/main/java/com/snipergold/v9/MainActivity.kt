package com.snipergold.v9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

data class CandleData(val open: Float, val high: Float, val low: Float, val close: Float, val time: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SniperGoldV11Pro() }
    }
}

@Composable
fun SniperGoldV11Pro() {
    // LIVE DATA - 3 SOURCE FALLBACK SIMULATION
    var price by remember { mutableStateOf(4294.70f) }
    var change by remember { mutableStateOf(12.36f) }
    var changePct by remember { mutableStateOf(0.29f) }
    var lastUpdate by remember { mutableStateOf("8:25:14 PM") }
    var guideHigh by remember { mutableStateOf(4302.00f) } // BOS high
    var guideLow by remember { mutableStateOf(4287.00f) } // BOS low
    var pattern by remember { mutableStateOf("M") }
    var signal by remember { mutableStateOf("WAIT") }
    var marketTrend by remember { mutableStateOf("UPTREND") }
    var direction by remember { mutableStateOf("BULLISH") }
    var timeframe by remember { mutableStateOf("1m") }
    var isCandleMode by remember { mutableStateOf(true) }
    var nextCandleSec by remember { mutableStateOf(46) }
    
    // Generate realistic candles
    var candles by remember { mutableStateOf(List(30) { i ->
        val base = 4291f + Random.nextFloat() * 10f - 5f + (i - 15) * 0.3f
        val o = base + Random.nextFloat() * 2 - 1
        val c = base + Random.nextFloat() * 2 - 1
        val h = maxOf(o, c) + Random.nextFloat() * 1.5f
        val l = minOf(o, c) - Random.nextFloat() * 1.5f
        CandleData(o, h, l, c, "${8 + i/5}:${18 + (i*2)%60}")
    })}
    
    // LIVE 1-SEC UPDATE
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            val delta = (Random.nextFloat() * 2 - 1) * 1.2f
            val newPrice = (price + delta).coerceIn(4286f, 4303f)
            change = newPrice - 4282.34f
            changePct = (change / 4282.34f * 100)
            price = newPrice
            lastUpdate = "${8}:${25}:${(14 + Random.nextInt(60))} PM"
            nextCandleSec = if (nextCandleSec <= 1) 60 else nextCandleSec - 1
            
            // Update last candle
            candles = candles.dropLast(1) + candles.last().copy(close = newPrice, high = maxOf(candles.last().high, newPrice), low = minOf(candles.last().low, newPrice))
            
            // BOS logic
            guideHigh = candles.maxOf { it.high } - 0.5f
            guideLow = candles.minOf { it.low } + 0.5f
            
            // Trend
            if (candles.takeLast(5).map { it.close }.zipWithNext().all { it.second > it.first }) {
                marketTrend = "UPTREND"
                direction = "BULLISH"
                pattern = "M"
            } else if (candles.takeLast(5).map { it.close }.zipWithNext().all { it.second < it.first }) {
                marketTrend = "DOWNTREND"
                direction = "BEARISH"
                pattern = "W"
            }
            
            // Signal based on BOS
            signal = when {
                newPrice >= guideHigh - 0.8f -> "SELL"
                newPrice <= guideLow + 0.8f -> "BUY"
                else -> "WAIT"
            }
        }
    }

    val tp1 = if (signal == "BUY") price + 20f else price - 20f
    val tp2 = if (signal == "BUY") price + 40f else price - 40f
    val tp3 = if (signal == "BUY") price + 60f else price - 60f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080C12))
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {
        // TOP HEADER - XAU/USD LIVE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF121821), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🪙", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("XAU/USD", color = Color(0xFFFFD700), fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.background(Color(0xFF0A2E1F), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFF00FF88), RoundedCornerShape(12.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("● LIVE", color = Color(0xFF00FF88), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("Gold Spot • Real-time", color = Color.Gray, fontSize = 10.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${"%.2f".format(price)}", color = Color(0xFF00FF88), fontSize = 22.sp, fontWeight = FontWeight.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("+${"%.2f".format(change)} (+${"%.2f".format(changePct)}%) ▲", color = Color(0xFF00FF88), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text("Last update: $lastUpdate", color = Color.Gray, fontSize = 9.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Sources + 3 SOURCE FALLBACK
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.background(Color(0xFF121821), RoundedCornerShape(8.dp)).padding(8.dp)) {
                Column {
                    Row { Text("Gold-API.com", color = Color.Gray, fontSize = 8.sp); Text(" ✅", color = Color(0xFF00FF88), fontSize = 8.sp) }
                    Row { Text("XAUS.com", color = Color.Gray, fontSize = 8.sp); Text(" ✅", color = Color(0xFF00FF88), fontSize = 8.sp) }
                    Row { Text("GoldPrice.dev", color = Color.Gray, fontSize = 8.sp); Text(" ✅", color = Color(0xFF00FF88), fontSize = 8.sp) }
                }
            }
            Box(modifier = Modifier.background(Color(0xFF1A1205), RoundedCornerShape(8.dp)).border(1.dp, Color(0xFFFFA500), RoundedCornerShape(8.dp)).padding(8.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👑", fontSize = 12.sp)
                    Text("3 SOURCE", color = Color(0xFFFFA500), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("FALLBACK", color = Color(0xFFFFA500), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("Real-time • 1s", color = Color.Gray, fontSize = 7.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // TIMEFRAME + Candles/Line
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(6) { idx ->
                    val tf = listOf("1m","5m","15m","1H","4H","1D")[idx]
                    val selected = tf == timeframe
                    Box(
                        modifier = Modifier
                            .background(if (selected) Color(0xFF0A2E1F) else Color(0xFF121821), RoundedCornerShape(20.dp))
                            .border(1.dp, if (selected) Color(0xFF00FF88) else Color(0xFF2A2A2A), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(tf, color = if (selected) Color(0xFF00FF88) else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.background(if (isCandleMode) Color(0xFF0A2E1F) else Color(0xFF121821), RoundedCornerShape(20.dp)).border(1.dp, if (isCandleMode) Color(0xFF00FF88) else Color.Gray, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Text("🕯 Candles", color = if (isCandleMode) Color(0xFF00FF88) else Color.Gray, fontSize = 10.sp)
                }
                Box(modifier = Modifier.background(if (!isCandleMode) Color(0xFF0A2E1F) else Color(0xFF121821), RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Text("📈 Line", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CHART - XAU/USD CANDLES
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(Color(0xFF0F141E), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF1E2A3A), RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val allHigh = candles.maxOf { it.high }
                val allLow = candles.minOf { it.low }
                val range = allHigh - allLow + 10f
                val minVal = allLow - 5f
                fun mapY(v: Float) = h - ((v - minVal) / range * h)
                
                // Grid
                for (i in 0..4) {
                    val y = h * i / 4f
                    drawLine(Color(0xFF1A2535), Offset(0f, y), Offset(w, y), 0.5f)
                }
                
                // BOS high - dashed yellow
                val bosHighY = mapY(guideHigh)
                drawLine(Color(0xFFFFD700), Offset(0f, bosHighY), Offset(w, bosHighY), 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
                // BOS low - dashed red
                val bosLowY = mapY(guideLow)
                drawLine(Color.Red, Offset(0f, bosLowY), Offset(w, bosLowY), 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))

                // Candles
                val candleWidth = w / candles.size * 0.6f
                candles.forEachIndexed { i, c ->
                    val x = (i.toFloat() / candles.size * w) + candleWidth
                    val isGreen = c.close >= c.open
                    val color = if (isGreen) Color(0xFF00FF88) else Color(0xFFFF4444)
                    // wick
                    drawLine(color, Offset(x, mapY(c.high)), Offset(x, mapY(c.low)), 1f)
                    // body
                    val bodyTop = mapY(maxOf(c.open, c.close))
                    val bodyBottom = mapY(minOf(c.open, c.close))
                    drawRect(color, Offset(x - candleWidth/2, bodyTop), androidx.compose.ui.geometry.Size(candleWidth, abs(bodyBottom - bodyTop).coerceAtLeast(2f)))
                }

                // Current price line - green dashed
                val priceY = mapY(price)
                drawLine(Color(0xFF00FF88), Offset(0f, priceY), Offset(w, priceY), 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f)))
            }
            
            // Chart overlays
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("XAU/USD  O: ${"%.2f".format(candles.last().open)}  H: ${"%.2f".format(candles.maxOf { it.high })}  L: ${"%.2f".format(candles.minOf { it.low })}  C: ${"%.2f".format(price)}", color = Color.Gray, fontSize = 8.sp)
                    Text("${"%.2f".format(guideHigh)}", color = Color(0xFFFFD700), fontSize = 8.sp)
                }
                Text("BOS high", color = Color(0xFFFFD700), fontSize = 8.sp)
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text("BOS low", color = Color.Red, fontSize = 8.sp)
                    Box(modifier = Modifier.background(Color(0xFF00FF88), RoundedCornerShape(4.dp)).padding(4.dp)) {
                        Text("${"%.2f".format(price)}", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Market Trend + Direction + Next candle
        Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF121821), RoundedCornerShape(8.dp)).padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Market Trend", color = Color.Gray, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.background(Color(0xFF0A2E1F), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFF00FF88), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(if (marketTrend == "UPTREND") "▲ $marketTrend" else "▼ $marketTrend", color = Color(0xFF00FF88), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Current Direction: $direction", color = Color(0xFF00FF88), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp)).padding(6.dp)) {
                Text("Next candle in: 00:${"%02d".format(nextCandleSec)}", color = Color.White, fontSize = 9.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // GOLD TRACKER + SESSION SYNC (like screenshot)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // GOLD TRACKER
            Box(modifier = Modifier.weight(1.2f).background(Color(0xFF121821), RoundedCornerShape(12.dp)).padding(12.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙 GOLD TRACKER", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("XAU/USD", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("$${"%.2f".format(price)}", color = Color(0xFF00FF88), fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("+${"%.2f".format(change)} (+${"%.2f".format(changePct)}%) ▲", color = Color(0xFF00FF88), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("High", color = Color.Gray, fontSize = 9.sp)
                        Text("$${"%.2f".format(candles.maxOf { it.high })}", color = Color.White, fontSize = 9.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Low", color = Color.Gray, fontSize = 9.sp)
                        Text("$${"%.2f".format(candles.minOf { it.low })}", color = Color.White, fontSize = 9.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Gold", color = Color.Gray, fontSize = 9.sp)
                        Text("${candles.size}", color = Color.White, fontSize = 9.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Forex", color = Color.Gray, fontSize = 9.sp)
                        Text("62.69", color = Color.White, fontSize = 9.sp)
                    }
                }
            }
            // SESSION SYNC + QUICK ACTIONS
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF121821), RoundedCornerShape(12.dp)).padding(10.dp)) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("🌐 SESSION SYNC", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Box(modifier = Modifier.background(Color(0xFF0A2E1F), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("LONDON", color = Color(0xFF00FF88), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.background(Color(0xFF0A2E1F), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFF00FF88), RoundedCornerShape(12.dp)).padding(6.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("LONDON", color = Color(0xFF00FF88), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    Text("08:00-16:30 (UTC)", color = Color.Gray, fontSize = 6.sp)
                                }
                            }
                            Box(modifier = Modifier.background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp)).padding(6.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("NY", color = Color.Gray, fontSize = 8.sp)
                                    Text("13:30-21:00", color = Color.Gray, fontSize = 6.sp)
                                }
                            }
                            Box(modifier = Modifier.background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp)).padding(6.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("ASIA", color = Color.Gray, fontSize = 8.sp)
                                    Text("00:00-08:00", color = Color.Gray, fontSize = 6.sp)
                                }
                            }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF121821), RoundedCornerShape(12.dp)).padding(10.dp)) {
                    Column {
                        Text("QUICK ACTIONS", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.background(Color(0xFF1A1205), RoundedCornerShape(20.dp)).border(1.dp, Color(0xFFFFA500), RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text("↻ Refresh Quote", color = Color(0xFFFFA500), fontSize = 9.sp)
                            }
                            Box(modifier = Modifier.background(Color(0xFF0A2E1F), RoundedCornerShape(20.dp)).border(1.dp, Color(0xFF00FF88), RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text("↑ Replay BUY", color = Color(0xFF00FF88), fontSize = 9.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.background(Color(0xFF2A0A0A), RoundedCornerShape(20.dp)).border(1.dp, Color.Red, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text("↓ Replay SELL", color = Color.Red, fontSize = 9.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("● Live Data • Auto Refresh (1s)", color = Color(0xFF00FF88), fontSize = 8.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ORIGINAL GUIDE + TP + SIGNAL - COMBINED WITH PRO DESIGN
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF121821), RoundedCornerShape(12.dp)).padding(12.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("GUIDE HIGH (BOS high)", color = Color.Gray, fontSize = 9.sp)
                        Text("${"%.2f".format(guideHigh)}", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("GUIDE LOW (BOS low)", color = Color.Gray, fontSize = 9.sp)
                        Text("${"%.2f".format(guideLow)}", color = Color(0xFF00FF88), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("PATTERN", color = Color.Gray, fontSize = 9.sp)
                        Text(pattern, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("TP1-TP5 (GUIDE RESULT BASE) - PRO", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("TP1 50% RR1: $${"%.2f".format(tp1)}", color = Color.Gray, fontSize = 11.sp)
                Text("TP2 30% RR2: $${"%.2f".format(tp2)}", color = Color.Gray, fontSize = 11.sp)
                Text("TP3 10% RR3: $${"%.2f".format(tp3)}", color = Color.Gray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF0F0F0F), RoundedCornerShape(10.dp)).padding(12.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BUY/SELL BASE RA SA PATTERN + GUIDE RESULT", color = Color.Gray, fontSize = 9.sp)
                        Text(signal, color = when(signal){"BUY"->Color(0xFF00FF88);"SELL"->Color.Red;else->Color.Gray}, fontSize = 32.sp, fontWeight = FontWeight.Black)
                        Text(if(signal=="WAIT") "Nag hulat pattern + BOS break · $${"%.2f".format(price)}" else "$signal NOW @ $${"%.2f".format(price)}", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        Text("V11 PRO GOLD TRACKER - SINGLE FRAMEWORK - JETPACK COMPOSE CANVAS", color = Color(0xFF00FF88), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(20.dp))
    }
}
