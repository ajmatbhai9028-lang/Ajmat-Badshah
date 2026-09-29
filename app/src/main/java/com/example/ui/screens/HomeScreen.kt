package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.InteriorStyle
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.RoomType
import com.example.data.model.UserStyleProfile
import com.example.ui.components.EcoBadge
import com.example.ui.components.formatPrice
import com.example.ui.components.getDrawableResForProduct
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WciViewModel,
    uiState: UiState,
    onProductClick: (String) -> Unit,
    onNavigateToAr: (String) -> Unit,
    onNavigateToShowroomTour: () -> Unit,
    onNavigateToStyleQuiz: () -> Unit,
    onNavigateToMoodboard: (String) -> Unit,
    onNavigateToSellerStudio: () -> Unit = {}
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val wishlistedIds = remember(wishlistItems) { wishlistItems.map { it.productId }.toSet() }

    var showVoiceSearchDialog by remember { mutableStateOf(false) }
    var selectedStyleFilter by remember { mutableStateOf<InteriorStyle?>(null) }

    val userProfile = uiState.userStyleProfile
    val allCatalog = viewModel.repository.getAllCatalogProducts()
    val personalizedPicks = remember(userProfile, allCatalog) {
        viewModel.getPersonalizedRecommendations()
    }
    val personalizedMoodboards = remember(userProfile, viewModel.repository.moodboards) {
        viewModel.getPersonalizedMoodboards()
    }

    // Filter products
    val filteredProducts = remember(
        uiState.selectedCategory,
        uiState.selectedRoomType,
        selectedStyleFilter,
        uiState.searchQuery,
        uiState.fastShipOnly,
        allCatalog
    ) {
        allCatalog.filter { prod ->
            val matchCat = uiState.selectedCategory == null || prod.category == uiState.selectedCategory
            val matchRoom = uiState.selectedRoomType == RoomType.ALL || prod.roomType == uiState.selectedRoomType
            val matchStyle = selectedStyleFilter == null || prod.styles.contains(selectedStyleFilter)
            val matchQuery = uiState.searchQuery.isBlank() ||
                    prod.name.contains(uiState.searchQuery, ignoreCase = true) ||
                    prod.material.contains(uiState.searchQuery, ignoreCase = true) ||
                    prod.color.contains(uiState.searchQuery, ignoreCase = true)
            val matchFast = !uiState.fastShipOnly || prod.isFastShipEligible
            matchCat && matchRoom && matchStyle && matchQuery && matchFast
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_catalog_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Search Bar & Voice Input Row
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                "Search walnut, boucle, modular sofa...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("catalog_search_input"),
                        singleLine = true
                    )
                    IconButton(
                        onClick = { showVoiceSearchDialog = true },
                        modifier = Modifier.testTag("voice_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "AI Voice Search",
                            tint = BrassGold
                        )
                    }
                }
            }
        }

        // 2. Express Delivery Toggle & Currency Selector strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = uiState.fastShipOnly,
                        onCheckedChange = { viewModel.toggleFastShipOnly() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrassGold,
                            checkedTrackColor = WalnutBrown
                        ),
                        modifier = Modifier.testTag("fast_ship_switch")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Express Fast-Ship Only",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Next-Day White Glove Dispatch",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Currency Pill
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = uiState.selectedCurrency,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 3. Hero Visual Banner - Showroom Tour / Seasonal Highlights
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onNavigateToShowroomTour() }
                    .testTag("hero_showroom_banner")
            ) {
                Box(modifier = Modifier.height(210.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_showroom),
                        contentDescription = "WCI Flagship Atelier Showroom",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xCC1B1816))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = BrassGold,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "AUTUMN / WINTER ATELIER 2026",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalTeak,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Step Inside: 360° Virtual Showroom",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = WarmWhite
                            )
                        )
                        Text(
                            text = "Walk through our flagship gallery & tap items for specs",
                            style = MaterialTheme.typography.bodySmall,
                            color = SandStone
                        )
                    }
                }
            }
        }

        // Seller Studio Entry Banner (AI Listing Assistant)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WalnutBrown),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToSellerStudio() }
                    .testTag("home_seller_studio_card")
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
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = CharcoalTeak, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Sell on WCI Atelier",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = WarmWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = BrassGold,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "AI GUIDE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalTeak,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "List furniture with multi-angle photos, 360° tours, strike-through discounts & live publishing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmWhite.copy(alpha = 0.85f),
                            maxLines = 2
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = BrassGold, modifier = Modifier.size(18.dp))
                }
            }
        }

        // 4. Daily Countdown Deals Banner (Real-time dynamic timer)
        item {
            val hours = uiState.countdownSecondsRemaining / 3600
            val minutes = (uiState.countdownSecondsRemaining % 3600) / 60
            val seconds = uiState.countdownSecondsRemaining % 60
            Card(
                colors = CardDefaults.cardColors(containerColor = WalnutBrown),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Flash Deals",
                        tint = BrassGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Daily Atelier Deals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WarmWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = MutedTerracotta,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Up to 25% Off",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmWhite,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Dynamic Demand Pricing Engine • Resetting in ${String.format("%02d:%02d:%02d", hours, minutes, seconds)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SandStone
                        )
                    }
                }
            }
        }

        // 5. Room Type Filter Chips
        item {
            Text(
                text = "Curated Spaces",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                RoomType.values().forEach { room ->
                    val isSelected = uiState.selectedRoomType == room
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setRoomType(room) },
                        label = { Text(room.displayName) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WalnutBrown,
                            selectedLabelColor = WarmWhite,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }

        // 6. Curated "Get The Look" Shoppable Moodboards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shoppable Moodboards",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "1-Click Room Bundle",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrassGold,
                    fontWeight = FontWeight.Bold
                )
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(personalizedMoodboards) { look ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .width(280.dp)
                            .clickable { onNavigateToMoodboard(look.id) }
                            .testTag("moodboard_card_${look.id}")
                    ) {
                        Column {
                            Box(modifier = Modifier.height(130.dp)) {
                                Image(
                                    painter = painterResource(id = getDrawableResForProduct(look.imageResName)),
                                    contentDescription = look.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    color = WalnutBrown.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "${look.bundleDiscountPct}% Bundle Discount",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrassGold,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = look.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = look.style,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Curated by ${look.designer}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Interactive Onboarding Style Quiz / Personalized Persona
        item {
            if (userProfile != null) {
                // If user took the quiz: Show their personalized style profile & recommendations
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { onNavigateToStyleQuiz() }
                            .testTag("personalized_style_persona_card")
                    ) {
                        Box(modifier = Modifier.height(190.dp)) {
                            Image(
                                painter = painterResource(id = getDrawableResForProduct(userProfile.primaryStyle.imageResName)),
                                contentDescription = userProfile.primaryStyle.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color(0xDD1B1816))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(color = BrassGold, shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "YOUR AESTHETIC: ${userProfile.primaryStyle.title.uppercase()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = CharcoalTeak,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Surface(color = WalnutBrown.copy(alpha = 0.85f), shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "${userProfile.styleScores[userProfile.primaryStyle] ?: 94}% Match",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = WarmWhite,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = userProfile.primaryStyle.subtitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmWhite
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "Tap to review complete diagnostic & retake",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SandStone,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = BrassGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Curated for You products carousel
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tailored For You: ${userProfile.primaryStyle.title}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Based on Quiz",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrassGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(personalizedPicks.take(5)) { prod ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .width(200.dp)
                                    .clickable { onProductClick(prod.id) }
                                    .testTag("curated_pick_${prod.id}")
                            ) {
                                Column {
                                    Box(modifier = Modifier.height(115.dp)) {
                                        Image(
                                            painter = painterResource(id = getDrawableResForProduct(prod.imageResName)),
                                            contentDescription = prod.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Surface(
                                            color = WalnutBrown,
                                            shape = RoundedCornerShape(bottomEnd = 8.dp),
                                            modifier = Modifier.align(Alignment.TopStart)
                                        ) {
                                            Text(
                                                text = if (prod.styles.contains(userProfile.primaryStyle)) "Top Pick" else "Atelier Match",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BrassGold,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = prod.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = prod.material,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = formatPrice(prod.price, uiState.selectedCurrency, uiState.currencyMultiplier),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = WalnutBrown
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // If user hasn't taken the quiz: Prominent onboarding invitation
                Card(
                    colors = CardDefaults.cardColors(containerColor = SandStone),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .clickable { onNavigateToStyleQuiz() }
                        .testTag("style_quiz_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = WalnutBrown,
                            shape = CircleShape,
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = BrassGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(color = WalnutBrown.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = "60-SECOND ONBOARDING QUIZ",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = WalnutBrown,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Find Your Signature Aesthetic",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WalnutBrown
                            )
                            Text(
                                text = "Discover whether you are Modern, Bohemian, Traditional, or Industrial to unlock personalized product picks & moodboards.",
                                style = MaterialTheme.typography.bodySmall,
                                color = LightWalnut
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Start quiz",
                            tint = WalnutBrown
                        )
                    }
                }
            }
        }

        // 8. Catalog Section Header & Style Filters
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Atelier Collection (${filteredProducts.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (uiState.selectedCategory != null || selectedStyleFilter != null) {
                    TextButton(onClick = {
                        viewModel.setCategory(null)
                        selectedStyleFilter = null
                    }) {
                        Text("Clear filters", color = BrassGold)
                    }
                }
            }

            // Aesthetic Style filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                FilterChip(
                    selected = selectedStyleFilter == null,
                    onClick = { selectedStyleFilter = null },
                    label = { Text("All Aesthetics") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WalnutBrown,
                        selectedLabelColor = WarmWhite,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                InteriorStyle.values().forEach { style ->
                    val isSelected = selectedStyleFilter == style
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStyleFilter = if (isSelected) null else style },
                        label = { Text(style.title) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WalnutBrown,
                            selectedLabelColor = WarmWhite,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("style_chip_${style.name.lowercase()}")
                    )
                }
            }
        }

        // 9. Product Cards List
        items(filteredProducts) { product ->
            val isWish = wishlistedIds.contains(product.id)
            ProductCardRow(
                product = product,
                currency = uiState.selectedCurrency,
                currencyMultiplier = uiState.currencyMultiplier,
                isWishlisted = isWish,
                onCardClick = { onProductClick(product.id) },
                onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                onArClick = { onNavigateToAr(product.id) }
            )
        }
    }

    // Voice search popup dialog
    if (showVoiceSearchDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceSearchDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = BrassGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Natural Voice Search")
                }
            },
            text = {
                Column {
                    Text(
                        "Speak naturally to search dimensions, materials, or room fits. Try saying:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val suggestions = listOf(
                        "\"Find a modular oak sofa under $3,000\"",
                        "\"Show me cognac leather armchairs for living room\"",
                        "\"Dining table that fits a 4-meter room\""
                    )
                    suggestions.forEach { sample ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    val cleaned = sample.replace("\"", "").replace("Find a ", "").replace("Show me ", "")
                                    viewModel.setSearchQuery(cleaned)
                                    showVoiceSearchDialog = false
                                }
                        ) {
                            Text(
                                text = sample,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showVoiceSearchDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun ProductCardRow(
    product: Product,
    currency: String,
    currencyMultiplier: Double,
    isWishlisted: Boolean,
    onCardClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    onArClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column {
            // Product Hero Image with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = getDrawableResForProduct(product.imageResName)),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top badges row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        if (product.isFastShipEligible) {
                            Surface(
                                color = WalnutBrown,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = BrassGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "FAST-SHIP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = WarmWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        if (product.isArtisanHandcrafted) {
                            Surface(
                                color = BrassGold,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "ARTISAN HANDCRAFTED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CharcoalTeak,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Wishlist button
                    IconButton(
                        onClick = onWishlistToggle,
                        modifier = Modifier
                            .background(WarmWhite.copy(alpha = 0.85f), CircleShape)
                            .size(38.dp)
                            .testTag("wishlist_toggle_${product.id}")
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) MutedTerracotta else WalnutBrown,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // AR Trigger Button in bottom-right corner of image
                Surface(
                    color = CharcoalTeak.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clickable { onArClick() }
                        .testTag("ar_view_button_${product.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = "View in AR",
                            tint = BrassGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "View in AR",
                            style = MaterialTheme.typography.labelMedium,
                            color = WarmWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Info section
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = BrassGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${product.rating} (${product.reviewCount})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = product.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))
                EcoBadge(
                    sustainabilityScore = product.sustainabilityScore,
                    carbonOffset = product.carbonOffsetKg
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatPrice(product.price, currency, currencyMultiplier),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (product.originalPrice > product.price) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = formatPrice(product.originalPrice, currency, currencyMultiplier),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val discountPct = (((product.originalPrice - product.price) / product.originalPrice) * 100).toInt()
                            Surface(
                                color = BrassGold,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "$discountPct% OFF",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalTeak,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    // Available Swatches dots
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        product.availableSwatches.take(3).forEach { swatch ->
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(swatch.hexColor))
                            )
                        }
                    }
                }
            }
        }
    }
}
