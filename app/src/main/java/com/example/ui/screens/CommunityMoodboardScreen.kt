package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.formatPrice
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityMoodboardScreen(
    viewModel: WciViewModel,
    onProductClick: (String) -> Unit,
    onNavigateToCart: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var votedChallenges by remember { mutableStateOf(setOf<String>()) }
    var bundleAddedSnackbar by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("community_moodboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SandStone),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Atelier Community & Inspiration",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = WalnutBrown
                        )
                    )
                    Text(
                        text = "Explore real client home tours, vote in monthly design challenges, and order complete designer looks in 1 click.",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightWalnut
                    )
                }
            }
        }

        // 2. Shoppable Moodboard Highlights (Get The Look)
        item {
            Text(
                text = "Curated Shoppable Moodboards",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        items(viewModel.repository.moodboards) { look ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Box(modifier = Modifier.height(180.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.hero_showroom),
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
                                text = "Save ${look.bundleDiscountPct}% as Room Bundle",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(look.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(look.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Featured Products in this Suite:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        look.bundledProductIds.forEach { pid ->
                            val p = viewModel.repository.getProductById(pid)
                            if (p != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onProductClick(p.id) }
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• ${p.name}", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        formatPrice(p.price, uiState.selectedCurrency, uiState.currencyMultiplier),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BrassGold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                look.bundledProductIds.forEach { pid ->
                                    val p = viewModel.repository.getProductById(pid)
                                    if (p != null) {
                                        viewModel.addToCart(p, p.availableSwatches.first(), assembly = true)
                                    }
                                }
                                bundleAddedSnackbar = true
                                onNavigateToCart()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = BrassGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("1-Click Buy Complete Look (${look.bundledProductIds.size} Items)")
                        }
                    }
                }
            }
        }

        // 3. User Design Challenges & Contests
        item {
            Text(
                text = "Active Monthly Design Challenges",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            )
        }

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
                        Surface(
                            color = ForestEco.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "SEPTEMBER CHALLENGE",
                                style = MaterialTheme.typography.labelSmall,
                                color = ForestEco,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text("Winner receives $1,000 WCI Credit", style = MaterialTheme.typography.labelSmall, color = BrassGold, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("The Serene Sanctuary: Under 500 Sq Ft Living Room", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("Showcasing creative room flow with København modular seating and floating shelving.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isVoted = votedChallenges.contains("SANCTUARY")
                        Text(
                            text = if (isVoted) "349 Community Votes" else "348 Community Votes",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = {
                                votedChallenges = if (isVoted) votedChallenges - "SANCTUARY" else votedChallenges + "SANCTUARY"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isVoted) ForestEco else WalnutBrown
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isVoted) Icons.Default.Check else Icons.Default.ThumbUp,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isVoted) "Voted" else "Cast Vote")
                        }
                    }
                }
            }
        }
    }
}
