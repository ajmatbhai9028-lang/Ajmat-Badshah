package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ProductCategory
import com.example.ui.components.formatPrice
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradePortalScreen(
    viewModel: WciViewModel,
    onProductClick: (String) -> Unit,
    onNavigateToSellerStudio: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Trade Pro B2B, 1: Artisan Marketplace, 2: Price Match & Pre-Orders

    var businessName by remember { mutableStateOf("Studio Aalto Architecture & Interiors") }
    var resaleTaxId by remember { mutableStateOf("US-CA-981042-REV") }
    var bulkQuantityEstimate by remember { mutableFloatStateOf(12f) }
    var rfqSubmitted by remember { mutableStateOf(false) }

    var competitorUrl by remember { mutableStateOf("") }
    var competitorPrice by remember { mutableStateOf("") }
    var priceMatchSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("trade_portal_screen")
    ) {
        // Tab Header
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = WalnutBrown
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Trade Pro (B2B)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Local Artisan Hub", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Price Match / Pre-Order", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Trade Pro B2B Portal
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = WalnutBrown),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Surface(
                                    color = BrassGold,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "VERIFIED TRADE PROFESSIONAL",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalTeak,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Architect & Interior Designer Program",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmWhite
                                    )
                                )
                                Text(
                                    text = "Enjoy 25% to 35% commercial volume discounts, tax-exempt purchasing, and dedicated white-glove project managers.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SandStone
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Bulk Order RFQ & Volume Bidding", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = businessName,
                                    onValueChange = { businessName = it },
                                    label = { Text("Design Firm / Enterprise Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = resaleTaxId,
                                    onValueChange = { resaleTaxId = it },
                                    label = { Text("Resale / Tax-Exempt Certificate Number") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Project Volume Estimate: ${bulkQuantityEstimate.toInt()} Units")
                                Slider(
                                    value = bulkQuantityEstimate,
                                    onValueChange = { bulkQuantityEstimate = it },
                                    valueRange = 5f..50f,
                                    colors = SliderDefaults.colors(thumbColor = WalnutBrown, activeTrackColor = BrassGold)
                                )
                                val discountRate = if (bulkQuantityEstimate >= 25) 35 else 25
                                Surface(
                                    color = ForestEco.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Qualifies for Tier ${if (discountRate == 35) "2 (35% Off)" else "1 (25% Off)"} Commercial Pricing",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ForestEco,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { rfqSubmitted = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Submit Commercial RFQ Proposal")
                                }
                                if (rfqSubmitted) {
                                    Text(
                                        text = "✓ RFQ submitted! Your assigned Trade Concierge will email formal line-item pricing within 4 hours.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ForestEco,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Local Artisan Marketplace Hub
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SandStone),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Local Artisan Marketplace Hub",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = WalnutBrown
                                    )
                                )
                                Text(
                                    text = "Celebrating independent North American and Scandinavian craftsmen. Every piece is numbered, signed, and hand-built with local old-growth sustainable hardwoods.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LightWalnut
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onNavigateToSellerStudio,
                                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("trade_launch_seller_studio_button")
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = BrassGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("List Furniture as an Artisan Seller (AI Guide)")
                                }
                            }
                        }
                    }

                    items(viewModel.repository.catalogProducts.filter { it.isArtisanHandcrafted }) { artisanProd ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProductClick(artisanProd.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.hero_showroom),
                                    contentDescription = artisanProd.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(artisanProd.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text("Master Craftsman Henrik Lind", style = MaterialTheme.typography.labelSmall, color = BrassGold)
                                    Text(
                                        formatPrice(artisanProd.price, uiState.selectedCurrency, uiState.currencyMultiplier),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Price Matching Guarantee & Pre-Orders
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Price Matching Guarantee Tool", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "Found an identical designer timber or boucle piece lower elsewhere? Submit link for an immediate price match plus extra 5% credit.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = competitorUrl,
                                    onValueChange = { competitorUrl = it },
                                    label = { Text("Competitor Product URL") },
                                    placeholder = { Text("https://...") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = competitorPrice,
                                    onValueChange = { competitorPrice = it },
                                    label = { Text("Listed Price ($)") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { priceMatchSubmitted = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Submit Price Match Claim")
                                }
                                if (priceMatchSubmitted) {
                                    Text(
                                        text = "✓ Claim received! Automatic verification completed. Adjustment code generated.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ForestEco,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Spring 2027 Pre-Order Collection", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "Reserve unreleased architectural lines with refundable 10% deposit. Top-tier loyalty members receive VIP first-batch dispatch.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = WarmWhite,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Sylvan Floating Walnut Credenza (2027 Line)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text("Ships February 2027 • Only 20 Pieces Worldwide", style = MaterialTheme.typography.labelSmall, color = BrassGold)
                                        }
                                        Button(
                                            onClick = { /* Pre-order */ },
                                            colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Reserve")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
