package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.data.model.CustomProductionStage
import com.example.ui.components.formatPrice
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    viewModel: WciViewModel,
    targetOrderId: String? = null
) {
    val orders by viewModel.userOrders.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val liveProgress by viewModel.liveGpsProgress.collectAsState()
    val liveEta by viewModel.liveEtaMinutes.collectAsState()

    var showRecycleDialog by remember { mutableStateOf(false) }
    var recycleSuccess by remember { mutableStateOf(false) }
    var showModifyDialog by remember { mutableStateOf(false) }
    var modifySuccess by remember { mutableStateOf(false) }

    val activeOrder = orders.firstOrNull() ?: OrderEntity(
        orderId = "WCI-ORD-88219",
        productIdsJson = "WCI-SOFA-01",
        totalAmount = 2539.0,
        orderDate = "Sept 23, 2026",
        status = "OUT_FOR_DELIVERY",
        isExpress = true,
        timeSlot = "Today, 2:00 PM - 5:00 PM",
        address = "742 Evergreen Terrace, San Francisco, CA"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("order_tracking_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Order Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order ${activeOrder.orderId}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Placed on ${activeOrder.orderDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = BrassGold,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "OUT FOR DELIVERY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalTeak,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Destination: ${activeOrder.address}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Scheduled Window: ${activeOrder.timeSlot}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 2. Live GPS Delivery Tracking Map Simulation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalTeak),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Simulated GPS Route Map
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Draw city grid roads
                        drawLine(Color(0xFF332D27), Offset(0f, h * 0.3f), Offset(w, h * 0.3f), strokeWidth = 8f)
                        drawLine(Color(0xFF332D27), Offset(0f, h * 0.7f), Offset(w, h * 0.7f), strokeWidth = 8f)
                        drawLine(Color(0xFF332D27), Offset(w * 0.3f, 0f), Offset(w * 0.3f, h), strokeWidth = 8f)
                        drawLine(Color(0xFF332D27), Offset(w * 0.7f, 0f), Offset(w * 0.7f, h), strokeWidth = 8f)

                        // Active delivery route (green/brass dotted path)
                        val startRoute = Offset(w * 0.15f, h * 0.7f)
                        val waypoint = Offset(w * 0.5f, h * 0.7f)
                        val endRoute = Offset(w * 0.7f, h * 0.3f)

                        drawLine(
                            color = Color(0xFFC89A3E),
                            start = startRoute,
                            end = waypoint,
                            strokeWidth = 5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                        drawLine(
                            color = Color(0xFFC89A3E),
                            start = waypoint,
                            end = endRoute,
                            strokeWidth = 5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )

                        // Destination Home marker
                        drawCircle(color = Color(0xFF386641), radius = 16f, center = endRoute)
                        drawCircle(color = Color(0xFFFFFFFF), radius = 6f, center = endRoute)

                        // Delivery Van current position
                        val currentPos = Offset(
                            x = startRoute.x + (waypoint.x - startRoute.x) * liveProgress * 2,
                            y = startRoute.y
                        )
                        drawCircle(color = Color(0xFFC89A3E), radius = 20f, center = currentPos)
                        drawCircle(color = Color(0xFF2C221C), radius = 12f, center = currentPos)
                    }

                    // Live GPS Info overlay
                    Surface(
                        color = CharcoalTeak.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = BrassGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Driver Marcus Vance • ETA $liveEta mins",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmWhite
                                )
                                Text(
                                    text = "Electric White-Glove Van #4 • 2 Stops Away",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SandStone
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Custom Bespoke Atelier Order Production Stages
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Atelier Bespoke Crafting Timeline",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Transparent visibility into each hand-crafting milestone",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    CustomProductionStage.values().forEachIndexed { index, stage ->
                        val isDone = index <= 4 // QC completed, now in dispatch
                        val isCurrent = index == 5
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDone) ForestEco else if (isCurrent) BrassGold else Color(0xFFCCCCCC)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.Check else Icons.Default.HourglassBottom,
                                        contentDescription = null,
                                        tint = WarmWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                if (index < CustomProductionStage.values().size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(30.dp)
                                            .background(if (isDone) ForestEco else Color(0xFFCCCCCC))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stage.stageName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone || isCurrent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = stage.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Order Actions: Self-service Modification & Old Furniture Recycling
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showModifyDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Modify Order")
                }

                Button(
                    onClick = { showRecycleDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestEco),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.Recycling, contentDescription = null, tint = WarmWhite, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Old Furniture Recycling", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }

    // Recycling Dialog
    if (showRecycleDialog) {
        AlertDialog(
            onDismissRequest = { showRecycleDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Recycling, contentDescription = null, tint = ForestEco)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eco Furniture Recycling")
                }
            },
            text = {
                Text("Our delivery crew can collect your pre-existing sofa or table during delivery and transport it to our verified zero-landfill timber & foam recycling facility.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        recycleSuccess = true
                        showRecycleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestEco)
                ) {
                    Text("Schedule Free Collection")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecycleDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modify Order Dialog
    if (showModifyDialog) {
        AlertDialog(
            onDismissRequest = { showModifyDialog = false },
            title = { Text("Order Modification Cutoff") },
            text = {
                Text("Self-service modifications (swatch finish, delivery notes, or address adjustments) are available until final dispatch.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        modifySuccess = true
                        showModifyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown)
                ) {
                    Text("Confirm Updates")
                }
            },
            dismissButton = {
                TextButton(onClick = { showModifyDialog = false }) { Text("Close") }
            }
        )
    }
}
