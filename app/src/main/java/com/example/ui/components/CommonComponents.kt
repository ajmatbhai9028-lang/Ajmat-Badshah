package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Product
import com.example.ui.theme.*

fun launchDirectCall(context: Context, phoneNumber: String = "7320054330") {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to initiate call to $phoneNumber", Toast.LENGTH_SHORT).show()
    }
}

fun launchWhatsApp(
    context: Context,
    phoneNumber: String = "9572349911",
    message: String = "Hello WCI Furniture, I would like to inquire about your collection and services."
) {
    try {
        val cleanNumber = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
        val encodedMsg = Uri.encode(message)
        val url = if (message.isNotBlank()) "https://wa.me/$cleanNumber?text=$encodedMsg" else "https://wa.me/$cleanNumber"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val fallbackUrl = "https://wa.me/$phoneNumber"
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(fallbackIntent)
        } catch (_: Exception) {
            Toast.makeText(context, "WhatsApp not available", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun AtelierDirectContactCard(
    modifier: Modifier = Modifier,
    callNumber: String = "7320054330",
    whatsappNumber: String = "9572349911"
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = WalnutBrown),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = BrassGold,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = CharcoalTeak,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Instant Atelier Concierge",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WarmWhite
                    )
                    Text(
                        text = "Direct call & WhatsApp support available now",
                        style = MaterialTheme.typography.bodySmall,
                        color = SandStone
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Call Button (7320054330)
                Button(
                    onClick = { launchDirectCall(context, callNumber) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrassGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("direct_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Direct Call",
                        tint = CharcoalTeak,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Call $callNumber",
                        fontWeight = FontWeight.Bold,
                        color = CharcoalTeak,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // WhatsApp Button (9572349911)
                Button(
                    onClick = { launchWhatsApp(context, whatsappNumber) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("direct_whatsapp_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp Chat",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WhatsApp $whatsappNumber",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

fun formatPrice(amount: Double, currency: String, multiplier: Double = 1.0): String {
    val symbol = when {
        currency.contains("EUR") -> "€"
        currency.contains("GBP") -> "£"
        currency.contains("INR") -> "₹"
        currency.contains("JPY") -> "¥"
        currency.contains("CAD") -> "CA$"
        currency.contains("AUD") -> "AU$"
        else -> "$"
    }
    val converted = amount * multiplier
    return if (currency.contains("JPY") || currency.contains("INR")) {
        "$symbol${String.format("%,.0f", converted)}"
    } else {
        "$symbol${String.format("%,.2f", converted)}"
    }
}

fun getDrawableResForProduct(resName: String): Int {
    return when (resName) {
        "sofa_nordic" -> R.drawable.sofa_nordic
        "armchair_cognac" -> R.drawable.armchair_cognac
        "table_travertine" -> R.drawable.table_travertine
        "lamp_ceramic" -> R.drawable.lamp_ceramic
        "hero_showroom" -> R.drawable.hero_showroom
        "img_style_modern" -> R.drawable.img_style_modern
        "img_style_bohemian" -> R.drawable.img_style_bohemian
        "img_style_traditional" -> R.drawable.img_style_traditional
        "img_style_industrial" -> R.drawable.img_style_industrial
        else -> R.drawable.ic_launcher_wci
    }
}

@Composable
fun EcoBadge(sustainabilityScore: Int, carbonOffset: Int, modifier: Modifier = Modifier) {
    Surface(
        color = ForestEco.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = "Eco friendly",
                tint = ForestEco,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Eco $sustainabilityScore% • -$carbonOffset kg CO₂",
                style = MaterialTheme.typography.labelSmall,
                color = ForestEco,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WciTopBar(
    title: String,
    wishlistCount: Int,
    cartCount: Int,
    isOffline: Boolean,
    onSearchClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onCartClick: () -> Unit,
    onScannerClick: () -> Unit,
    onOfflineToggle: () -> Unit,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isOffline) {
                    Text(
                        text = "● Offline Mode Active (Local Cache)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
            } else {
                IconButton(
                    onClick = onOfflineToggle,
                    modifier = Modifier.testTag("offline_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isOffline) Icons.Filled.CloudOff else Icons.Outlined.CloudDone,
                        contentDescription = "Offline Mode",
                        tint = if (isOffline) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onScannerClick,
                modifier = Modifier.testTag("scanner_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.QrCodeScanner,
                    contentDescription = "Showroom Barcode & QR Scanner"
                )
            }
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("search_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search catalog"
                )
            }
            // Wishlist icon with badge
            BadgedBox(
                badge = {
                    if (wishlistCount > 0) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text(wishlistCount.toString())
                        }
                    }
                }
            ) {
                IconButton(
                    onClick = onWishlistClick,
                    modifier = Modifier.testTag("wishlist_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist"
                    )
                }
            }
            // Cart icon with badge
            BadgedBox(
                badge = {
                    if (cartCount > 0) {
                        Badge(containerColor = BrassGold) {
                            Text(cartCount.toString(), color = CharcoalTeak)
                        }
                    }
                }
            ) {
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier.testTag("cart_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = "Shopping Cart"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}
