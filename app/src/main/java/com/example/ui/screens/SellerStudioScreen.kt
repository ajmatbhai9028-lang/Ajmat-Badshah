package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.components.formatPrice
import com.example.ui.components.getDrawableResForProduct
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerStudioScreen(
    viewModel: WciViewModel,
    onNavigateBack: () -> Unit,
    onViewProductInStorefront: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sellerListings by viewModel.sellerListings.collectAsState()
    val draft by viewModel.activeListingDraft.collectAsState()
    val currentStep by viewModel.sellerWizardStep.collectAsState()

    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: AI Listing Assistant, 1: Inventory & Management
    var showPublishedDialog by remember { mutableStateOf(false) }
    var publishedProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Atelier Seller Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = if (selectedMainTab == 0) "AI Furniture Listing Assistant" else "Stock & Listing Management",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("seller_studio_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        // Quick switch tabs
                        IconButton(
                            onClick = { selectedMainTab = if (selectedMainTab == 0) 1 else 0 },
                            modifier = Modifier.testTag("seller_studio_toggle_tab")
                        ) {
                            Icon(
                                imageVector = if (selectedMainTab == 0) Icons.Outlined.Inventory2 else Icons.Outlined.AddCircleOutline,
                                contentDescription = if (selectedMainTab == 0) "View Inventory" else "New Listing"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )

                // Main navigation tabs
                TabRow(
                    selectedTabIndex = selectedMainTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = WalnutBrown
                ) {
                    Tab(
                        selected = selectedMainTab == 0,
                        onClick = { selectedMainTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Listing Assistant", fontWeight = if (selectedMainTab == 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_ai_listing_assistant")
                    )
                    Tab(
                        selected = selectedMainTab == 1,
                        onClick = { selectedMainTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Inventory (${sellerListings.size})", fontWeight = if (selectedMainTab == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_seller_inventory")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (selectedMainTab == 0) {
                // AI Listing Wizard
                SellerListingWizardContent(
                    viewModel = viewModel,
                    draft = draft,
                    currentStep = currentStep,
                    currency = uiState.selectedCurrency,
                    currencyMultiplier = uiState.currencyMultiplier,
                    onStepChanged = { step -> viewModel.setSellerWizardStep(step) },
                    onPublishClicked = {
                        val product = viewModel.publishProductListing(draft)
                        publishedProduct = product
                        showPublishedDialog = true
                    }
                )
            } else {
                // Seller Inventory Management
                SellerInventoryManagementContent(
                    viewModel = viewModel,
                    listings = sellerListings,
                    currency = uiState.selectedCurrency,
                    currencyMultiplier = uiState.currencyMultiplier,
                    onStartNewListing = {
                        viewModel.resetListingDraft()
                        selectedMainTab = 0
                    },
                    onViewInStorefront = onViewProductInStorefront
                )
            }
        }
    }

    // Celebratory Published Dialog
    if (showPublishedDialog && publishedProduct != null) {
        val prod = publishedProduct!!
        AlertDialog(
            onDismissRequest = { showPublishedDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF2E6930),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = WarmWhite, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Product Published!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Your furniture piece is now live in the WCI Atelier Storefront and available for customer orders.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(prod.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text(prod.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = formatPrice(prod.price, uiState.selectedCurrency, uiState.currencyMultiplier),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (prod.originalPrice > prod.price) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = formatPrice(prod.originalPrice, uiState.selectedCurrency, uiState.currencyMultiplier),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = BrassGold,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            val pct = (((prod.originalPrice - prod.price) / prod.originalPrice) * 100).toInt()
                                            Text(
                                                text = "$pct% OFF",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalTeak,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    color = Color(0xFF2E6930).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "PUBLISHED • ${prod.stockCount} in Stock",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF2E6930),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPublishedDialog = false
                        onViewProductInStorefront(prod.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                    modifier = Modifier.testTag("dialog_view_in_storefront_button")
                ) {
                    Text("View in Storefront")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPublishedDialog = false
                        selectedMainTab = 1 // switch to inventory management
                    },
                    modifier = Modifier.testTag("dialog_manage_inventory_button")
                ) {
                    Text("Manage in Inventory")
                }
            }
        )
    }
}

@Composable
fun SellerListingWizardContent(
    viewModel: WciViewModel,
    draft: SellerProductListing,
    currentStep: Int,
    currency: String,
    currencyMultiplier: Double,
    onStepChanged: (Int) -> Unit,
    onPublishClicked: () -> Unit
) {
    val steps = listOf(
        "1. Details" to Icons.Default.Description,
        "2. Photos" to Icons.Default.PhotoCamera,
        "3. 360° & Video" to Icons.Default.RotateRight,
        "4. Pricing" to Icons.Default.Sell,
        "5. Stock & Publish" to Icons.Default.Publish
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("seller_listing_wizard"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Step Progress Bar Indicator
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Step ${currentStep + 1} of 5: ${steps[currentStep].first.substringAfter(". ")}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WalnutBrown
                        )
                        Text(
                            text = "${((currentStep + 1) * 20)}% Complete",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrassGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (currentStep + 1) / 5f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = BrassGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    // Step chips navigation
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(steps.indices.toList()) { index ->
                            val isSelected = index == currentStep
                            val isCompleted = index < currentStep
                            FilterChip(
                                selected = isSelected,
                                onClick = { onStepChanged(index) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isCompleted) Icons.Default.CheckCircle else steps[index].second,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (isCompleted) Color(0xFF2E6930) else if (isSelected) WalnutBrown else Color.Gray
                                    )
                                },
                                label = {
                                    Text(
                                        text = steps[index].first,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrassGold.copy(alpha = 0.25f),
                                    selectedLabelColor = WalnutBrown
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. AI Assistant Context Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = WalnutBrown),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BrassGold,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CharcoalTeak, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Listing Guide",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrassGold
                        )
                        Text(
                            text = when (currentStep) {
                                0 -> "Let's capture essential dimensions, materials, and categories for architectural buyers."
                                1 -> "Upload real photos directly from your device. Front, back, and detail views are verified for authenticity."
                                2 -> "Showcase 360° rotation and interactive video walkthroughs to increase buyer conversion."
                                3 -> "Set original and discounted prices. A clear discount badge and strike-through original price boosts sales."
                                else -> "Manage stock availability, track SKUs, and set product status to Published."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmWhite
                        )
                    }
                }
            }
        }

        // 3. Step Content Switcher
        item {
            when (currentStep) {
                0 -> Step1ProductDetails(viewModel, draft)
                1 -> Step2MultiAnglePhotos(viewModel, draft)
                2 -> Step3Interactive360AndVideo(viewModel, draft)
                3 -> Step4PricingAndDiscounts(viewModel, draft, currency, currencyMultiplier)
                4 -> Step5StockAndPublish(viewModel, draft, currency, currencyMultiplier, onPublishClicked)
            }
        }

        // 4. Navigation Buttons (Back / Next)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { onStepChanged(currentStep - 1) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("wizard_prev_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Previous")
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (currentStep < 4) {
                    Button(
                        onClick = { onStepChanged(currentStep + 1) },
                        colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("wizard_next_button")
                    ) {
                        Text("Next Step")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// STEP 1: BASIC PRODUCT DETAILS
// -------------------------------------------------------------------------------------------------
@Composable
fun Step1ProductDetails(viewModel: WciViewModel, draft: SellerProductListing) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Basic Product Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { viewModel.applySampleListingPreset("credenza") },
                    modifier = Modifier.testTag("quick_fill_credenza_preset")
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto-Fill Sample", style = MaterialTheme.typography.labelSmall)
                }
            }

            // 1. Product Title
            OutlinedTextField(
                value = draft.title,
                onValueChange = { newTitle -> viewModel.updateListingDraft { it.copy(title = newTitle) } },
                label = { Text("Product Title *") },
                placeholder = { Text("e.g., Kyoto Architectural Oak Credenza") },
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.generateAiListingCopy(draft.category, draft.material.ifBlank { "Solid White Oak" }, draft.color.ifBlank { "Natural Finish" }) },
                        modifier = Modifier.testTag("ai_enhance_title_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Suggest Title", tint = BrassGold)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seller_input_title"),
                shape = RoundedCornerShape(12.dp)
            )

            // 2. Category Selection
            Text(
                text = "Furniture Category *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ProductCategory.values()) { category ->
                    val isSelected = draft.category == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateListingDraft { it.copy(category = category) } },
                        label = { Text(category.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WalnutBrown,
                            selectedLabelColor = WarmWhite
                        ),
                        modifier = Modifier.testTag("category_chip_${category.name.lowercase()}")
                    )
                }
            }

            // 3. Material
            OutlinedTextField(
                value = draft.material,
                onValueChange = { newMat -> viewModel.updateListingDraft { it.copy(material = newMat) } },
                label = { Text("Primary Material & Construction *") },
                placeholder = { Text("e.g., Kiln-Dried White Oak, Solid Walnut, Aniline Leather") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seller_input_material"),
                shape = RoundedCornerShape(12.dp)
            )

            // Quick Material Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Solid Walnut", "White Oak", "Aniline Leather", "Honed Travertine", "Boucle Fabric").forEach { mat ->
                    SuggestionChip(
                        onClick = { viewModel.updateListingDraft { it.copy(material = mat) } },
                        label = { Text(mat, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            // 4. Dimensions
            OutlinedTextField(
                value = draft.dimensions,
                onValueChange = { newDim -> viewModel.updateListingDraft { it.copy(dimensions = newDim) } },
                label = { Text("Dimensions (W x D x H) *") },
                placeholder = { Text("e.g., 68\"W x 20\"D x 32\"H (173 x 51 x 81 cm)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seller_input_dimensions"),
                shape = RoundedCornerShape(12.dp)
            )

            // Dimension Sliders for precision
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Width: ${draft.widthCm.toInt()} cm", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = draft.widthCm,
                        onValueChange = { w -> viewModel.updateListingDraft { it.copy(widthCm = w) } },
                        valueRange = 40f..300f
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Depth: ${draft.depthCm.toInt()} cm", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = draft.depthCm,
                        onValueChange = { d -> viewModel.updateListingDraft { it.copy(depthCm = d) } },
                        valueRange = 30f..150f
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Height: ${draft.heightCm.toInt()} cm", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = draft.heightCm,
                        onValueChange = { h -> viewModel.updateListingDraft { it.copy(heightCm = h) } },
                        valueRange = 30f..220f
                    )
                }
            }

            // 5. Color / Finish
            OutlinedTextField(
                value = draft.color,
                onValueChange = { newCol -> viewModel.updateListingDraft { it.copy(color = newCol) } },
                label = { Text("Color & Finish *") },
                placeholder = { Text("e.g., Bleached Natural Oak, Cognac, Roman Ivory") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seller_input_color"),
                shape = RoundedCornerShape(12.dp)
            )

            // 6. Description
            OutlinedTextField(
                value = draft.description,
                onValueChange = { newDesc -> viewModel.updateListingDraft { it.copy(description = newDesc) } },
                label = { Text("Architectural Description & Joinery") },
                placeholder = { Text("Describe joinery, finish, tactile feel, and artisanal care...") },
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seller_input_description"),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// STEP 2: MULTI-ANGLE PHOTOS DIRECTLY FROM DEVICE
// -------------------------------------------------------------------------------------------------
@Composable
fun Step2MultiAnglePhotos(viewModel: WciViewModel, draft: SellerProductListing) {
    val photos = draft.multiAnglePhotos

    // Android Photo Pickers for each angle slot
    val frontPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.updateListingDraft { it.copy(multiAnglePhotos = it.multiAnglePhotos.copy(frontUri = uri.toString())) }
        }
    }
    val backPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.updateListingDraft { it.copy(multiAnglePhotos = it.multiAnglePhotos.copy(backUri = uri.toString())) }
        }
    }
    val detailPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.updateListingDraft { it.copy(multiAnglePhotos = it.multiAnglePhotos.copy(detailUri = uri.toString())) }
        }
    }
    val sidePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.updateListingDraft { it.copy(multiAnglePhotos = it.multiAnglePhotos.copy(sideUri = uri.toString())) }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Multi-Angle Photo Upload",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Upload high-res photos directly from your device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = if (photos.hasRequiredAngles) Color(0xFF2E6930).copy(alpha = 0.15f) else BrassGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (photos.hasRequiredAngles) "All Required Angles ✓" else "${photos.totalUploaded}/3 Required",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (photos.hasRequiredAngles) Color(0xFF2E6930) else WalnutBrown,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Quick load preset demo photos
            OutlinedButton(
                onClick = {
                    viewModel.updateListingDraft {
                        it.copy(
                            sampleDrawableRes = "sofa_nordic",
                            multiAnglePhotos = MultiAnglePhotos(
                                frontUri = "preset://sofa_nordic",
                                backUri = "preset://sofa_nordic_back",
                                detailUri = "preset://sofa_nordic_detail",
                                sideUri = "preset://sofa_nordic_side"
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("load_preset_photos_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Load Atelier High-Res Studio Photos (Demo Preset)")
            }

            // Angle 1: Front View (Required)
            PhotoSlotCard(
                title = "Front View *",
                description = "Direct eye-level frontal perspective showing complete silhouette.",
                uri = photos.frontUri,
                fallbackDrawable = draft.sampleDrawableRes,
                testTag = "upload_front_photo_button",
                onSelectClick = { frontPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onClearClick = { viewModel.updateListingDraft { it.copy(multiAnglePhotos = it.multiAnglePhotos.copy(frontUri = null)) } }
            )

            // Angle 2: Back View (Required)
            PhotoSlotCard(
                title = "Back View *",
                description = "Rear perspective highlighting joinery, finish, and reverse aesthetic.",
                uri = photos.backUri,
                fallbackDrawable = draft.sampleDrawableRes,
                testTag = "upload_back_photo_button",
                onSelectClick = { backPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onClearClick = { viewModel.updateListingDraft { it.copy(multiAnglePhotos = it.multiAnglePhotos.copy(backUri = null)) } }
            )

            // Angle 3: Detail / Joinery View (Required)
            PhotoSlotCard(
                title = "Detail & Joinery View *",
                description = "Macro close-up of mortise joinery, grain, fabric texture, or hardware.",
                uri = photos.detailUri,
                fallbackDrawable = draft.sampleDrawableRes,
                testTag = "upload_detail_photo_button",
                onSelectClick = { detailPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onClearClick = { viewModel.updateListingDraft { it.copy(multiAnglePhotos = it.multiAnglePhotos.copy(detailUri = null)) } }
            )

            // Angle 4: Side / Architectural Angle (Optional)
            PhotoSlotCard(
                title = "Side Profile (Optional)",
                description = "Side profile illustrating depth, recline angle, and floating clearance.",
                uri = photos.sideUri,
                fallbackDrawable = draft.sampleDrawableRes,
                testTag = "upload_side_photo_button",
                onSelectClick = { sidePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onClearClick = { viewModel.updateListingDraft { it.copy(multiAnglePhotos = it.multiAnglePhotos.copy(sideUri = null)) } }
            )
        }
    }
}

@Composable
fun PhotoSlotCard(
    title: String,
    description: String,
    uri: String?,
    fallbackDrawable: String,
    testTag: String,
    onSelectClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (uri != null) SandStone.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (uri != null) BrassGold else Color(0x22000000))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Preview thumbnail
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                if (uri != null) {
                    if (uri.startsWith("preset://")) {
                        Image(
                            painter = painterResource(id = getDrawableResForProduct(fallbackDrawable)),
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = uri,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Surface(
                        color = Color(0xFF2E6930),
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(18.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = WarmWhite, modifier = Modifier.size(12.dp))
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(28.dp))
                        Text("Add", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onSelectClick,
                        colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag(testTag)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (uri == null) "Select from Device" else "Change", style = MaterialTheme.typography.labelSmall)
                    }

                    if (uri != null) {
                        OutlinedButton(
                            onClick = onClearClick,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// STEP 3: 360-DEGREE INTERACTIVE MEDIA & VIDEO WALKTHROUGHS
// -------------------------------------------------------------------------------------------------
@Composable
fun Step3Interactive360AndVideo(viewModel: WciViewModel, draft: SellerProductListing) {
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: 360 Interactive, 1: Video Walkthrough
    var interactiveAngle by remember { mutableFloatStateOf(0f) }
    var isSimulatingVideo by remember { mutableStateOf(false) }
    var videoProgress by remember { mutableFloatStateOf(0.35f) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Interactive 360° Media & Video Walkthrough",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Interactive media allows interior designers and clients to inspect fine details and rotate the piece before placing orders.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Sub Tab selector
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = WalnutBrown
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text("360° Interactive Turntable") },
                    modifier = Modifier.testTag("tab_360_turntable")
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text("Video Walkthrough & Hotspots") },
                    modifier = Modifier.testTag("tab_video_walkthrough")
                )
            }

            if (activeSubTab == 0) {
                // 360 INTERACTIVE TURNTABLE
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Interactive Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CharcoalTeak)
                            .pointerInput(Unit) {
                                detectDragGestures { _, dragAmount ->
                                    interactiveAngle = (interactiveAngle + dragAmount.x * 0.6f) % 360f
                                    if (interactiveAngle < 0) interactiveAngle += 360f
                                }
                            }
                            .testTag("turntable_drag_area"),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = getDrawableResForProduct(draft.sampleDrawableRes)),
                            contentDescription = "360 rotation view",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    rotationY = interactiveAngle
                                    cameraDistance = 14 * density
                                }
                        )

                        Surface(
                            color = CharcoalTeak.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.RotateRight, contentDescription = null, tint = BrassGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Drag to rotate 360° (${interactiveAngle.toInt()}°)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmWhite
                                )
                            }
                        }
                    }

                    // Angle Slider Control
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Angle: ${interactiveAngle.toInt()}°", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Slider(
                            value = interactiveAngle,
                            onValueChange = { interactiveAngle = it },
                            valueRange = 0f..360f,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // AI 360 Quality Check Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SandStone.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2E6930), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI 360° Inspection Report", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• Lighting Uniformity: 98% (No harsh hotspots)", style = MaterialTheme.typography.bodySmall)
                                Text("Pass", color = Color(0xFF2E6930), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• Rotational Smoothness: 96% (60 FPS frames)", style = MaterialTheme.typography.bodySmall)
                                Text("Pass", color = Color(0xFF2E6930), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• Turntable Centering: 100% (Zero axis wobble)", style = MaterialTheme.typography.bodySmall)
                                Text("Optimal", color = Color(0xFF2E6930), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            } else {
                // VIDEO WALKTHROUGH & HOTSPOTS
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CharcoalTeak),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = getDrawableResForProduct(draft.sampleDrawableRes)),
                            contentDescription = "Video walkthrough",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Hotspot overlay markers
                        draft.videoWalkthrough.hotspots.forEach { spot ->
                            Surface(
                                shape = CircleShape,
                                color = BrassGold,
                                border = BorderStroke(2.dp, WarmWhite),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.LocationOn, contentDescription = spot.title, tint = CharcoalTeak, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Play / Pause Simulation
                        IconButton(
                            onClick = { isSimulatingVideo = !isSimulatingVideo },
                            modifier = Modifier
                                .size(56.dp)
                                .background(WalnutBrown.copy(alpha = 0.85f), CircleShape)
                                .testTag("video_walkthrough_play_toggle")
                        ) {
                            Icon(
                                imageVector = if (isSimulatingVideo) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = WarmWhite,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Video timeline bar at bottom
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(CharcoalTeak.copy(alpha = 0.8f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "0:${String.format("%02d", (videoProgress * draft.videoWalkthrough.durationSec).toInt())} / 0:${draft.videoWalkthrough.durationSec}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmWhite
                                )
                                Text(
                                    text = "${draft.videoWalkthrough.hotspots.size} Craftsmanship Hotspots",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrassGold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { videoProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                color = BrassGold,
                                trackColor = Color.Gray
                            )
                        }
                    }

                    // Scrubber
                    Slider(
                        value = videoProgress,
                        onValueChange = { videoProgress = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Hotspot List
                    Text("Interactive Feature Hotspots:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    draft.videoWalkthrough.hotspots.forEach { spot ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = BrassGold,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(end = 10.dp)
                                ) {
                                    Text(
                                        text = "0:${String.format("%02d", spot.timestampSec)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalTeak,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(spot.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    Text(spot.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// STEP 4: PRICING (STRIKE-THROUGH ORIGINAL, DISCOUNTED PRICE & DISCOUNT BADGE)
// -------------------------------------------------------------------------------------------------
@Composable
fun Step4PricingAndDiscounts(
    viewModel: WciViewModel,
    draft: SellerProductListing,
    currency: String,
    currencyMultiplier: Double
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pricing & Discount Configuration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Showcase original pricing with strike-through and a discount badge to highlight value to buyers.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 1. Original Price Input
            OutlinedTextField(
                value = if (draft.originalPrice > 0) draft.originalPrice.toString() else "",
                onValueChange = { input ->
                    val p = input.toDoubleOrNull() ?: 0.0
                    viewModel.updateListingDraft { it.copy(originalPrice = p) }
                },
                label = { Text("Original Manufacturer / Retail Price ($) *") },
                placeholder = { Text("e.g., 2200.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seller_input_original_price"),
                shape = RoundedCornerShape(12.dp)
            )

            // 2. Discounted Selling Price Input
            OutlinedTextField(
                value = if (draft.discountedPrice > 0) draft.discountedPrice.toString() else "",
                onValueChange = { input ->
                    val p = input.toDoubleOrNull() ?: 0.0
                    viewModel.updateListingDraft { it.copy(discountedPrice = p) }
                },
                label = { Text("Discounted Atelier Price ($) *") },
                placeholder = { Text("e.g., 1760.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Icon(Icons.Default.LocalOffer, contentDescription = null, tint = BrassGold) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seller_input_discounted_price"),
                shape = RoundedCornerShape(12.dp)
            )

            // Quick Discount Percentage Shortcuts
            Text("Quick Discount Presets:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(10, 15, 20, 25, 30).forEach { discountPct ->
                    OutlinedButton(
                        onClick = {
                            if (draft.originalPrice > 0) {
                                val discounted = draft.originalPrice * (1.0 - (discountPct / 100.0))
                                viewModel.updateListingDraft {
                                    it.copy(
                                        discountedPrice = Math.round(discounted * 100.0) / 100.0,
                                        discountPercent = discountPct,
                                        discountBadgeText = "$discountPct% OFF"
                                    )
                                }
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("$discountPct%", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // 3. LIVE PRICING PREVIEW CARD (Displays Strike-Through, Discounted Price, and Badge)
            Text(
                text = "Live Storefront Price Display Preview:",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, BrassGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "STOREFRONT PRICING BADGE PREVIEW",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrassGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Discounted price
                        Text(
                            text = formatPrice(draft.discountedPrice, currency, currencyMultiplier),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("preview_discounted_price")
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Strike-through original price
                        if (draft.originalPrice > draft.discountedPrice && draft.originalPrice > 0) {
                            Text(
                                text = formatPrice(draft.originalPrice, currency, currencyMultiplier),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline,
                                textDecoration = TextDecoration.LineThrough,
                                modifier = Modifier.testTag("preview_strikethrough_original_price")
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // Discount Badge
                            val pct = if (draft.originalPrice > 0) (((draft.originalPrice - draft.discountedPrice) / draft.originalPrice) * 100).toInt() else 0
                            Surface(
                                color = BrassGold,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("preview_discount_badge")
                            ) {
                                Text(
                                    text = "$pct% OFF",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalTeak,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (draft.originalPrice > draft.discountedPrice && draft.originalPrice > 0) {
                        val savings = draft.originalPrice - draft.discountedPrice
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Buyers save ${formatPrice(savings, currency, currencyMultiplier)} with this listing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2E6930),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// STEP 5: STOCK MANAGEMENT & PUBLISH PRODUCT
// -------------------------------------------------------------------------------------------------
@Composable
fun Step5StockAndPublish(
    viewModel: WciViewModel,
    draft: SellerProductListing,
    currency: String,
    currencyMultiplier: Double,
    onPublishClicked: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Stock Management & Publishing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage inventory units, low stock alerts, and set status to Published to make this furniture available in the catalog.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Stock count adjuster
            Card(
                colors = CardDefaults.cardColors(containerColor = SandStone.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Available Inventory Units *", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.updateListingDraft { it.copy(stockCount = (it.stockCount - 1).coerceAtLeast(0)) } },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(WalnutBrown.copy(alpha = 0.15f), CircleShape)
                                    .testTag("stepper_decrement_stock")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease Stock")
                            }

                            Text(
                                text = "${draft.stockCount} units",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .testTag("text_current_stock_count")
                            )

                            IconButton(
                                onClick = { viewModel.updateListingDraft { it.copy(stockCount = it.stockCount + 1) } },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(WalnutBrown.copy(alpha = 0.15f), CircleShape)
                                    .testTag("stepper_increment_stock")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase Stock")
                            }
                        }

                        Surface(
                            color = if (draft.stockCount > draft.lowStockThreshold) Color(0xFF2E6930).copy(alpha = 0.15f) else Color(0xFFD89D6A).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (draft.stockCount > draft.lowStockThreshold) "In Stock" else "Low Stock Alert",
                                color = if (draft.stockCount > draft.lowStockThreshold) Color(0xFF2E6930) else WalnutBrown,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // SKU & Barcode Generator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = draft.sku,
                    onValueChange = { s -> viewModel.updateListingDraft { it.copy(sku = s) } },
                    label = { Text("SKU Number") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = draft.barcode,
                    onValueChange = { b -> viewModel.updateListingDraft { it.copy(barcode = b) } },
                    label = { Text("Showroom Barcode") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Status Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Product Publication Status:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(ProductListingStatus.DRAFT, ProductListingStatus.PUBLISHED).forEach { st ->
                        val isSelected = draft.status == st
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateListingDraft { it.copy(status = st) } },
                            label = { Text(st.label) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (st == ProductListingStatus.PUBLISHED) Icons.Default.Public else Icons.Default.EditNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (st == ProductListingStatus.PUBLISHED) Color(0xFF2E6930) else WalnutBrown,
                                selectedLabelColor = WarmWhite
                            ),
                            modifier = Modifier.testTag("status_chip_${st.name.lowercase()}")
                        )
                    }
                }
            }

            // Summary Listing Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("PUBLISHING CHECKLIST", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = BrassGold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("✓ Title & Materials: ${draft.title.ifBlank { "Untitled Furniture" }} (${draft.material.ifBlank { "Unspecified" }})", style = MaterialTheme.typography.bodySmall)
                    Text("✓ Dimensions: ${draft.dimensions.ifBlank { "${draft.widthCm.toInt()} x ${draft.depthCm.toInt()} cm" }}", style = MaterialTheme.typography.bodySmall)
                    Text("✓ Multi-Angle Media: ${draft.multiAnglePhotos.totalUploaded} uploaded (Front, Back, Detail)", style = MaterialTheme.typography.bodySmall)
                    Text("✓ Pricing: Original ${formatPrice(draft.originalPrice, currency, currencyMultiplier)} → Discounted ${formatPrice(draft.discountedPrice, currency, currencyMultiplier)}", style = MaterialTheme.typography.bodySmall)
                    Text("✓ Inventory: ${draft.stockCount} units available", style = MaterialTheme.typography.bodySmall)
                }
            }

            // Big Publish Button
            Button(
                onClick = onPublishClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E6930)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("publish_product_button")
            ) {
                Icon(Icons.Default.Public, contentDescription = null, tint = WarmWhite, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Publish Furniture to Atelier (Set Status: Published)",
                    fontWeight = FontWeight.Bold,
                    color = WarmWhite,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: SELLER INVENTORY & PRODUCT MANAGEMENT
// -------------------------------------------------------------------------------------------------
@Composable
fun SellerInventoryManagementContent(
    viewModel: WciViewModel,
    listings: List<SellerProductListing>,
    currency: String,
    currencyMultiplier: Double,
    onStartNewListing: () -> Unit,
    onViewInStorefront: (String) -> Unit
) {
    var filterStatus by remember { mutableStateOf<ProductListingStatus?>(null) }

    val filteredListings = remember(listings, filterStatus) {
        if (filterStatus == null) listings else listings.filter { it.status == filterStatus }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("seller_inventory_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Quick Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Seller Inventory Hub",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${listings.size} Total Products Listed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onStartNewListing,
                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("button_create_new_listing")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Listing")
                }
            }
        }

        // Status Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterStatus == null,
                    onClick = { filterStatus = null },
                    label = { Text("All (${listings.size})") }
                )
                FilterChip(
                    selected = filterStatus == ProductListingStatus.PUBLISHED,
                    onClick = { filterStatus = ProductListingStatus.PUBLISHED },
                    label = { Text("Published (${listings.count { it.status == ProductListingStatus.PUBLISHED }})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2E6930),
                        selectedLabelColor = WarmWhite
                    )
                )
                FilterChip(
                    selected = filterStatus == ProductListingStatus.DRAFT,
                    onClick = { filterStatus = ProductListingStatus.DRAFT },
                    label = { Text("Drafts (${listings.count { it.status == ProductListingStatus.DRAFT }})") }
                )
            }
        }

        // Product Cards
        items(filteredListings) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inventory_item_${item.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Thumbnail
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SandStone),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = getDrawableResForProduct(item.sampleDrawableRes)),
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Status badge
                                Surface(
                                    color = Color(item.status.badgeColorHex).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.status.label.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(item.status.badgeColorHex),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = "SKU: ${item.sku}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${item.material} • ${item.dimensions}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // PRICING ROW (Strike-through original, discounted, and badge)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatPrice(item.discountedPrice, currency, currencyMultiplier),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (item.originalPrice > item.discountedPrice) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = formatPrice(item.originalPrice, currency, currencyMultiplier),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = BrassGold,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "${item.discountPercent}% OFF",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = CharcoalTeak,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Stock Management Row with +/- steppers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Stock: ", style = MaterialTheme.typography.labelMedium)
                            IconButton(
                                onClick = { viewModel.updateStockCount(item.id, -1) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                            }

                            Text(
                                text = "${item.stockCount}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )

                            IconButton(
                                onClick = { viewModel.updateStockCount(item.id, 1) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.toggleListingStatus(item.id) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (item.status == ProductListingStatus.PUBLISHED) "Pause" else "Publish", style = MaterialTheme.typography.labelSmall)
                            }

                            Button(
                                onClick = { onViewInStorefront(item.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Storefront", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
