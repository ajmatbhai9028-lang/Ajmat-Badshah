package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SupportFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesCareScreen(
    viewModel: WciViewModel
) {
    val warranties by viewModel.warranties.collectAsState()
    var showRepairTicketDialog by remember { mutableStateOf(false) }
    var repairTicketSubmitted by remember { mutableStateOf(false) }
    var showSubscriptionSuccess by remember { mutableStateOf(false) }

    // DIY Assembly checklist state
    var step1Done by remember { mutableStateOf(true) }
    var step2Done by remember { mutableStateOf(true) }
    var step3Done by remember { mutableStateOf(false) }
    var step4Done by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("services_care_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Digital Warranty Manager Section
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BrassGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Digital Warranty Vault",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Surface(
                            color = ForestEco.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "10-Year Atelier Guarantee",
                                style = MaterialTheme.typography.labelSmall,
                                color = ForestEco,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    warranties.forEach { warranty ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(warranty.productName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("Serial: ${warranty.serialNumber} • Purchased: ${warranty.purchaseDate}", style = MaterialTheme.typography.labelSmall)
                                Text("Status: ${warranty.status}", color = ForestEco, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showRepairTicketDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit In-Home Repair Ticket")
                    }
                }
            }
        }

        // 2. Interactive DIY Assembly Progress Tracker
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive Assembly Checklist",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "København Modular Sofa • Estimated DIY Time: 15 mins",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val steps = listOf(
                        Triple("Step 1: Unbox and align white oak base rails", step1Done) { step1Done = !step1Done },
                        Triple("Step 2: Fasten 4 structural M8 bolts with included brass key", step2Done) { step2Done = !step2Done },
                        Triple("Step 3: Interlock modular seat core onto oak suspension", step3Done) { step3Done = !step3Done },
                        Triple("Step 4: Fluff high-density cruelty-free down cushions", step4Done) { step4Done = !step4Done }
                    )

                    steps.forEach { (desc, isDone, onToggle) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggle() }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = isDone,
                                onCheckedChange = { onToggle() },
                                colors = CheckboxDefaults.colors(checkedColor = ForestEco)
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        // 3. DIY Care & Maintenance Video Guides
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Master Material Care Library",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Expert guidance to maintain natural patina over generations",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val guides = listOf(
                        "Restoring Natural Luster to Solid Oak & Walnut" to "3 min video • Beeswax application",
                        "Emergency Spill Treatment on Boucle & Velvet" to "2 min video • Water-free blotting",
                        "Aniline Leather Conditioning & Scratch Buffing" to "4 min video • Organic balm care"
                    )

                    guides.forEach { (title, subtitle) ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { /* Play video */ }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, tint = BrassGold, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Consumables Subscription Service
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SandStone),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Autorenew, contentDescription = null, tint = WalnutBrown)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Organic Care Consumables Subscription",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WalnutBrown
                        )
                    }
                    Text(
                        text = "Receive WCI Artisanal Wood Butter & Organic Leather Balm every 6 months automatically. Keeps timber hydrated and supple.",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightWalnut
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showSubscriptionSuccess = true },
                        colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Subscribe for $24 / 6 Months (Free Shipping)")
                    }
                    if (showSubscriptionSuccess) {
                        Text(
                            text = "✓ Care subscription activated! First kit dispatched with your order.",
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestEco,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // 5. Help & Client Support Section with SupportFooter
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("help_support_section")
            ) {
                Text(
                    text = "Help & Client Support",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SupportFooter(
                    phoneNumber = "7320054330",
                    whatsappNumber = "9572349911"
                )
            }
        }
    }

    if (showRepairTicketDialog) {
        AlertDialog(
            onDismissRequest = { showRepairTicketDialog = false },
            title = { Text("Schedule In-Home Repair") },
            text = {
                Column {
                    Text("Under your 10-year warranty, certified master technicians will visit your home for joinery tune-ups, fabric repair, or hardware leveling.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Describe issue (e.g. slight hinge looseness)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repairTicketSubmitted = true
                        showRepairTicketDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown)
                ) {
                    Text("Submit Ticket")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRepairTicketDialog = false }) { Text("Cancel") }
            }
        )
    }
}
