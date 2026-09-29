package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.RoomType
import com.example.ui.components.formatPrice
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomPlannerScreen(
    viewModel: WciViewModel,
    onNavigateToCart: () -> Unit
) {
    val blueprint by viewModel.currentBlueprint.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedItemId by remember { mutableStateOf<String?>(null) }
    var showAddFurnitureSheet by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var budgetTarget by remember { mutableDoubleStateOf(5000.0) }
    var roomWidthMeters by remember { mutableFloatStateOf(blueprint.widthMeters) }
    var roomLengthMeters by remember { mutableFloatStateOf(blueprint.lengthMeters) }
    var exportSuccessMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("room_planner_screen")
    ) {
        // 1. Planner Top Info Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = blueprint.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${String.format("%.1f", roomWidthMeters)}m × ${String.format("%.1f", roomLengthMeters)}m • ${blueprint.flooringType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { showBudgetDialog = true },
                            modifier = Modifier.testTag("budget_calc_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Budget Calculator",
                                tint = BrassGold
                            )
                        }
                        Button(
                            onClick = {
                                // Add all placed items to cart
                                blueprint.items.forEach { placed ->
                                    val prod = viewModel.repository.getProductById(placed.productId)
                                    if (prod != null) {
                                        viewModel.addToCart(prod, prod.availableSwatches.first(), assembly = true)
                                    }
                                }
                                onNavigateToCart()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("Buy Suite (${blueprint.items.size})", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // Modular Smart Footprint Recommendation Pill
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = SandStone.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = null,
                            tint = WalnutBrown,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Smart Recommendation: Room has 1.3m clearance. Optimal for 3-seater + floating armchair.",
                            style = MaterialTheme.typography.labelSmall,
                            color = WalnutBrown
                        )
                    }
                }
            }
        }

        // 2. Interactive 2D/3D Blueprint Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFEDE9E3))
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(blueprint.items) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                // Hit test placed items
                                val canvasWidth = size.width.toFloat()
                                val canvasHeight = size.height.toFloat()
                                val scaleX = canvasWidth / roomWidthMeters
                                val scaleY = canvasHeight / roomLengthMeters

                                val clicked = blueprint.items.findLast { item ->
                                    val itemLeft = item.x * scaleX
                                    val itemTop = item.y * scaleY
                                    val itemRight = itemLeft + item.widthMeters * scaleX
                                    val itemBottom = itemTop + item.depthMeters * scaleY
                                    offset.x in itemLeft..itemRight && offset.y in itemTop..itemBottom
                                }
                                selectedItemId = clicked?.id
                            },
                            onDrag = { _, dragAmount ->
                                val currentId = selectedItemId ?: return@detectDragGestures
                                val canvasWidth = size.width.toFloat()
                                val canvasHeight = size.height.toFloat()
                                val scaleX = canvasWidth / roomWidthMeters
                                val scaleY = canvasHeight / roomLengthMeters

                                val item = blueprint.items.find { it.id == currentId } ?: return@detectDragGestures
                                val newX = item.x + (dragAmount.x / scaleX)
                                val newY = item.y + (dragAmount.y / scaleY)
                                viewModel.moveBlueprintItem(currentId, newX, newY)
                            }
                        )
                    }
            ) {
                val canvasW = size.width
                val canvasH = size.height

                // Draw Grid Background
                val gridSpacing = 40f
                var x = 0f
                while (x < canvasW) {
                    drawLine(
                        color = Color(0x33B8860B),
                        start = Offset(x, 0f),
                        end = Offset(x, canvasH),
                        strokeWidth = 1f
                    )
                    x += gridSpacing
                }
                var y = 0f
                while (y < canvasH) {
                    drawLine(
                        color = Color(0x33B8860B),
                        start = Offset(0f, y),
                        end = Offset(canvasW, y),
                        strokeWidth = 1f
                    )
                    y += gridSpacing
                }

                // Outer Wall Boundary
                drawRect(
                    color = Color(0xFF2C221C),
                    topLeft = Offset(4f, 4f),
                    size = Size(canvasW - 8f, canvasH - 8f),
                    style = Stroke(width = 8f)
                )

                // Scaling factors
                val scaleX = canvasW / roomWidthMeters
                val scaleY = canvasH / roomLengthMeters

                // Placed furniture items
                blueprint.items.forEach { item ->
                    val isSelected = item.id == selectedItemId
                    val left = item.x * scaleX
                    val top = item.y * scaleY
                    val w = item.widthMeters * scaleX
                    val h = item.depthMeters * scaleY

                    rotate(degrees = item.rotationDegrees, pivot = Offset(left + w / 2, top + h / 2)) {
                        // Drop shadow
                        drawRect(
                            color = Color(0x33000000),
                            topLeft = Offset(left + 6f, top + 6f),
                            size = Size(w, h)
                        )
                        // Furniture body
                        drawRect(
                            color = if (isSelected) Color(0xFFC89A3E) else Color(item.colorHex),
                            topLeft = Offset(left, top),
                            size = Size(w, h)
                        )
                        // Border
                        drawRect(
                            color = if (isSelected) Color(0xFFFFFFFF) else Color(0xFF2C221C),
                            topLeft = Offset(left, top),
                            size = Size(w, h),
                            style = Stroke(width = if (isSelected) 3f else 1.5f)
                        )
                    }
                }
            }

            // Blueprint Dimensions pill
            Surface(
                color = CharcoalTeak.copy(alpha = 0.85f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Floor Area: ${String.format("%.1f", roomWidthMeters * roomLengthMeters)} m²",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmWhite,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // 3. Selected Item Toolbar (Rotate, Delete, Details)
        if (selectedItemId != null) {
            val selectedItem = blueprint.items.find { it.id == selectedItemId }
            if (selectedItem != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedItem.productName,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rotation: ${selectedItem.rotationDegrees.toInt()}°",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { viewModel.rotateBlueprintItem(selectedItem.id) }) {
                                Icon(Icons.Default.RotateRight, contentDescription = "Rotate 45°")
                            }
                            IconButton(onClick = {
                                viewModel.removeBlueprintItem(selectedItem.id)
                                selectedItemId = null
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MutedTerracotta)
                            }
                        }
                    }
                }
            }
        }

        // 4. Bottom Control Bar: Add Furniture, Flooring, Export
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showAddFurnitureSheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_furniture_blueprint_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = BrassGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Furniture")
                }

                OutlinedButton(
                    onClick = { exportSuccessMessage = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = WalnutBrown)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Blueprint")
                }
            }
        }
    }

    // Add Furniture BottomSheet / Dialog
    if (showAddFurnitureSheet) {
        AlertDialog(
            onDismissRequest = { showAddFurnitureSheet = false },
            title = { Text("Add Furniture to Blueprint") },
            text = {
                Column {
                    Text("Choose a piece to drop onto your room plan:")
                    Spacer(modifier = Modifier.height(10.dp))
                    viewModel.repository.catalogProducts.forEach { product ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.addBlueprintItem(product)
                                    showAddFurnitureSheet = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("${product.widthCm}cm × ${product.depthCm}cm", style = MaterialTheme.typography.labelSmall)
                                }
                                Text(
                                    formatPrice(product.price, uiState.selectedCurrency, uiState.currencyMultiplier),
                                    fontWeight = FontWeight.Bold,
                                    color = BrassGold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddFurnitureSheet = false }) { Text("Cancel") }
            }
        )
    }

    // Interactive Room Budget Calculator Dialog
    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = BrassGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Room Budget Calculator")
                }
            },
            text = {
                Column {
                    Text("Furnishing Target Budget: ${formatPrice(budgetTarget, uiState.selectedCurrency, uiState.currencyMultiplier)}")
                    Slider(
                        value = budgetTarget.toFloat(),
                        onValueChange = { budgetTarget = it.toDouble() },
                        valueRange = 2000f..15000f,
                        colors = SliderDefaults.colors(thumbColor = WalnutBrown, activeTrackColor = BrassGold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val currentTotal = blueprint.estimatedTotalCost
                    val remaining = budgetTarget - currentTotal
                    Text(
                        text = "Current Suite Total: ${formatPrice(currentTotal, uiState.selectedCurrency, uiState.currencyMultiplier)}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (remaining >= 0)
                            "Under budget by ${formatPrice(remaining, uiState.selectedCurrency, uiState.currencyMultiplier)}"
                        else
                            "Exceeds budget by ${formatPrice(-remaining, uiState.selectedCurrency, uiState.currencyMultiplier)}",
                        color = if (remaining >= 0) ForestEco else MutedTerracotta,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showBudgetDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown)
                ) {
                    Text("Done")
                }
            }
        )
    }

    if (exportSuccessMessage) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { exportSuccessMessage = false }) { Text("OK", color = BrassGold) }
            }
        ) {
            Text("Blueprint export ready! High-res PDF and AR scene link generated.")
        }
    }
}
