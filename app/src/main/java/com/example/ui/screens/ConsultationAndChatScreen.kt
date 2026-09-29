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
import com.example.ui.components.AtelierDirectContactCard
import com.example.ui.components.SupportFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationAndChatScreen(
    viewModel: WciViewModel
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    var selectedConsultantRole by remember { mutableStateOf("INTERIOR_DESIGNER") } // "INTERIOR_DESIGNER", "MASTER_CRAFTSMAN", "VIDEO_SHOPPING"
    var showBookingModal by remember { mutableStateOf(false) }
    var bookingConfirmed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("consultation_chat_screen")
    ) {
        // 1. Consultant Selector Strip
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Atelier Expert Concierge",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Direct dialogue with certified architects & master joiners",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { showBookingModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BrassGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = CharcoalTeak, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Book 1-on-1 Video", color = CharcoalTeak, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedConsultantRole == "INTERIOR_DESIGNER",
                        onClick = { selectedConsultantRole = "INTERIOR_DESIGNER" },
                        label = { Text("Interior Designer") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WalnutBrown,
                            selectedLabelColor = WarmWhite
                        )
                    )
                    FilterChip(
                        selected = selectedConsultantRole == "MASTER_CRAFTSMAN",
                        onClick = { selectedConsultantRole = "MASTER_CRAFTSMAN" },
                        label = { Text("Master Craftsman") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WalnutBrown,
                            selectedLabelColor = WarmWhite
                        )
                    )
                    FilterChip(
                        selected = selectedConsultantRole == "VIDEO_SHOPPING",
                        onClick = { selectedConsultantRole = "VIDEO_SHOPPING" },
                        label = { Text("Live Showroom") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WalnutBrown,
                            selectedLabelColor = WarmWhite
                        )
                    )
                }
            }
        }

        // Direct Call (7320054330) & WhatsApp (9572349911) SupportFooter
        SupportFooter(
            phoneNumber = "7320054330",
            whatsappNumber = "9572349911",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        // 2. Chat Stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { (msg, isUser) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Surface(
                            shape = CircleShape,
                            color = WalnutBrown,
                            modifier = Modifier
                                .size(34.dp)
                                .align(Alignment.Bottom)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("A", color = BrassGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Surface(
                        color = if (isUser) WalnutBrown else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        tonalElevation = 1.dp,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser) WarmWhite else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // 3. Message Input Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("Ask about dimensions, light, finishes...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.sendChatMessage(inputMessage)
                        inputMessage = ""
                    },
                    modifier = Modifier
                        .background(WalnutBrown, CircleShape)
                        .testTag("send_chat_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = BrassGold, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    // Video Consultation Booking Modal
    if (showBookingModal) {
        AlertDialog(
            onDismissRequest = { showBookingModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = BrassGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Live Showroom Video Walkthrough")
                }
            },
            text = {
                Column {
                    Text(
                        "Schedule a private 30-minute streaming session with our showroom associate in San Francisco. See piece details up-close via high-definition camera.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Available Slots:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    val slots = listOf("Tomorrow at 11:00 AM PST", "Tomorrow at 3:30 PM PST", "Friday at 2:00 PM PST")
                    slots.forEach { slot ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    bookingConfirmed = true
                                    showBookingModal = false
                                }
                        ) {
                            Text(slot, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBookingModal = false }) { Text("Close") }
            }
        )
    }
}
