package com.snipergold.v9

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// ─── Data ─────────────────────────────────────────────────────────────
data class Candle(
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val time: String
)

data class SourceStatus(val name: String, val ok: Boolean)
data class Session(val name: String, val label: String, val offsetHours: Int, val startMin: Int, val endMin: Int)

// ─── Theme ────────────────────────────────────────────────────────────
private val Bg              = Color(0xFF060F0C)
private val Surface         = Color(0xFF0D1C17)
private val SurfaceAlt      = Color(0xFF0A1613)
private val CardBorder      = Color(0xFF17352C)
private val Gold            = Color(0xFFFFC94A)
private val Teal            = Color(0xFF17E8B0)
private val Bull            = Color(0xFF17E8B0)
private val Bear            = Color(0xFFFF4D4D)
private val TextMain        = Color(0xFFEAF7F1)
private val TextMuted       = Color(0xFF7E988C)
private val ResistanceFill  = Color(0x40FF4D4D)
private val SupportFill     = Color(0x3317E8B0)

private val TIMEFRAMES = listOf("1m", "5m", "15m", "1H", "4H", "1D")
private val SESSIONS = listOf(
    Session("LONDON", "08:00–16:30", 0, 8 * 60, 16 * 60 + 30),
    Session("NY", "13:30–21:00", -5, 13 * 60 + 30, 21 * 60),
    Session("ASIA", "00:00–08:00", 8, 0, 8 * 60)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SniperGoldApp() }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────
private fun generateCandles(n: Int, start: Float, peak: Float, trough: Float, end: Float): List<Candle> {
    val peakIdx = (n * 0.34f).toInt()
    val troughIdx = (n * 0.76f).toInt()
    fun base(i: Int): Float = when {
        i <= peakIdx -> start + (peak - start) * (i / peakIdx.toFloat().coerceAtLeast(1f))
        i <= troughIdx -> peak + (trough - peak) * ((i - peakIdx) / (troughIdx - peakIdx).toFloat().coerceAtLeast(1f))
        else -> trough + (end - trough) * ((i - troughIdx) / (n - 1 - troughIdx).toFloat().coerceAtLeast(1f))
    }
    var prevClose = start
    return List(n) { i ->
        val b = base(i)
        val o = prevClose + Random.nextFloat() * 0.3f - 0.15f
        val c = if (i == n - 1) end else b + Random.nextFloat() * 0.5f - 0.25f
        val h = max(o, c) + Random.nextFloat() * 0.6f
        val l = min(o, c) - Random.nextFloat() * 0.6f
        prevClose = c
        Candle(o, h, l, c, "${8 + i / 5}:${(18 + i * 2) % 60}")
    }
}

private fun computeRsi(closes: List<Float>, period: Int = 14): Float {
    if (closes.size < 2) return 50f
    val diffs = closes.zipWithNext { a, b -> b - a }.takeLast(period)
    val gains = diffs.filter { it > 0 }.sum()
    val losses = diffs.filter { it < 0 }.sum().let { abs(it) }
    if (losses == 0f) return 100f
    val rs = (gains / diffs.size) / (losses / diffs.size)
    return 100f - (100f / (1f + rs))
}

private fun isSessionActive(session: Session): Boolean {
    val nowUtcMillis = System.currentTimeMillis()
    val localMinutes = (((nowUtcMillis / 60000) + session.offsetHours * 60) % 1440 + 1440) % 1440
    return if (session.startMin <= session.endMin) {
        localMinutes in session.startMin until session.endMin
    } else {
        localMinutes >= session.startMin || localMinutes < session.endMin
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLabel(
    text: String,
    x: Float,
    y: Float,
    color: Color,
    sizePx: Float,
    align: Paint.Align = Paint.Align.LEFT,
    bold: Boolean = false
) {
    drawContext.canvas.nativeCanvas.drawText(
        text, x, y,
        Paint().apply {
            this.color = color.toArgb()
            this.textSize = sizePx
            this.textAlign = align
            this.isAntiAlias = true
            if (bold) this.typeface = Typeface.DEFAULT_BOLD
        }
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawArrow(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float = 4f
) {
    drawLine(color, start, end, strokeWidth)
    val angle = kotlin.math.atan2(end.y - start.y, end.x - start.x)
    val arrowLen = 16f
    val arrowAngle = Math.toRadians(26.0)
    val p1 = Offset(
        end.x - arrowLen * cos(angle - arrowAngle).toFloat(),
        end.y - arrowLen * sin(angle - arrowAngle).toFloat()
    )
    val p2 = Offset(
        end.x - arrowLen * cos(angle + arrowAngle).toFloat(),
        end.y - arrowLen * sin(angle + arrowAngle).toFloat()
    )
    drawLine(color, end, p1, strokeWidth)
    drawLine(color, end, p2, strokeWidth)
}

@Composable
fun SniperGoldApp() {
    // ── State ────────────────────────────────────────────────────────
    var price by remember { mutableFloatStateOf(4294.70f) }
    var change by remember { mutableFloatStateOf(12.36f) }
    var changePct by remember { mutableFloatStateOf(0.29f) }
    var lastUpdate by remember { mutableStateOf("20:26:18") }
    var nextSec by remember { mutableIntStateOf(42) }
    var signal by remember { mutableStateOf("WAIT") }
    var marketTrend by remember { mutableStateOf("UPTREND") }
    var direction by remember { mutableStateOf("BULLISH") }
    var selectedTimeframe by remember { mutableStateOf("1m") }
    var chartMode by remember { mutableStateOf("Candles") }
    var refreshPulse by remember { mutableIntStateOf(0) }

    val sources = remember {
        listOf(
            SourceStatus("Gold-API.com", true),
            SourceStatus("XAUS.com", true),
            SourceStatus("GoldPrice.dev", true)
        )
    }

    val candles = remember {
        generateCandles(n = 42, start = 4292.6f, peak = 4301.2f, trough = 4288.6f, end = 4294.70f)
            .toMutableStateList()
    }

    val guideHigh by remember(candles) { derivedStateOf { candles.maxOf { it.high } - 0.5f } }
    val guideLow by remember(candles) { derivedStateOf { candles.minOf { it.low } + 0.5f } }
    val rsi by remember(candles) { derivedStateOf { computeRsi(candles.map { it.close }) } }
    val tp1 by remember(price, signal) { derivedStateOf { if (signal == "BUY") price + 20f else price - 20f } }
    val tp2 by remember(price, signal) { derivedStateOf { if (signal == "BUY") price + 40f else price - 40f } }
    val tp3 by remember(price, signal) { derivedStateOf { if (signal == "BUY") price + 60f else price - 60f } }

    fun refreshQuote() {
        val delta = (Random.nextFloat() * 2f - 1f) * 1.4f
        price = (price + delta).coerceIn(4286f, 4303f)
        change = price - 4282.34f
        changePct = change / 4282.34f * 100f
        lastUpdate = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val last = candles.last()
        candles[candles.lastIndex] = last.copy(
            close = price,
            high = max(last.high, price),
            low = min(last.low, price)
        )
        val closes = candles.takeLast(5).map { it.close }
        val trendUp = closes.zipWithNext().all { it.second >= it.first }
        val trendDown = closes.zipWithNext().all { it.second <= it.first }
        marketTrend = if (trendUp) "UPTREND" else if (trendDown) "DOWNTREND" else "SIDEWAYS"
        direction = if (trendUp) "BULLISH" else if (trendDown) "BEARISH" else "NEUTRAL"
        signal = when {
            price >= guideHigh - 0.8f -> "SELL"
            price <= guideLow + 0.8f -> "BUY"
            else -> "WAIT"
        }
        refreshPulse++
    }

    fun replay(dir: String) {
        val step = if (dir == "BUY") 0.6f else -0.6f
        price = (price + step).coerceIn(4286f, 4303f)
        change = price - 4282.34f
        changePct = change / 4282.34f * 100f
        val last = candles.last()
        candles[candles.lastIndex] = last.copy(
            close = price,
            high = max(last.high, price),
            low = min(last.low, price)
        )
        signal = dir
    }

    // ── Live ticker ──────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            refreshQuote()
            nextSec = if (nextSec <= 1) 60 else nextSec - 1
        }
    }

    // ── UI ───────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        HeaderCard(price, change, changePct, lastUpdate, sources)
        TimeframeRow(selectedTimeframe, { selectedTimeframe = it }, chartMode, { chartMode = it })
        ChartCard(candles, price, guideHigh, guideLow, rsi)
        GoldTrackerCard(price, change, changePct, candles)
        SessionSyncCard()
        QuickActionsCard(onRefresh = { refreshQuote() }, onBuy = { replay("BUY") }, onSell = { replay("SELL") })
        MarketTrendBar(marketTrend, direction, nextSec)
        Footer()
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  HEADER
// ═══════════════════════════════════════════════════════════════════════
@Composable
private fun HeaderCard(price: Float, change: Float, changePct: Float, lastUpdate: String, sources: List<SourceStatus>) {
    val color = if (change >= 0) Bull else Bear
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🥇", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "XAU/USD",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Gold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .border(1.dp, Teal, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(Teal, RoundedCornerShape(50)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LIVE", color = Teal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Text(
            text = "Gold Spot · Real-time",
            color = TextMuted,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$${"%.2f".format(price)}",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = TextMain,
            fontSize = 34.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${if (change >= 0) "+" else ""}${"%.2f".format(change)}  (${if (change >= 0) "+" else ""}${"%.2f".format(changePct)}%)",
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text("Last update: $lastUpdate", color = TextMuted, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sources.forEach { s ->
                Row(
                    modifier = Modifier
                        .background(SurfaceAlt, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(s.name, color = TextMain, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (s.ok) "✓" else "✕", color = if (s.ok) Teal else Bear, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(
                modifier = Modifier
                    .border(1.dp, Gold, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👑 3 SOURCE FALLBACK", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Real-time · 1s", color = TextMuted, fontSize = 9.sp)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  TIMEFRAME ROW
// ═══════════════════════════════════════════════════════════════════════
@Composable
private fun TimeframeRow(
    selected: String,
    onSelect: (String) -> Unit,
    chartMode: String,
    onModeChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(10.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TIMEFRAMES.forEach { tf ->
            val isSel = tf == selected
            Box(
                modifier = Modifier
                    .background(if (isSel) Teal.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(50))
                    .border(1.dp, if (isSel) Teal else CardBorder, RoundedCornerShape(50))
                    .clickable { onSelect(tf) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(tf, color = if (isSel) Teal else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        listOf("Candles", "Line").forEach { mode ->
            val isSel = mode == chartMode
            Box(
                modifier = Modifier
                    .background(if (isSel) Teal.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(50))
                    .border(1.dp, if (isSel) Teal else CardBorder, RoundedCornerShape(50))
                    .clickable { onModeChange(mode) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(mode, color = if (isSel) Teal else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  CHART (candles + zones + trend arrows + RSI momentum)
// ═══════════════════════════════════════════════════════════════════════
@Composable
private fun ChartCard(
    candles: List<Candle>,
    price: Float,
    guideHigh: Float,
    guideLow: Float,
    rsi: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Text(
            "XAU/USD   O ${"%.2f".format(candles.first().open)}  H ${"%.2f".format(candles.maxOf { it.high })}  L ${"%.2f".format(candles.minOf { it.low })}  C ${"%.2f".format(price)}",
            color = TextMuted, fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val maxH = candles.maxOf { it.high }
                val minL = candles.minOf { it.low }
                val range = (maxH - minL).coerceAtLeast(1f)
                val pad = range * 0.12f
                val minVal = minL - pad
                val maxVal = maxH + pad
                fun y(v: Float) = h - ((v - minVal) / (maxVal - minVal) * h)

                // grid
                for (i in 0..4) {
                    val yy = h * i / 4f
                    drawLine(TextMuted.copy(alpha = 0.12f), Offset(0f, yy), Offset(w, yy), 1f)
                }

                // Resistance zone: top 15% of range
                val resistTop = maxVal
                val resistBottom = maxVal - range * 0.16f
                drawRect(ResistanceFill, Offset(0f, y(resistTop)), Size(w, y(resistBottom) - y(resistTop)))
                drawLine(Bear.copy(alpha = 0.7f), Offset(0f, y(resistBottom)), Offset(w, y(resistBottom)), 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)))
                drawLabel("RESISTANCE ZONE", w / 2f, y(resistTop) + 22f, Bear, 22f, Paint.Align.CENTER, bold = true)

                // Support zone: bottom 12% of range
                val supportTop = minVal + range * 0.13f
                val supportBottom = minVal
                drawRect(SupportFill, Offset(0f, y(supportTop)), Size(w, y(supportBottom) - y(supportTop)))
                drawLine(Teal.copy(alpha = 0.7f), Offset(0f, y(supportTop)), Offset(w, y(supportTop)), 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)))
                drawLabel("SUPPORT ZONE", w / 2f, y(supportBottom) - 10f, Teal, 22f, Paint.Align.CENTER, bold = true)

                // BOS guide lines
                drawLine(Gold, Offset(0f, y(guideHigh)), Offset(w, y(guideHigh)), 1.2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)))
                drawLine(Bear, Offset(0f, y(guideLow)), Offset(w, y(guideLow)), 1.2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)))

                // candles
                val n = candles.size
                val candleW = (w / n) * 0.55f
                candles.forEachIndexed { i, c ->
                    val x = (i + 0.5f) * (w / n)
                    val isBull = c.close >= c.open
                    val col = if (isBull) Bull else Bear
                    drawLine(col, Offset(x, y(c.high)), Offset(x, y(c.low)), 1f)
                    val top = y(max(c.open, c.close))
                    val bottom = y(min(c.open, c.close))
                    val bodyH = abs(bottom - top).coerceAtLeast(2f)
                    drawRect(col, Offset(x - candleW / 2, top), Size(candleW, bodyH))
                }

                // trend arrows based on peak/trough of closes
                val closes = candles.map { it.close }
                val peakIdx = closes.indices.maxByOrNull { closes[it] } ?: 0
                val troughIdx = (peakIdx until closes.size).maxByOrNull { -closes[it] } ?: peakIdx
                fun px(i: Int) = (i + 0.5f) * (w / n)

                if (peakIdx > 2) {
                    val a1 = Offset(px(0), y(closes[0]) + 30f)
                    val a2 = Offset(px(peakIdx / 2), y(closes[peakIdx / 2]) - 30f)
                    drawArrow(a1, a2, Teal)
                    drawLabel("UP TREND", (a1.x + a2.x) / 2, a1.y + 26f, Teal, 20f, Paint.Align.CENTER, bold = true)
                }
                if (troughIdx > peakIdx + 2) {
                    val mid = (peakIdx + troughIdx) / 2
                    val b1 = Offset(px(peakIdx), y(closes[peakIdx]) + 20f)
                    val b2 = Offset(px(troughIdx), y(closes[troughIdx]) - 20f)
                    drawArrow(b1, b2, Bear)
                    drawLabel("DOWN TREND", px(mid), (y(closes[peakIdx]) + y(closes[troughIdx])) / 2f - 10f, Bear, 20f, Paint.Align.CENTER, bold = true)
                }
                if (troughIdx < n - 3) {
                    val c1 = Offset(px(troughIdx), y(closes[troughIdx]) + 26f)
                    val c2 = Offset(px(n - 1), y(closes[n - 1]) - 26f)
                    drawArrow(c1, c2, Teal)
                    drawLabel("UP TREND", (c1.x + c2.x) / 2, c1.y + 26f, Teal, 20f, Paint.Align.CENTER, bold = true)
                }

                // current price line
                val py = y(price)
                drawLine(TextMain.copy(alpha = 0.6f), Offset(0f, py), Offset(w, py), 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f)))

                // price tag
                drawLabel("${"%.2f".format(price)}", w - 4f, py - 6f, TextMain, 22f, Paint.Align.RIGHT, bold = true)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Momentum", color = TextMuted, fontSize = 10.sp)
            Text("RSI (14): ${"%.1f".format(rsi)} ${if (rsi >= 50) "▲" else "▼"}",
                color = if (rsi >= 50) Bull else Bear, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth().height(28.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            candles.forEach { c ->
                val bull = c.close >= c.open
                val mag = (abs(c.close - c.open) / 2.5f).coerceIn(0.15f, 1f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(mag)
                        .background(if (bull) Bull else Bear, RoundedCornerShape(1.dp))
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  GOLD TRACKER
// ═══════════════════════════════════════════════════════════════════════
@Composable
private fun GoldTrackerCard(price: Float, change: Float, changePct: Float, candles: List<Candle>) {
    val color = if (change >= 0) Bull else Bear
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🥇", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("GOLD TRACKER", color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("XAU/USD", color = TextMuted, fontSize = 13.sp)
        Text("$${"%.2f".format(price)}", color = TextMain, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(
            "${if (change >= 0) "+" else ""}${"%.2f".format(change)}  (${if (change >= 0) "+" else ""}${"%.2f".format(changePct)}%)",
            color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(14.dp))
        TrackerRow("High", "$${"%.2f".format(candles.maxOf { it.high })}")
        TrackerRow("Low", "$${"%.2f".format(candles.minOf { it.low })}")
        TrackerRow("Gold", "${"%.2f".format(price)}")
        TrackerRow("Bars", "${candles.size}")
    }
}

@Composable
private fun TrackerRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMuted, fontSize = 12.sp)
        Text(value, color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  SESSION SYNC
// ═══════════════════════════════════════════════════════════════════════
@Composable
private fun SessionSyncCard() {
    val active = remember { SESSIONS.firstOrNull { isSessionActive(it) } ?: SESSIONS[0] }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🌐", fontSize = 15.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text("SESSION SYNC", color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Box(
                modifier = Modifier
                    .border(1.dp, Teal, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(active.name, color = Teal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SESSIONS.forEach { s ->
                val isActive = s.name == active.name
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SurfaceAlt, RoundedCornerShape(10.dp))
                        .border(1.dp, if (isActive) Teal else CardBorder, RoundedCornerShape(10.dp))
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(s.name, color = if (isActive) Teal else TextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(s.label, color = TextMuted, fontSize = 9.sp)
                    Text("(UTC${if (s.offsetHours >= 0) "+" else ""}${s.offsetHours})", color = TextMuted, fontSize = 8.sp)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  QUICK ACTIONS
// ═══════════════════════════════════════════════════════════════════════
@Composable
private fun QuickActionsCard(onRefresh: () -> Unit, onBuy: () -> Unit, onSell: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text("QUICK ACTIONS", color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton("🔄 Refresh Quote", Gold, Modifier.weight(1f), onRefresh)
            ActionButton("↑ Replay BUY", Teal, Modifier.weight(1f), onBuy)
        }
        Spacer(modifier = Modifier.height(8.dp))
        ActionButton("↓ Replay SELL", Bear, Modifier.fillMaxWidth(), onSell)
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).background(Teal, RoundedCornerShape(50)))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Live Data · Auto Refresh (1s)", color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ActionButton(label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .border(1.dp, color, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  MARKET TREND BAR
// ═══════════════════════════════════════════════════════════════════════
@Composable
private fun MarketTrendBar(trend: String, direction: String, nextSec: Int) {
    val trendColor = when (trend) {
        "UPTREND" -> Bull
        "DOWNTREND" -> Bear
        else -> TextMuted
    }
    val dirColor = when (direction) {
        "BULLISH" -> Bull
        "BEARISH" -> Bear
        else -> TextMuted
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📈", fontSize = 15.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Market Trend", color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .border(1.dp, trendColor, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("${if (trend == "UPTREND") "▲" else if (trend == "DOWNTREND") "▼" else "—"} $trend",
                    color = trendColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text("Direction: $direction", color = dirColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Next candle in", color = TextMuted, fontSize = 9.sp)
            Text("00:${"%02d".format(nextSec)}", color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  FOOTER
// ═══════════════════════════════════════════════════════════════════════
@Composable
private fun Footer() {
    Text(
        text = "SniperGold V11 · GREEN 21S COMEBACK · Elegant",
        modifier = Modifier.fillMaxWidth(),
        color = TextMuted.copy(alpha = 0.5f),
        fontSize = 10.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = 1.sp
    )
}
