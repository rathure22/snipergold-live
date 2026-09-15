package com.snipergold.v9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

data class Candle(val time: String, val o: Float, val h: Float, val l: Float, val c: Float)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FullSniperGoldApp() }
    }
}

@Composable
fun FullSniperGoldApp() {
    var price by remember { mutableStateOf(4294.70f) }
    var change by remember { mutableStateOf(12.36f) }
    var selectedTf by remember { mutableStateOf("1m") }
    var isCandleMode by remember { mutableStateOf(true) }
    var nextCandle by remember { mutableStateOf(42) }

    val candles = remember {
        listOf(
            Candle("8:18", 4290.2f, 4291.5f, 4289.0f, 4290.8f),
            Candle("8:19", 4290.8f, 4292.0f, 4290.0f, 4291.2f),
            Candle("8:19", 4291.2f, 4292.5f, 4290.5f, 4292.0f),
            Candle("8:20", 4292.0f, 4293.5f, 4291.0f, 4293.0f),
            Candle("8:20", 4293.0f, 4294.5f, 4292.2f, 4294.0f),
            Candle("8:21", 4294.0f, 4296.0f, 4293.5f, 4295.5f),
            Candle("8:21", 4295.5f, 4298.0f, 4295.0f, 4297.0f),
            Candle("8:22", 4297.0f, 4300.5f, 4296.5f, 4299.8f),
            Candle("8:22", 4299.8f, 4302.0f, 4298.5f, 4300.2f),
            Candle("8:23", 4300.2f, 4301.0f, 4298.0f, 4299.0f),
            Candle("8:23", 4299.0f, 4300.0f, 4297.5f, 4298.2f),
            Candle("8:24", 4298.2f, 4299.5f, 4296.0f, 4297.0f),
            Candle("8:24", 4297.0f, 4298.0f, 4294.5f, 4295.0f),
            Candle("8:25", 4295.0f, 4296.0f, 4292.0f, 4293.5f),
            Candle("8:25", 4293.5f, 4294.5f, 4290.0f, 4291.0f),
            Candle("8:26", 4291.0f, 4292.0f, 4288.5f, 4289.5f),
            Candle("8:26", 4289.5f, 4291.0f, 4288.0f, 4290.0f),
            Candle("8:27", 4290.0f, 4291.5f, 4289.0f, 4290.5f),
            Candle("8:27", 4290.5f, 4292.5f, 4290.0f, 4292.0f),
            Candle("8:28", 4292.0f, 4294.0f, 4291.5f, 4293.5f),
            Candle("8:28", 4293.5f, 4295.0f, 4292.5f, 4294.7f)
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            price = (price + (Random.nextFloat() - 0.5f) * 0.8f).coerceIn(4288f, 4302f)
            if (nextCandle <= 1) nextCandle = 60 else nextCandle--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080C12))
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        // TOP BAR - XAU/USD LIVE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0E131C), RoundedCornerShape(12.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🏆", fontSize = 24.sp)
            Spacer(Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("XAU/USD", color = Color(0xFFFFD700), fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF00FF88).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) { Text("● LIVE", color = Color(0xFF00FF88), fontSize = 8.sp, fontWeight = FontWeight.Bold) }
                }
                Text("Gold Spot • Real-time", color = Color.Gray, fontSize = 10.sp)
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("$" + String.format("%.2f", price), color = Color(0xFF00FF88), fontSize = 20.sp, fontWeight = FontWeight.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("+" + String.format("%.2f", change) + " (+0.29%) ▲", color = Color(0xFF00FF88), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(6.dp).background(Color(0xFF00FF88), RoundedCornerShape(3.dp)))
                    Spacer(Modifier.width(4.dp))
                    Text("Last update: 8:26:18 PM", color = Color.Gray, fontSize = 8.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Gold-API.com ✓", color = Color.Gray, fontSize = 8.sp)
                Text("XAUS.com ✓", color = Color.Gray, fontSize = 8.sp)
                Text("GoldPrice.dev ✓", color = Color.Gray, fontSize = 8.sp)
            }
        }

        Spacer(Modifier.height(8.dp))

        // TIMEFRAME + MODE
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("1m", "5m", "15m", "1H", "4H", "1D").forEach { tf ->
                val selected = tf == selectedTf
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .background(
                            if (selected) Color(0xFF00FF88).copy(alpha = 0.2f) else Color(0xFF121821),
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            if (selected) 1.dp else 0.dp,
                            if (selected) Color(0xFF00FF88) else Color.Transparent,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) { Text(tf, color = if (selected) Color(0xFF00FF88) else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(if (isCandleMode) Color(0xFF00FF88).copy(alpha = 0.2f) else Color(0xFF121821), RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFF00FF88), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) { Text("📊 Candles", color = Color(0xFF00FF88), fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier.background(Color(0xFF121821), RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 6.dp)
            ) { Text("〰 Line", color = Color.Gray, fontSize = 10.sp) }
        }

        Spacer(Modifier.height(8.dp))

        // MAIN CONTENT - CHART + TRACKER
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // CHART AREA
            Column(
                modifier = Modifier
                    .weight(2.2f)
                    .background(Color(0xFF0E131C), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("XAU/USD", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("O: 4293.80  H: 4295.10  L: 4293.60  C: " + String.format("%.2f", price), color = Color.Gray, fontSize = 8.sp)
                    Text("4302.00", color = Color.Gray, fontSize = 8.sp)
                }

                Box(modifier = Modifier.fillMaxWidth().height(240.dp).padding(top = 4.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val minPrice = 4288f
                        val maxPrice = 4302f
                        val priceRange = maxPrice - minPrice

                        fun priceToY(p: Float): Float {
                            return h - ((p - minPrice) / priceRange * h * 0.85f) - h * 0.07f
                        }

                        // Grid
                        for (i in 0..4) {
                            val y = h * 0.07f + i * (h * 0.85f / 4)
                            drawLine(Color(0xFF1A2332), Offset(0f, y), Offset(w, y), 0.5f)
                        }

                        // RESISTANCE ZONE
                        val resTop = priceToY(4302f)
                        val resBottom = priceToY(4299.5f)
                        drawRect(Color(0xFFFF0000).copy(alpha = 0.15f), Offset(0f, resTop), size = androidx.compose.ui.geometry.Size(w, resBottom - resTop))
                        drawRect(Color(0xFFFF0000).copy(alpha = 0.3f), Offset(0f, resTop), size = androidx.compose.ui.geometry.Size(w, 2f))
                        drawRect(Color(0xFFFF0000).copy(alpha = 0.3f), Offset(0f, resBottom), size = androidx.compose.ui.geometry.Size(w, 2f))

                        // SUPPORT ZONE
                        val supTop = priceToY(4290.5f)
                        val supBottom = priceToY(4288.5f)
                        drawRect(Color(0xFF00FF88).copy(alpha = 0.12f), Offset(0f, supTop), size = androidx.compose.ui.geometry.Size(w, supBottom - supTop))
                        drawRect(Color(0xFF00FF88).copy(alpha = 0.4f), Offset(0f, supTop), size = androidx.compose.ui.geometry.Size(w, 1.5f))
                        drawRect(Color(0xFF00FF88).copy(alpha = 0.4f), Offset(0f, supBottom), size = androidx.compose.ui.geometry.Size(w, 1.5f))

                        // CANDLES
                        val candleWidth = w / candles.size * 0.7f
                        val spacing = w / candles.size * 0.3f

                        candles.forEachIndexed { index, c ->
                            val x = index * (candleWidth + spacing) + spacing
                            val openY = priceToY(c.o)
                            val closeY = priceToY(c.c)
                            val highY = priceToY(c.h)
                            val lowY = priceToY(c.l)

                            val isGreen = c.c >= c.o
                            val color = if (isGreen) Color(0xFF00FF88) else Color(0xFFFF4444)

                            // Wick
                            drawLine(color, Offset(x + candleWidth / 2, highY), Offset(x + candleWidth / 2, lowY), 1.2f)

                            // Body
                            val bodyTop = minOf(openY, closeY)
                            val bodyBottom = maxOf(openY, closeY)
                            val bodyHeight = abs(bodyBottom - bodyTop).coerceAtLeast(3f)
                            if (abs(c.c - c.o) < 0.3f) {
                                drawLine(color, Offset(x, bodyTop), Offset(x + candleWidth, bodyTop), 1.5f)
                            } else {
                                drawRect(color, Offset(x, bodyTop), androidx.compose.ui.geometry.Size(candleWidth, bodyHeight))
                                if (isGreen) {
                                    drawRect(Color(0xFF00FF88).copy(alpha = 0.6f), Offset(x + 1f, bodyTop + 1f), androidx.compose.ui.geometry.Size(candleWidth - 2f, bodyHeight - 2f))
                                }
                            }
                        }

                        // Current price line dashed
                        val currY = priceToY(price)
                        val path = Path()
                        var dash = true
                        var dx = 0f
                        while (dx < w) {
                            if (dash) {
                                path.moveTo(dx, currY)
                                path.lineTo((dx + 8f).coerceAtMost(w), currY)
                            }
                            dx += 12f
                            dash = !dash
                        }
                        drawPath(path, Color(0xFF00FF88).copy(alpha = 0.8f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))

                        // UP TREND arrows (simplified as lines)
                        drawLine(Color(0xFF00FF88), Offset(w * 0.08f, priceToY(4290f)), Offset(w * 0.22f, priceToY(4296f)), 1.5f)
                        drawLine(Color(0xFF00FF88), Offset(w * 0.35f, priceToY(4300.5f)), Offset(w * 0.48f, priceToY(4293f)), 1.5f)
                        drawLine(Color(0xFF00FF88), Offset(w * 0.72f, priceToY(4289f)), Offset(w * 0.88f, priceToY(4294.5f)), 1.5f)
                    }

                    // Labels
                    Box(Modifier.fillMaxSize()) {
                        Text("RESISTANCE ZONE", color = Color(0xFFFF4444), fontSize = 7.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopCenter).background(Color(0xFFFF0000).copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                        Text("SUPPORT ZONE", color = Color(0xFF00FF88), fontSize = 7.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomCenter).background(Color(0xFF00FF88).copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                        Text("UP TREND", color = Color(0xFF00FF88), fontSize = 7.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopStart).padding(start = 24.dp, top = 60.dp))
                        Text("DOWN TREND", color = Color(0xFFFF4444), fontSize = 7.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopCenter).padding(top = 70.dp))
                        Box(Modifier.align(Alignment.CenterEnd).background(Color(0xFF00FF88).copy(alpha = 0.25f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                            Text(String.format("%.2f", price), color = Color(0xFF00FF88), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(Modifier.align(Alignment.BottomStart).padding(bottom = 2.dp)) {
                            listOf("8:18", "8:20", "8:22", "8:24", "8:26", "8:28").forEach {
                                Text(it, color = Color.Gray, fontSize = 7.sp, modifier = Modifier.padding(end = 18.dp))
                            }
                        }
                        Column(Modifier.align(Alignment.TopEnd)) {
                            listOf("4302.00", "4300.00", "4298.00", "4296.00", "4292.00", "4290.00", "4288.60").forEach {
                                Text(it, color = Color.Gray, fontSize = 6.sp, modifier = Modifier.padding(bottom = 12.dp))
                            }
                        }
                    }
                }

                // Momentum
                Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Momentum", color = Color.Gray, fontSize = 7.sp)
                    Spacer(Modifier.weight(1f))
                    Text("RSI (14): 58.4 ▲", color = Color(0xFF00FF88), fontSize = 7.sp)
                }
                Canvas(modifier = Modifier.fillMaxWidth().height(18.dp).padding(top = 2.dp)) {
                    val barCount = 40
                    val bw = size.width / barCount
                    for (i in 0 until barCount) {
                        val h = if (i < 15) 4f + i * 0.6f else if (i < 25) 13f - (i - 15) * 0.8f else 5f + (i - 25) * 0.3f
                        val col = if (i < 22) Color(0xFF00FF88) else if (i < 32) Color(0xFFFF4444) else Color(0xFF00FF88)
                        drawRect(col, Offset(i * bw, size.height - h), androidx.compose.ui.geometry.Size(bw * 0.7f, h))
                    }
                }
            }

            // RIGHT PANEL
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // GOLD TRACKER
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0E131C), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏆", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("GOLD TRACKER", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("XAU/USD", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("$" + String.format("%.2f", price), color = Color(0xFF00FF88), fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Text("+" + String.format("%.2f", change) + " (+0.29%) ▲", color = Color(0xFF00FF88), fontSize = 9.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("High", color = Color.Gray, fontSize = 8.sp)
                        Text("$4352.49", color = Color.White, fontSize = 8.sp)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Low", color = Color.Gray, fontSize = 8.sp)
                        Text("$4294.70", color = Color.White, fontSize = 8.sp)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Gold", color = Color.Gray, fontSize = 8.sp)
                        Text("4294.70", color = Color.White, fontSize = 8.sp)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Forex", color = Color.Gray, fontSize = 8.sp)
                        Text("62.69", color = Color.White, fontSize = 8.sp)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Bars", color = Color.Gray, fontSize = 8.sp)
                        Text("42", color = Color.White, fontSize = 8.sp)
                    }
                }

                // SESSION SYNC
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0E131C), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌐", fontSize = 10.sp)
                            Spacer(Modifier.width(4.dp))
                            Text("SESSION SYNC", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(Modifier.background(Color(0xFF00FF88).copy(alpha = 0.2f), RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("LONDON", color = Color(0xFF00FF88), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Column(Modifier.weight(1f).background(Color(0xFF00FF88).copy(alpha = 0.15f), RoundedCornerShape(6.dp)).border(1.dp, Color(0xFF00FF88), RoundedCornerShape(6.dp)).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("LONDON", color = Color(0xFF00FF88), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            Text("08:00-16:30", color = Color.White, fontSize = 6.sp)
                            Text("(UTC+0)", color = Color.Gray, fontSize = 5.sp)
                        }
                        Column(Modifier.weight(1f).background(Color(0xFF121821), RoundedCornerShape(6.dp)).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("NY", color = Color.Gray, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            Text("13:30-21:00", color = Color.White, fontSize = 6.sp)
                            Text("(UTC-3)", color = Color.Gray, fontSize = 5.sp)
                        }
                        Column(Modifier.weight(1f).background(Color(0xFF121821), RoundedCornerShape(6.dp)).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ASIA", color = Color.Gray, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            Text("00:00-08:00", color = Color.White, fontSize = 6.sp)
                            Text("(UTC+8)", color = Color.Gray, fontSize = 5.sp)
                        }
                    }
                }

                // QUICK ACTIONS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0E131C), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Text("QUICK ACTIONS", color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.weight(1f).background(Color(0xFFFFD700).copy(alpha = 0.15f), RoundedCornerShape(16.dp)).border(1.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp)).padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                            Text("🔄 Refresh Quote", color = Color(0xFFFFD700), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(Modifier.weight(1f).background(Color(0xFF00FF88).copy(alpha = 0.15f), RoundedCornerShape(16.dp)).border(1.dp, Color(0xFF00FF88), RoundedCornerShape(16.dp)).padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                            Text("↑ Replay BUY", color = Color(0xFF00FF88), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(Modifier.fillMaxWidth().background(Color(0xFFFF4444).copy(alpha = 0.15f), RoundedCornerShape(16.dp)).border(1.dp, Color(0xFFFF4444), RoundedCornerShape(16.dp)).padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                        Text("↓ Replay SELL", color = Color(0xFFFF4444), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // MARKET TREND
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0E131C), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📈", fontSize = 10.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("Market Trend", color = Color.Gray, fontSize = 8.sp)
                    }
                    Box(Modifier.background(Color(0xFF00FF88).copy(alpha = 0.15f), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFF00FF88), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text("▲ UPTREND", color = Color(0xFF00FF88), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Current Direction: BULLISH", color = Color(0xFF00FF88), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0E131C), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Next candle in:\n00:" + String.format("%02d", nextCandle), color = Color.Gray, fontSize = 7.sp)
                    Box(Modifier.size(24.dp).background(Color(0xFF00FF88).copy(alpha = 0.2f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Text("↻", color = Color(0xFF00FF88), fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).background(Color(0xFF00FF88), RoundedCornerShape(3.dp)))
                        Spacer(Modifier.width(4.dp))
                        Text("Live Data - Auto Refresh (1s)", color = Color.Gray, fontSize = 6.sp)
                    }
                }
            }
        }
    }
}
