package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Product
import com.example.ui.components.formatPrice
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowroomTourScreen(
    viewModel: WciViewModel,
    onNavigateBack: () -> Unit,
    onProductClick: (String) -> Unit
) {
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var selectedHotspotProduct by remember { mutableStateOf<Product?>(null) }
    var showBookAssistant by remember { mutableStateOf(false) }

    val products = viewModel.repository.catalogProducts

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalTeak)
            .testTag("showroom_tour_screen")
    ) {
        // 360° Panning Panorama Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        panOffsetX += dragAmount.x * 0.8f
                    }
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.hero_showroom),
                contentDescription = "Showroom 360 Panorama",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = panOffsetX % 400
                    }
            )

            // Interactive Hotspot Pins on furniture pieces in the showroom
            // Hotspot 1: Sofa
            val sofa = products.find { it.id == "WCI-SOFA-01" }
            if (sofa != null) {
                Surface(
                    shape = CircleShape,
                    color = BrassGold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-40).dp, y = 30.dp)
                        .size(36.dp)
                        .clickable { selectedHotspotProduct = sofa }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.TouchApp, contentDescription = null, tint = CharcoalTeak, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Hotspot 2: Lamp
            val lamp = products.find { it.id == "WCI-LAMP-04" }
            if (lamp != null) {
                Surface(
                    shape = CircleShape,
                    color = BrassGold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 110.dp, y = (-20).dp)
                        .size(36.dp)
                        .clickable { selectedHotspotProduct = lamp }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.TouchApp, contentDescription = null, tint = CharcoalTeak, modifier = Modifier.size(20.dp))
                    }
                }
            }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.RotateRight, contentDescription = null, tint = BrassGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WCI Flagship Atelier (Drag to Pan 360°)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = WarmWhite
                    )
                }
            }
        }

        // Bottom CTA strip
        Surface(
            color = CharcoalTeak.copy(alpha = 0.95f),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Interactive Virtual Showroom", fontWeight = FontWeight.Bold, color = WarmWhite, style = MaterialTheme.typography.titleSmall)
                    Text("Tap pulsating golden pins to inspect pieces", color = SandStone, style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = { showBookAssistant = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BrassGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Live Concierge", color = CharcoalTeak, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Hotspot Product Inspection Sheet
        if (selectedHotspotProduct != null) {
            val prod = selectedHotspotProduct!!
            Card(
                colors = CardDefaults.cardColors(containerColor = WarmWhite),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 80.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(prod.subtitle, style = MaterialTheme.typography.bodySmall, color = LightWalnut)
                        }
                        IconButton(onClick = { selectedHotspotProduct = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatPrice(prod.price, "USD ($)"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WalnutBrown
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val id = prod.id
                            selectedHotspotProduct = null
                            onProductClick(id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Full Specifications & AR")
                    }
                }
            }
        }
    }

    if (showBookAssistant) {
        AlertDialog(
            onDismissRequest = { showBookAssistant = false },
            title = { Text("Connect with Showroom Associate") },
            text = {
                Text("Start a live 1-on-1 audio/video call with our gallery specialist right now to walk around any display and ask questions.")
            },
            confirmButton = {
                Button(
                    onClick = { showBookAssistant = false },
                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown)
                ) {
                    Text("Start Live Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookAssistant = false }) { Text("Cancel") }
            }
        )
    }
}
