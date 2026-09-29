package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AtelierDirectContactCard
import com.example.ui.components.SupportFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndHubScreen(
    viewModel: WciViewModel,
    onNavigateToStyleQuiz: () -> Unit = {},
    onNavigateToSellerStudio: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile = uiState.userStyleProfile

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var exportCompleted by remember { mutableStateOf(false) }
    var giftCardCode by remember { mutableStateOf("") }
    var giftCardBalance by remember { mutableDoubleStateOf(250.0) }
    var deskHeight by remember { mutableIntStateOf(uiState.smartDeskHeightCm) }
    var bedBrightness by remember { mutableFloatStateOf(uiState.smartBedUnderglowBrightness) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_hub_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. User Profile & Loyalty Tier Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = WalnutBrown),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrassGold,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("A", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CharcoalTeak)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Astrid Vance",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmWhite
                                )
                            )
                            Text(
                                text = "Member since 2024 • Verified Atelier Collector",
                                style = MaterialTheme.typography.bodySmall,
                                color = SandStone
                            )
                        }
                        Surface(
                            color = BrassGold,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "GOLD TIER",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalTeak,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "2,450 Loyalty Points (550 points to Platinum VIP)",
                        style = MaterialTheme.typography.labelMedium,
                        color = SandStone
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { 2450f / 3000f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = BrassGold,
                        trackColor = Color(0x33FFFFFF)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Active Perks: VIP 48-Hour Early Sale Access • Free Fabric Swatches • Direct Master Craftsman Channel",
                        style = MaterialTheme.typography.labelSmall,
                        color = SandStone
                    )
                }
            }
        }

        // 1b. Interior Design Style Persona Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrassGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Interior Style Persona",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        if (userProfile != null) {
                            TextButton(onClick = onNavigateToStyleQuiz) {
                                Text("Retake", color = BrassGold, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (userProfile != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = BrassGold, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = userProfile.primaryStyle.title.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalTeak,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${userProfile.styleScores[userProfile.primaryStyle] ?: 90}% Affinity",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = userProfile.primaryStyle.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Palette: ${userProfile.primaryStyle.colorPaletteName}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = WalnutBrown
                        )
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Take our 60-second style diagnostic to uncover your signature aesthetic and personalize your Atelier recommendations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onNavigateToStyleQuiz,
                            colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrassGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Launch Style Diagnostic Quiz", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 1c. Atelier Seller Studio & Inventory Access
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = WalnutBrown),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSellerStudio() }
                    .testTag("hub_seller_studio_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BrassGold,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = CharcoalTeak, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Atelier Seller Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
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
                            text = "List furniture products, upload multi-angle photos, configure 360° tours, set discounted pricing & manage inventory stock.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SandStone
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = BrassGold)
                }
            }
        }

        // 1d. Direct Call & WhatsApp Concierge
        item {
            SupportFooter(
                phoneNumber = "7320054330",
                whatsappNumber = "9572349911"
            )
        }

        // 2. Smart Furniture Connectivity Hub
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sensors, contentDescription = null, tint = BrassGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Furniture IoT Studio",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "Connected devices in your residence",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // 2a. Smart Standing Desk Height Controller
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("WCI Motorized Ergonomic Desk", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("${deskHeight} cm", fontWeight = FontWeight.Bold, color = BrassGold)
                            }
                            Slider(
                                value = deskHeight.toFloat(),
                                onValueChange = {
                                    deskHeight = it.toInt()
                                    viewModel.setSmartDeskHeight(deskHeight)
                                },
                                valueRange = 65f..125f,
                                colors = SliderDefaults.colors(thumbColor = WalnutBrown, activeTrackColor = BrassGold)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { deskHeight = 74; viewModel.setSmartDeskHeight(74) },
                                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Preset: Sit (74cm)", style = MaterialTheme.typography.labelSmall)
                                }
                                Button(
                                    onClick = { deskHeight = 108; viewModel.setSmartDeskHeight(108) },
                                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Preset: Stand (108cm)", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    // 2b. Artemis Bed Ambient Glow Dimmer
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Artemis Bed Ambient Headboard Glow", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("${(bedBrightness * 100).toInt()}% • 2700K", fontWeight = FontWeight.Bold, color = BrassGold)
                            }
                            Slider(
                                value = bedBrightness,
                                onValueChange = {
                                    bedBrightness = it
                                    viewModel.setSmartBedLighting(it, 2700)
                                },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(thumbColor = WalnutBrown, activeTrackColor = BrassGold)
                            )
                        }
                    }
                }
            }
        }

        // 3. Referral Program (Refer & Earn)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SandStone),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Refer & Earn $150 Atelier Credit",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = WalnutBrown
                        )
                    )
                    Text(
                        text = "Gift friends $150 off their first handcrafted heirloom order over $1,000, and receive $150 credit to your balance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightWalnut
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = WarmWhite,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("WCI-ASTRID-99", fontWeight = FontWeight.Bold, color = WalnutBrown)
                            Text("Copy Link", color = BrassGold, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // 4. Digital Gift Cards & Vouchers
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Digital Gift Cards & Registry", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("Current gift balance: $${giftCardBalance.toInt()}.00", style = MaterialTheme.typography.bodySmall, color = ForestEco, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        OutlinedTextField(
                            value = giftCardCode,
                            onValueChange = { giftCardCode = it },
                            placeholder = { Text("Enter Gift Card Code") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (giftCardCode.isNotBlank()) {
                                    giftCardBalance += 100.0
                                    giftCardCode = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown)
                        ) {
                            Text("Redeem")
                        }
                    }
                }
            }
        }

        // 5. Global Localization (Currency & Language)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCurrencyDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("International Currency & Destination", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("Current: ${uiState.selectedCurrency}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }

        // 6. Privacy & Data Export (GDPR compliant)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Privacy & GDPR Compliance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("Self-service export of all saved room plans, dimensions, and purchase histories in JSON/CSV format.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { exportCompleted = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Personal Account Data")
                    }
                    if (exportCompleted) {
                        Text("✓ Export package ready. Saved to device storage.", color = ForestEco, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }

    // Currency selector dialog
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Global Currency") },
            text = {
                val currencies = listOf(
                    "USD ($)" to 1.0,
                    "EUR (€)" to 0.92,
                    "GBP (£)" to 0.78,
                    "CAD ($)" to 1.36,
                    "AUD ($)" to 1.52,
                    "INR (₹)" to 83.5,
                    "JPY (¥)" to 148.0
                )
                Column {
                    currencies.forEach { (curr, rate) ->
                        Surface(
                            color = if (uiState.selectedCurrency == curr) WalnutBrown else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.setCurrency(curr, rate)
                                    showCurrencyDialog = false
                                }
                        ) {
                            Text(
                                text = curr,
                                color = if (uiState.selectedCurrency == curr) WarmWhite else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) { Text("Cancel") }
            }
        )
    }
}
