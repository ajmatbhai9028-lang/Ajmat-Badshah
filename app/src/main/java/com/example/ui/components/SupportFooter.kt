package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

/**
 * Creates an Intent to dial the specified support phone number via Intent.ACTION_DIAL.
 */
fun createSupportCallIntent(phoneNumber: String = "7320054330"): Intent {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return intent
}

/**
 * Creates an Intent to open WhatsApp chat using an https://wa.me/ URL intent.
 */
fun createSupportWhatsAppIntent(phoneNumber: String = "9572349911"): Intent {
    val cleanNumber = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
    val url = "https://wa.me/$cleanNumber"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return intent
}

/**
 * SupportFooter component providing instant access to direct phone calling
 * (via Intent.ACTION_DIAL) and WhatsApp messaging (via https://wa.me/ URL).
 */
@Composable
fun SupportFooter(
    modifier: Modifier = Modifier,
    phoneNumber: String = "7320054330",
    whatsappNumber: String = "9572349911",
    title: String = "Need Help & Support?",
    subtitle: String = "Speak with our master craftsmen or chat directly with our concierge team."
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = WalnutBrown
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("support_footer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = BrassGold,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = "Customer Support",
                            tint = CharcoalTeak,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = WarmWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = SandStone
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 'Call Now' button linking to 7320054330 via Intent.ACTION_DIAL
                Button(
                    onClick = {
                        try {
                            val intent = createSupportCallIntent(phoneNumber)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable to initiate call to $phoneNumber", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrassGold,
                        contentColor = CharcoalTeak
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("call_now_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call Now",
                        tint = CharcoalTeak,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Call Now",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = CharcoalTeak
                    )
                }

                // 'WhatsApp Chat' button linking to 9572349911 using an https://wa.me/ URL intent
                Button(
                    onClick = {
                        try {
                            val intent = createSupportWhatsAppIntent(whatsappNumber)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable to launch WhatsApp chat", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366),
                        contentColor = WarmWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("whatsapp_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp Chat",
                        tint = WarmWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WhatsApp Chat",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = WarmWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Direct: +91 $phoneNumber",
                    style = MaterialTheme.typography.labelSmall,
                    color = SandStone.copy(alpha = 0.85f)
                )
                Text(
                    text = "WhatsApp: +91 $whatsappNumber",
                    style = MaterialTheme.typography.labelSmall,
                    color = SandStone.copy(alpha = 0.85f)
                )
            }
        }
    }
}
