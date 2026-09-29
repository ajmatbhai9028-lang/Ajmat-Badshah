package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Product
import com.example.data.model.SwatchOption
import com.example.ui.components.EcoBadge
import com.example.ui.components.formatPrice
import com.example.ui.components.getDrawableResForProduct
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: WciViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAr: (String) -> Unit,
    onNavigateToCart: () -> Unit
) {
    val product = remember(productId) { viewModel.getProductOrCustom(productId) }
    val uiState by viewModel.uiState.collectAsState()

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found")
        }
        return
    }

    var selectedSwatch by remember { mutableStateOf(product.availableSwatches.first()) }
    var assemblyBooked by remember { mutableStateOf(true) }
    var insuranceBooked by remember { mutableStateOf(true) }
    var is360Mode by remember { mutableStateOf(false) }
    var rotationAngle360 by remember { mutableFloatStateOf(0f) }
    var isVideoPlaying by remember { mutableStateOf(false) }
    var showEmiCalculator by remember { mutableStateOf(false) }
    var emiTenureMonths by remember { mutableFloatStateOf(12f) }
    var swatchOrderSuccess by remember { mutableStateOf(false) }
    var addedToCartSnackbar by remember { mutableStateOf(false) }

    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val isWishlisted = remember(wishlistItems, product.id) {
        wishlistItems.any { it.productId == product.id }
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Total Investment",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val finalPrice = product.price + (if (assemblyBooked) 89.0 else 0.0) + (if (insuranceBooked) 35.0 else 0.0)
                        Text(
                            text = formatPrice(finalPrice, uiState.selectedCurrency, uiState.currencyMultiplier),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.addToCart(product, selectedSwatch, assemblyBooked)
                            addedToCartSnackbar = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("add_to_cart_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = BrassGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Atelier Cart", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .testTag("product_detail_screen"),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Interactive 360° / Photo Gallery / Video Demo Showcase
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .background(SandStone.copy(alpha = 0.5f))
                ) {
                    if (isVideoPlaying) {
                        // In-app Video Demonstration Simulation
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CharcoalTeak),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = getDrawableResForProduct(product.imageResName)),
                                contentDescription = "Video preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer { alpha = 0.35f }
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = CircleShape,
                                    color = BrassGold,
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Playing",
                                        tint = CharcoalTeak,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Atelier Master Demonstration (4K)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = WarmWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Showing foam resiliency & joinery stress-tests",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SandStone
                                )
                            }
                        }
                    } else {
                        // Interactive 360° / Standard Gallery View
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(is360Mode) {
                                    if (is360Mode) {
                                        detectDragGestures { _, dragAmount ->
                                            rotationAngle360 = (rotationAngle360 + dragAmount.x * 0.5f) % 360f
                                        }
                                    }
                                }
                        ) {
                            Image(
                                painter = painterResource(id = getDrawableResForProduct(product.imageResName)),
                                contentDescription = product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        if (is360Mode) {
                                            rotationY = rotationAngle360
                                            cameraDistance = 12 * density
                                        }
                                    }
                            )

                            if (is360Mode) {
                                Surface(
                                    color = CharcoalTeak.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RotateRight,
                                            contentDescription = null,
                                            tint = BrassGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Drag left/right to rotate 360° (${rotationAngle360.toInt()}°)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = WarmWhite
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Top Bar controls over image
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .background(WarmWhite.copy(alpha = 0.9f), CircleShape)
                                .size(40.dp)
                                .testTag("detail_back_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WalnutBrown)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { viewModel.toggleWishlist(product.id) },
                                modifier = Modifier
                                    .background(WarmWhite.copy(alpha = 0.9f), CircleShape)
                                    .size(40.dp)
                                    .testTag("detail_wishlist_button")
                            ) {
                                Icon(
                                    imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Wishlist",
                                    tint = if (isWishlisted) MutedTerracotta else WalnutBrown
                                )
                            }
                            IconButton(
                                onClick = onNavigateToCart,
                                modifier = Modifier
                                    .background(WarmWhite.copy(alpha = 0.9f), CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(Icons.Outlined.ShoppingBag, contentDescription = "Cart", tint = WalnutBrown)
                            }
                        }
                    }

                    // View Mode Switcher Pills (Photo | 360° | Video)
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = !is360Mode && !isVideoPlaying,
                            onClick = { is360Mode = false; isVideoPlaying = false },
                            label = { Text("Photo") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WalnutBrown,
                                selectedLabelColor = WarmWhite,
                                containerColor = WarmWhite.copy(alpha = 0.85f)
                            )
                        )
                        FilterChip(
                            selected = is360Mode,
                            onClick = { is360Mode = true; isVideoPlaying = false },
                            label = { Text("360° View") },
                            leadingIcon = { Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WalnutBrown,
                                selectedLabelColor = WarmWhite,
                                containerColor = WarmWhite.copy(alpha = 0.85f)
                            ),
                            modifier = Modifier.testTag("toggle_360_view")
                        )
                        FilterChip(
                            selected = isVideoPlaying,
                            onClick = { isVideoPlaying = !isVideoPlaying; is360Mode = false },
                            label = { Text("Video Demo") },
                            leadingIcon = { Icon(Icons.Default.PlayCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WalnutBrown,
                                selectedLabelColor = WarmWhite,
                                containerColor = WarmWhite.copy(alpha = 0.85f)
                            ),
                            modifier = Modifier.testTag("toggle_video_demo")
                        )
                    }
                }
            }

            // 2. Title, Subtitle, Rating & AR Action Button
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = product.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = BrassGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.rating} (Verified ${product.reviewCount} Reviews)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        EcoBadge(product.sustainabilityScore, product.carbonOffsetKg)
                    }

                    // PRICING SECTION (Strike-Through Original, Discounted Price, Discount Badge & Stock Status)
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SandStone.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, BrassGold.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("product_pricing_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = formatPrice(product.price, uiState.selectedCurrency, uiState.currencyMultiplier),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (product.originalPrice > product.price) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = formatPrice(product.originalPrice, uiState.selectedCurrency, uiState.currencyMultiplier),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.outline,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val discountPct = (((product.originalPrice - product.price) / product.originalPrice) * 100).toInt()
                                        Surface(
                                            color = BrassGold,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "$discountPct% OFF",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalTeak,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                // Stock availability badge
                                Surface(
                                    color = if (product.stockCount > 5) Color(0xFF2E6930).copy(alpha = 0.15f) else Color(0xFFD89D6A).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (product.stockCount > 0) "${product.stockCount} in Stock" else "Made to Order",
                                        color = if (product.stockCount > 5) Color(0xFF2E6930) else WalnutBrown,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            if (product.originalPrice > product.price) {
                                val savings = product.originalPrice - product.price
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Atelier Savings: ${formatPrice(savings, uiState.selectedCurrency, uiState.currencyMultiplier)} off retail price",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E6930),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // AR View in Room CTA Button
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { onNavigateToAr(product.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("view_in_ar_cta")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = WalnutBrown,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View in Your Room (Real-Scale AR)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WalnutBrown
                        )
                    }
                }
            }

            // 3. Material & Fabric Swatches Selection + Physical Swatch Home Delivery
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Finish & Upholstery: ${selectedSwatch.name}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedSwatch.materialType,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        product.availableSwatches.forEach { swatch ->
                            val isSelected = selectedSwatch.name == swatch.name
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(swatch.hexColor))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) WalnutBrown else Color(0x33000000),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedSwatch = swatch },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (swatch.hexColor > 0xFF777777) CharcoalTeak else WarmWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    // Order Physical Swatches Action
                    OutlinedButton(
                        onClick = { swatchOrderSuccess = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = null,
                            tint = BrassGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Order Free Fabric Swatch Box Delivered Home")
                    }
                    if (swatchOrderSuccess) {
                        Text(
                            text = "✓ Sample Swatch Kit ordered to your address. Ships tomorrow.",
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestEco,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // 4. Financing & EMI Calculator Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = BrassGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Financing & 0% EMI Calculator",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            TextButton(onClick = { showEmiCalculator = !showEmiCalculator }) {
                                Text(if (showEmiCalculator) "Hide" else "Calculate", color = BrassGold)
                            }
                        }

                        val monthlyEmi = (product.price / emiTenureMonths.toInt())
                        Text(
                            text = "Starting at ${formatPrice(monthlyEmi, uiState.selectedCurrency, uiState.currencyMultiplier)}/mo for ${emiTenureMonths.toInt()} months",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        AnimatedVisibility(visible = showEmiCalculator) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Text(
                                    text = "Tenure: ${emiTenureMonths.toInt()} Months",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Slider(
                                    value = emiTenureMonths,
                                    onValueChange = { emiTenureMonths = it },
                                    valueRange = 3f..24f,
                                    steps = 6,
                                    colors = SliderDefaults.colors(
                                        thumbColor = WalnutBrown,
                                        activeTrackColor = BrassGold
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("3 Months (0% APR)", style = MaterialTheme.typography.labelSmall)
                                    Text("12 Months", style = MaterialTheme.typography.labelSmall)
                                    Text("24 Months", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            // 5. Checkout Add-on Services (Assembly & Insurance)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "White-Glove Services & Protection",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Assembly checkbox
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { assemblyBooked = !assemblyBooked },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = assemblyBooked,
                                onCheckedChange = { assemblyBooked = it },
                                colors = CheckboxDefaults.colors(checkedColor = WalnutBrown)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Professional Room Assembly (+ $89)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Certified technicians unpack, level, and remove all packaging",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        // Transit Insurance checkbox
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { insuranceBooked = !insuranceBooked },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = insuranceBooked,
                                onCheckedChange = { insuranceBooked = it },
                                colors = CheckboxDefaults.colors(checkedColor = WalnutBrown)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "5-Year Transit & Scratch Insurance (+ $35)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Zero-deductible coverage for accidental drops, stains, or damage",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 6. Comprehensive Dimensions & Specifications Sheet
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Architectural Dimensions & Specs",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Dimensions:", fontWeight = FontWeight.SemiBold)
                                Text(product.dimensions, color = MaterialTheme.colorScheme.primary)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Weight:", fontWeight = FontWeight.SemiBold)
                                Text("${product.weightKg} kg (${(product.weightKg * 2.2f).toInt()} lbs)")
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            product.specifications.forEach { (specKey, specVal) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$specKey:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = specVal,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1.4f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. Verified Reviews & Q&A
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Verified Atelier Reviews",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    viewModel.repository.verifiedReviews.forEach { review ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(review.author, fontWeight = FontWeight.Bold)
                                    Text(review.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }
                                Row {
                                    repeat(review.rating) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = BrassGold, modifier = Modifier.size(14.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Verified Buyer", style = MaterialTheme.typography.labelSmall, color = ForestEco)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(review.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Text(review.comment, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
