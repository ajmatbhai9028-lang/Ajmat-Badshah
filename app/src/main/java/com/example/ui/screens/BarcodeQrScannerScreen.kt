package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeQrScannerScreen(
    viewModel: WciViewModel,
    onNavigateBack: () -> Unit,
    onProductFound: (String) -> Unit
) {
    var manualCodeInput by remember { mutableStateOf("") }
    var scannedResultProduct by remember { mutableStateOf<Product?>(null) }
    var showExplodedManual by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalTeak)
            .testTag("barcode_scanner_screen")
    ) {
        // Scanner Viewfinder Canvas with glowing reticle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val boxSize = w * 0.7f
            val left = (w - boxSize) / 2
            val top = (h - boxSize) / 2

            // Dark semi-transparent mask
            drawRect(color = Color(0x99000000))

            // Cutout target area
            drawRect(
                color = Color(0xFFC89A3E),
                topLeft = Offset(left, top),
                size = Size(boxSize, boxSize),
                style = Stroke(width = 3f)
            )

            // Corner brackets
            val cornerLen = 30f
            // Top-left
            drawLine(Color(0xFFC89A3E), Offset(left, top), Offset(left + cornerLen, top), strokeWidth = 8f)
            drawLine(Color(0xFFC89A3E), Offset(left, top), Offset(left, top + cornerLen), strokeWidth = 8f)
            // Top-right
            drawLine(Color(0xFFC89A3E), Offset(left + boxSize, top), Offset(left + boxSize - cornerLen, top), strokeWidth = 8f)
            drawLine(Color(0xFFC89A3E), Offset(left + boxSize, top), Offset(left + boxSize, top + cornerLen), strokeWidth = 8f)
            // Bottom-left
            drawLine(Color(0xFFC89A3E), Offset(left, top + boxSize), Offset(left + cornerLen, top + boxSize), strokeWidth = 8f)
            drawLine(Color(0xFFC89A3E), Offset(left, top + boxSize), Offset(left, top + boxSize - cornerLen), strokeWidth = 8f)
            // Bottom-right
            drawLine(Color(0xFFC89A3E), Offset(left + boxSize, top + boxSize), Offset(left + boxSize - cornerLen, top + boxSize), strokeWidth = 8f)
            drawLine(Color(0xFFC89A3E), Offset(left + boxSize, top + boxSize), Offset(left + boxSize, top + boxSize - cornerLen), strokeWidth = 8f)
        }

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .background(CharcoalTeak.copy(alpha = 0.8f), CircleShape)
                    .size(44.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WarmWhite)
            }

            Surface(
                color = CharcoalTeak.copy(alpha = 0.85f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Showroom Barcode & QR Scanner",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = WarmWhite,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        // Instructions
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Align Barcode or QR Code",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = WarmWhite
            )
            Text(
                text = "Point camera at showroom tag for instant architectural specifications, assembly blueprints, or stock availability.",
                style = MaterialTheme.typography.bodySmall,
                color = SandStone,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Bottom Test Simulation Barcode triggers & Manual Entry
        Surface(
            color = CharcoalTeak.copy(alpha = 0.95f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quick Test: Tap Showroom Floor Tag",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrassGold,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sampleItems = viewModel.repository.catalogProducts.take(3)
                    sampleItems.forEach { prod ->
                        Surface(
                            color = WalnutBrown,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    scannedResultProduct = prod
                                }
                        ) {
                            Text(
                                text = prod.name.split(" ").take(2).joinToString(" "),
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmWhite,
                                modifier = Modifier.padding(8.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = manualCodeInput,
                        onValueChange = { manualCodeInput = it },
                        placeholder = { Text("Or enter Tag ID: e.g. WCI-SOFA-01") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WarmWhite,
                            unfocusedTextColor = WarmWhite,
                            focusedBorderColor = BrassGold,
                            unfocusedBorderColor = SandStone
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val found = viewModel.repository.catalogProducts.find {
                                it.id.equals(manualCodeInput.trim(), ignoreCase = true)
                            }
                            if (found != null) {
                                scannedResultProduct = found
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrassGold)
                    ) {
                        Text("Search", color = CharcoalTeak, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Scanned product dialog
        if (scannedResultProduct != null) {
            val prod = scannedResultProduct!!
            AlertDialog(
                onDismissRequest = { scannedResultProduct = null },
                title = {
                    Text(prod.name, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text("Dimensions: ${prod.dimensions}")
                        Text("Primary Material: ${prod.material}")
                        Text("Showroom Floor Stock: 4 units available for immediate dispatch", color = ForestEco, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { showExplodedManual = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open 3D Exploded Assembly Guide")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val id = prod.id
                            scannedResultProduct = null
                            onProductFound(id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown)
                    ) {
                        Text("Inspect Specifications")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { scannedResultProduct = null }) { Text("Close") }
                }
            )
        }

        if (showExplodedManual) {
            AlertDialog(
                onDismissRequest = { showExplodedManual = false },
                title = { Text("In-Store QR Manual Access") },
                text = {
                    Column {
                        Text("Digital Manual verified via showroom QR code.", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• Assembly blueprint PDF downloaded\n• Exploded hardware view unlocked\n• Lifetime care card registered to your account")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showExplodedManual = false },
                        colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown)
                    ) {
                        Text("Got It")
                    }
                }
            )
        }
    }
}
