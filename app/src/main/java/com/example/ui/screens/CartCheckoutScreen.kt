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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CartItemEntity
import com.example.ui.components.formatPrice
import com.example.ui.components.getDrawableResForProduct
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    viewModel: WciViewModel,
    onNavigateBack: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var shippingAddress by remember { mutableStateOf("742 Evergreen Terrace, Suite 4B, San Francisco, CA") }
    var secondaryAddress by remember { mutableStateOf("1200 Coastal Highway, Carmel-by-the-Sea, CA") }
    var isMultiAddressSplit by remember { mutableStateOf(false) }
    var isExpressDelivery by remember { mutableStateOf(false) }
    var isEcoPackaging by remember { mutableStateOf(true) }
    var isInsuranceOptIn by remember { mutableStateOf(true) }
    var selectedPaymentMethod by remember { mutableStateOf("CREDIT_CARD") } // "CREDIT_CARD", "UPI", "EMI", "DIGITAL_WALLET"
    var enteredPromoCode by remember { mutableStateOf("") }
    var promoMessage by remember { mutableStateOf("") }
    var selectedTimeSlot by remember { mutableStateOf("Thursday, Sept 25 (2:00 PM - 5:00 PM)") }

    // Calculation
    val itemsSubtotal = remember(cartItems) {
        cartItems.sumOf { item ->
            val p = viewModel.repository.getProductById(item.productId)
            (p?.price ?: 0.0) * item.quantity
        }
    }

    val assemblyTotal = remember(cartItems) {
        cartItems.count { it.assemblyBooked } * 89.0
    }

    val shippingFee = if (itemsSubtotal > 1500.0) 0.0 else 120.0
    val expressFee = if (isExpressDelivery) 95.0 else 0.0
    val insuranceFee = if (isInsuranceOptIn && cartItems.isNotEmpty()) 35.0 else 0.0
    val promoDiscount = (itemsSubtotal * uiState.promoDiscountPct) / 100.0
    val grandTotal = (itemsSubtotal + assemblyTotal + shippingFee + expressFee + insuranceFee - promoDiscount).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Atelier Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Grand Total Investment",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatPrice(grandTotal, uiState.selectedCurrency, uiState.currencyMultiplier),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Button(
                                onClick = {
                                    val orderId = viewModel.placeOrder(
                                        cartItemList = cartItems,
                                        total = grandTotal,
                                        address = shippingAddress,
                                        isExpress = isExpressDelivery,
                                        timeSlot = selectedTimeSlot
                                    )
                                    onOrderPlaced(orderId)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("one_tap_checkout_button")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = BrassGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("1-Tap Secure Checkout", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SandStone
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your Atelier Bag is Empty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Discover bespoke Scandinavian and Mid-Century pieces in catalog",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown)
                    ) {
                        Text("Explore Catalog")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Cart Items List
                items(cartItems) { item ->
                    val product = viewModel.repository.getProductById(item.productId)
                    if (product != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = getDrawableResForProduct(product.imageResName)),
                                    contentDescription = product.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color(item.swatchHex))
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = item.swatchName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (item.assemblyBooked) {
                                        Text(
                                            text = "✓ White-Glove Assembly (+ $89)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ForestEco
                                        )
                                    }
                                    Text(
                                        text = formatPrice(product.price, uiState.selectedCurrency, uiState.currencyMultiplier),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { viewModel.removeCartItem(item) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MutedTerracotta)
                                }
                            }
                        }
                    }
                }

                // 2. Multi-Address Order Splitting
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CallSplit, contentDescription = null, tint = BrassGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Multi-Address Order Splitting",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                Switch(
                                    checked = isMultiAddressSplit,
                                    onCheckedChange = { isMultiAddressSplit = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = BrassGold, checkedTrackColor = WalnutBrown)
                                )
                            }
                            Text(
                                text = "Route individual pieces to primary residence and coastal holiday home in single checkout.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isMultiAddressSplit) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Destination 1: $shippingAddress", style = MaterialTheme.typography.labelSmall, color = WalnutBrown)
                                Text("Destination 2: $secondaryAddress", style = MaterialTheme.typography.labelSmall, color = BrassGold)
                            }
                        }
                    }
                }

                // 3. Delivery Scheduling & White-Glove Slot Selection
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BrassGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "White-Glove Delivery Window",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            val slots = listOf(
                                "Thursday, Sept 25 (2:00 PM - 5:00 PM)",
                                "Friday, Sept 26 (9:00 AM - 12:00 PM)",
                                "Saturday, Sept 27 (1:00 PM - 4:00 PM)"
                            )
                            slots.forEach { slot ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedTimeSlot = slot }
                                        .padding(vertical = 4.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedTimeSlot == slot,
                                        onClick = { selectedTimeSlot = slot },
                                        colors = RadioButtonDefaults.colors(selectedColor = WalnutBrown)
                                    )
                                    Text(slot, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                // 4. Express Priority & Transit Insurance
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isExpressDelivery = !isExpressDelivery },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isExpressDelivery,
                                    onCheckedChange = { isExpressDelivery = it },
                                    colors = CheckboxDefaults.colors(checkedColor = WalnutBrown)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Express 24-Hour Dispatch (+ $95)", fontWeight = FontWeight.SemiBold)
                                    Text("Immediate atelier priority packaging and direct freight lane", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isInsuranceOptIn = !isInsuranceOptIn },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isInsuranceOptIn,
                                    onCheckedChange = { isInsuranceOptIn = it },
                                    colors = CheckboxDefaults.colors(checkedColor = WalnutBrown)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Full Transit & Damage Insurance (+ $35)", fontWeight = FontWeight.SemiBold)
                                    Text("Comprehensive replacement guarantee against transit scuffs or drops", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                // 5. Promo Code & Gift Registry Voucher
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = enteredPromoCode,
                                onValueChange = { enteredPromoCode = it },
                                label = { Text("Promo / Trade Code") },
                                placeholder = { Text("e.g. WCIATELIER") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val success = viewModel.applyPromoCode(enteredPromoCode)
                                    promoMessage = if (success) "10% Atelier Discount Applied!" else "Invalid code"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown)
                            ) {
                                Text("Apply")
                            }
                        }
                    }
                    if (promoMessage.isNotBlank()) {
                        Text(
                            text = promoMessage,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (promoMessage.contains("Applied")) ForestEco else MutedTerracotta,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // 6. Payment Method Selector
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Payment Gateway Engine", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            val paymentOptions = listOf(
                                "CREDIT_CARD" to "Credit / Debit Card (Amex, Visa, Mastercard)",
                                "UPI" to "Instant UPI / Digital Wallets (Apple & Google Pay)",
                                "EMI" to "0% Interest Atelier Financing (3 - 24 Months)"
                            )
                            paymentOptions.forEach { (key, label) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPaymentMethod = key }
                                        .padding(vertical = 4.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedPaymentMethod == key,
                                        onClick = { selectedPaymentMethod = key },
                                        colors = RadioButtonDefaults.colors(selectedColor = WalnutBrown)
                                    )
                                    Text(label, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                // 7. Order Breakdown Summary
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Investment Breakdown", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Furniture Items Subtotal:")
                                Text(formatPrice(itemsSubtotal, uiState.selectedCurrency, uiState.currencyMultiplier))
                            }
                            if (assemblyTotal > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("White-Glove Assembly:")
                                    Text(formatPrice(assemblyTotal, uiState.selectedCurrency, uiState.currencyMultiplier))
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Shipping & Handling:")
                                Text(if (shippingFee == 0.0) "COMPLIMENTARY" else formatPrice(shippingFee, uiState.selectedCurrency, uiState.currencyMultiplier))
                            }
                            if (expressFee > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Express Priority:")
                                    Text(formatPrice(expressFee, uiState.selectedCurrency, uiState.currencyMultiplier))
                                }
                            }
                            if (insuranceFee > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Transit Insurance:")
                                    Text(formatPrice(insuranceFee, uiState.selectedCurrency, uiState.currencyMultiplier))
                                }
                            }
                            if (promoDiscount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Promo Discount:", color = ForestEco)
                                    Text("-${formatPrice(promoDiscount, uiState.selectedCurrency, uiState.currencyMultiplier)}", color = ForestEco)
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Grand Total:", fontWeight = FontWeight.Bold)
                                Text(
                                    formatPrice(grandTotal, uiState.selectedCurrency, uiState.currencyMultiplier),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
