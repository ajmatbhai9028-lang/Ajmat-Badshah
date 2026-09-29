package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InteriorStyle
import com.example.data.model.Product
import com.example.data.model.UserStyleProfile
import com.example.ui.components.formatPrice
import com.example.ui.components.getDrawableResForProduct
import com.example.ui.theme.*
import com.example.ui.viewmodel.WciViewModel

data class QuizOption(
    val style: InteriorStyle,
    val title: String,
    val description: String,
    val paletteHexes: List<Long>? = null,
    val materialsSummary: String? = null
)

data class QuizQuestion(
    val id: Int,
    val category: String,
    val question: String,
    val subtitle: String,
    val options: List<QuizOption>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleQuizScreen(
    viewModel: WciViewModel,
    onNavigateBack: () -> Unit,
    onQuizComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val existingProfile = uiState.userStyleProfile

    // 5 curated questions covering architecture, palette, texture, focal piece, and space goal
    val questions = remember {
        listOf(
            QuizQuestion(
                id = 1,
                category = "Atmosphere & Silhouette",
                question = "What architectural silhouette resonates most with your sanctuary?",
                subtitle = "Select the structural design language that feels like home to you.",
                options = listOf(
                    QuizOption(
                        style = InteriorStyle.MODERN,
                        title = "Modern Minimalist",
                        description = "Sleek, low-slung horizontal silhouettes, geometric clarity, and calm uncluttered negative space."
                    ),
                    QuizOption(
                        style = InteriorStyle.BOHEMIAN,
                        title = "Earthy Bohemian",
                        description = "Warm layered curves, organic cane webbing, botanical warmth, and free-spirited relaxed living."
                    ),
                    QuizOption(
                        style = InteriorStyle.TRADITIONAL,
                        title = "Curated Traditional",
                        description = "Heirloom craftsmanship, stately symmetry, rich timber wainscoting, and deep historical elegance."
                    ),
                    QuizOption(
                        style = InteriorStyle.INDUSTRIAL,
                        title = "Architectural Industrial",
                        description = "Raw structural blackened steel, exposed masonry, smoked rift-sawn timbers, and urban loft honesty."
                    )
                )
            ),
            QuizQuestion(
                id = 2,
                category = "Color Harmony & Tone",
                question = "Which tonal color palette naturally calms your senses?",
                subtitle = "Choose the mood and color resonance for your everyday living space.",
                options = listOf(
                    QuizOption(
                        style = InteriorStyle.MODERN,
                        title = "Warm Atelier Neutrals",
                        description = "Warm Oat cream, Roman travertine ivory, soft pumice taupe, and subtle muted charcoal.",
                        paletteHexes = listOf(0xFFEDE7DF, 0xFFD6CEBF, 0xFF8C7E72, 0xFF4A3B32)
                    ),
                    QuizOption(
                        style = InteriorStyle.BOHEMIAN,
                        title = "Sun-Baked Earth & Botanicals",
                        description = "Warm terracotta clay, sunlit ochre, muted forest sage, and raw unbleached linen.",
                        paletteHexes = listOf(0xFFB8664D, 0xFFD89D6A, 0xFF637059, 0xFFF2ECE1)
                    ),
                    QuizOption(
                        style = InteriorStyle.TRADITIONAL,
                        title = "Heritage Cognac & Deep Timber",
                        description = "Rich amber cognac leather, deep espresso walnut, antique burnished brass, and warm alabaster.",
                        paletteHexes = listOf(0xFF2A2421, 0xFFB36738, 0xFF855836, 0xFFE0D8CB)
                    ),
                    QuizOption(
                        style = InteriorStyle.INDUSTRIAL,
                        title = "Loft Gunmetal & Smoked Ash",
                        description = "Blackened steel, smoked European ash, cold-cast cement gray, and warm reclaimed oak.",
                        paletteHexes = listOf(0xFF1F1E1D, 0xFF3D3A37, 0xFF7A7D82, 0xFFB0A89F)
                    )
                )
            ),
            QuizQuestion(
                id = 3,
                category = "Tactile Materials",
                question = "What tactile textures do you want beneath your fingertips?",
                subtitle = "The materials you touch shape how you feel in your home.",
                options = listOf(
                    QuizOption(
                        style = InteriorStyle.MODERN,
                        title = "Honed Stone & Heavy Boucle",
                        description = "Silky matte Italian travertine, bleached European white oak, tactile boucle, and brushed champagne brass.",
                        materialsSummary = "Travertine • White Oak • Boucle • Brass"
                    ),
                    QuizOption(
                        style = InteriorStyle.BOHEMIAN,
                        title = "Handwoven Cane & Belgian Linen",
                        description = "Textured rattan webbing, plantation teak, stonewashed Belgian linen, and hand-thrown unglazed ceramics.",
                        materialsSummary = "Rattan • Teak • Linen • Stoneware"
                    ),
                    QuizOption(
                        style = InteriorStyle.TRADITIONAL,
                        title = "Full-Grain Leather & Solid Walnut",
                        description = "Vegetable-tanned aniline pull-up leather, solid American black walnut, fine wool, and antiqued brass nailheads.",
                        materialsSummary = "Aniline Leather • Black Walnut • Wool"
                    ),
                    QuizOption(
                        style = InteriorStyle.INDUSTRIAL,
                        title = "Blackened Steel & Smoked Timber",
                        description = "Laser-welded architectural steel, smoked ash hardwood, distressed leather, and industrial tension turnbuckles.",
                        materialsSummary = "Steel • Smoked Ash • Distressed Leather"
                    )
                )
            ),
            QuizQuestion(
                id = 4,
                category = "Living Room Centerpiece",
                question = "What statement centerpiece anchors your dream living space?",
                subtitle = "Every iconic space begins with a central conversation anchor.",
                options = listOf(
                    QuizOption(
                        style = InteriorStyle.MODERN,
                        title = "Sculptural Modular Cloud Sofa",
                        description = "Low-profile architectural seating in textured oat boucle with clean geometry and deep restorative lounging."
                    ),
                    QuizOption(
                        style = InteriorStyle.BOHEMIAN,
                        title = "Artisanal Woven Rattan Lounger",
                        description = "A hand-bent sun-washed cane lounger nestled beside leafy botanicals, Moroccan rugs, and organic ceramics."
                    ),
                    QuizOption(
                        style = InteriorStyle.TRADITIONAL,
                        title = "Deep-Tufted Leather Chesterfield",
                        description = "An heirloom leather wingback armchair flanking an artisan-carved solid cherrywood library credenza."
                    ),
                    QuizOption(
                        style = InteriorStyle.INDUSTRIAL,
                        title = "Raw Smoked Oak & Steel Trestle Desk",
                        description = "A heavy solid oak workbench on blackened steel I-beam trestles paired with open architectural steel shelving."
                    )
                )
            ),
            QuizQuestion(
                id = 5,
                category = "Design Objective",
                question = "What is your primary living space objective?",
                subtitle = "How do you want your transformed home to serve your lifestyle?",
                options = listOf(
                    QuizOption(
                        style = InteriorStyle.MODERN,
                        title = "Serene & Uncluttered Calm",
                        description = "Eliminate visual clutter with quiet, intentional pieces that foster mindfulness and focus."
                    ),
                    QuizOption(
                        style = InteriorStyle.BOHEMIAN,
                        title = "Soulful & Collected Warmth",
                        description = "Create a cozy, layered atmosphere filled with warmth, organic textures, and relaxed hospitality."
                    ),
                    QuizOption(
                        style = InteriorStyle.TRADITIONAL,
                        title = "Enduring Heritage & Provenance",
                        description = "Invest in generational heirlooms with timeless joinery that patina gracefully for decades."
                    ),
                    QuizOption(
                        style = InteriorStyle.INDUSTRIAL,
                        title = "Architectural Utility & Character",
                        description = "Build a bold, durable, functional designer loft that celebrates structural materials and raw honesty."
                    )
                )
            )
        )
    }

    // State: Selected option for each question index (0..4)
    val selectedAnswers = remember { mutableStateMapOf<Int, InteriorStyle>() }
    var currentStep by remember { mutableIntStateOf(0) }
    var calculatedProfile by remember { mutableStateOf<UserStyleProfile?>(existingProfile) }
    var isShowingResults by remember { mutableStateOf(existingProfile != null) }

    fun calculateResults(): UserStyleProfile {
        val tally = mutableMapOf(
            InteriorStyle.MODERN to 0,
            InteriorStyle.BOHEMIAN to 0,
            InteriorStyle.TRADITIONAL to 0,
            InteriorStyle.INDUSTRIAL to 0
        )
        selectedAnswers.values.forEach { style ->
            tally[style] = (tally[style] ?: 0) + 1
        }
        val total = selectedAnswers.size.coerceAtLeast(1)
        val sortedStyles = tally.entries.sortedByDescending { it.value }

        val primary = sortedStyles.firstOrNull()?.key ?: InteriorStyle.MODERN
        val secondary = sortedStyles.getOrNull(1)?.let { if (it.value > 0 && it.key != primary) it.key else null }

        // Percentage breakdown
        val scoreMap = tally.mapValues { (_, count) ->
            Math.round((count.toFloat() / total) * 100).toInt()
        }

        val profile = UserStyleProfile(
            primaryStyle = primary,
            secondaryStyle = secondary,
            styleScores = scoreMap,
            preferredMaterials = primary.keyMaterials,
            preferredPalette = primary.colorPaletteName,
            spaceGoal = when (selectedAnswers[4]) {
                InteriorStyle.BOHEMIAN -> "Soulful & Collected Warmth"
                InteriorStyle.TRADITIONAL -> "Enduring Heritage & Provenance"
                InteriorStyle.INDUSTRIAL -> "Architectural Utility & Character"
                else -> "Serene & Uncluttered Calm"
            }
        )
        return profile
    }

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
                                text = if (isShowingResults) "Atelier Style Diagnostic" else "Interior Style Quiz",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isShowingResults) "Personalized Aesthetic Revealed" else "Step ${currentStep + 1} of ${questions.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (isShowingResults) {
                                    isShowingResults = false
                                } else if (currentStep > 0) {
                                    currentStep--
                                } else {
                                    onNavigateBack()
                                }
                            },
                            modifier = Modifier.testTag("style_quiz_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (isShowingResults) {
                            TextButton(
                                onClick = {
                                    selectedAnswers.clear()
                                    currentStep = 0
                                    isShowingResults = false
                                },
                                modifier = Modifier.testTag("retake_quiz_header_button")
                            ) {
                                Text("Retake", color = BrassGold, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )

                if (!isShowingResults) {
                    StyleQuizTopProgressBar(
                        currentStep = currentStep,
                        totalSteps = questions.size,
                        onStepClick = { targetStep ->
                            if (targetStep < currentStep) {
                                currentStep = targetStep
                            }
                        }
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
            if (isShowingResults && calculatedProfile != null) {
                // RESULT SCREEN
                StyleQuizResultContent(
                    profile = calculatedProfile!!,
                    viewModel = viewModel,
                    onApplyAndShop = {
                        viewModel.saveStyleProfile(calculatedProfile!!)
                        onQuizComplete()
                    },
                    onRetake = {
                        selectedAnswers.clear()
                        currentStep = 0
                        isShowingResults = false
                    }
                )
            } else {
                // QUESTION SCREEN
                val currentQ = questions[currentStep]
                val currentSelected = selectedAnswers[currentStep]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .testTag("quiz_step_${currentStep + 1}"),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Category Badge
                        Surface(
                            color = BrassGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = currentQ.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrassGold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentQ.question,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )

                        Text(
                            text = currentQ.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        // Option Cards
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(currentQ.options) { option ->
                                val isSelected = currentSelected == option.style

                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) WalnutBrown else MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (isSelected) BorderStroke(1.5.dp, BrassGold) else null,
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedAnswers[currentStep] = option.style
                                        }
                                        .testTag("option_${option.style.name.lowercase()}_step_${currentStep + 1}")
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // Radio icon
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isSelected) BrassGold else MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    if (isSelected) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = CharcoalTeak,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = option.title,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) WarmWhite else MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = option.style.subtitle,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isSelected) SandStone else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            // Visual style thumbnail
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = getDrawableResForProduct(option.style.imageResName)),
                                                    contentDescription = option.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = option.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSelected) WarmWhite.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        // Optional Palette Preview Swatches
                                        if (option.paletteHexes != null) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                option.paletteHexes.forEach { hex ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(hex))
                                                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                                    )
                                                }
                                                Text(
                                                    text = option.title,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isSelected) SandStone else MaterialTheme.colorScheme.outline,
                                                    modifier = Modifier.padding(start = 4.dp)
                                                )
                                            }
                                        }

                                        // Optional Materials pill summary
                                        if (option.materialsSummary != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                color = if (isSelected) CharcoalTeak.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = option.materialsSummary,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (isSelected) SandStone else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Navigation Actions footer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (currentStep < questions.size - 1) {
                                    currentStep++
                                } else {
                                    val profile = calculateResults()
                                    calculatedProfile = profile
                                    isShowingResults = true
                                }
                            },
                            enabled = currentSelected != null,
                            colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("quiz_next_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentStep < questions.size - 1) "Next Diagnostic Step" else "Reveal My Curated Style Persona",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = WarmWhite
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = if (currentStep < questions.size - 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = BrassGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StyleQuizResultContent(
    profile: UserStyleProfile,
    viewModel: WciViewModel,
    onApplyAndShop: () -> Unit,
    onRetake: () -> Unit
) {
    val primary = profile.primaryStyle
    val secondary = profile.secondaryStyle
    val uiState by viewModel.uiState.collectAsState()

    // Filter products matching this style
    val styleProducts = remember(primary) {
        viewModel.repository.catalogProducts.filter { it.styles.contains(primary) }
    }

    // Filter moodboard matching this style
    val styleMoodboard = remember(primary) {
        viewModel.repository.moodboards.find { it.matchingStyle == primary }
            ?: viewModel.repository.moodboards.first()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quiz_result_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Result Persona Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.height(240.dp)) {
                    Image(
                        painter = painterResource(id = getDrawableResForProduct(primary.imageResName)),
                        contentDescription = primary.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xE61B1816))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp)
                    ) {
                        Surface(
                            color = BrassGold,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "YOUR SIGNATURE AESTHETIC",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalTeak,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = primary.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = WarmWhite
                            )
                        )

                        Text(
                            text = if (secondary != null) {
                                "${primary.subtitle} • Accented with ${secondary.title} nuance"
                            } else {
                                primary.subtitle
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = SandStone
                        )
                    }
                }
            }
        }

        // 2. Compatibility Breakdown Bars
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Aesthetic Diagnostic Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Based on your 5 spatial preferences across light, timber, and form.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    InteriorStyle.values().forEach { st ->
                        val score = profile.styleScores[st] ?: 0
                        val isPrimary = st == primary
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = st.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isPrimary) WalnutBrown else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isPrimary) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(color = BrassGold, shape = RoundedCornerShape(4.dp)) {
                                            Text(
                                                "PRIMARY",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalTeak,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "$score%",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPrimary) WalnutBrown else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (score / 100f).coerceIn(0.05f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (isPrimary) BrassGold else WalnutBrown.copy(alpha = 0.6f),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 3. Signature Materials & Color Swatches
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Curated Palette: ${primary.colorPaletteName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        primary.colorHexes.forEach { hex ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(hex))
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Signature Atelier Materials",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        primary.keyMaterials.forEach { mat ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = mat,
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = SandStone.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = WalnutBrown,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = primary.designAdvice,
                                style = MaterialTheme.typography.bodySmall,
                                color = WalnutBrown
                            )
                        }
                    }
                }
            }
        }

        // 4. Personalized Curated Products Carousel
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Personalized Picks For You (${styleProducts.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Top Match",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrassGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(styleProducts) { prod ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .width(220.dp)
                            .testTag("result_product_card_${prod.id}")
                    ) {
                        Column {
                            Box(modifier = Modifier.height(130.dp)) {
                                Image(
                                    painter = painterResource(id = getDrawableResForProduct(prod.imageResName)),
                                    contentDescription = prod.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    color = WalnutBrown,
                                    shape = RoundedCornerShape(bottomEnd = 10.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "96% Match",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrassGold,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
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
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = WalnutBrown
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Curated Shoppable Moodboard Look
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SandStone),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = WalnutBrown, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                "CURATED ROOM BUNDLE",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            "${styleMoodboard.bundleDiscountPct}% OFF BUNDLE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = WalnutBrown
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = styleMoodboard.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = WalnutBrown
                        )
                    )
                    Text(
                        text = styleMoodboard.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = LightWalnut
                    )
                }
            }
        }

        // 6. Action Buttons
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Button(
                    onClick = onApplyAndShop,
                    colors = ButtonDefaults.buttonColors(containerColor = WalnutBrown),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("apply_style_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BrassGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Apply Style & Shop Curated Collection",
                        fontWeight = FontWeight.Bold,
                        color = WarmWhite,
                        fontSize = 15.sp
                    )
                }

                OutlinedButton(
                    onClick = onRetake,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("retake_quiz_button")
                ) {
                    Text(
                        text = "Retake Style Diagnostic Quiz",
                        fontWeight = FontWeight.SemiBold,
                        color = WalnutBrown
                    )
                }
            }
        }
    }
}

/**
 * Calculates normalized quiz progress in [0.0f, 1.0f].
 */
fun calculateQuizProgress(currentStep: Int, totalSteps: Int): Float {
    if (totalSteps <= 0) return 0f
    return ((currentStep + 1).toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)
}

/**
 * Calculates quiz completion percentage integer in [0, 100].
 */
fun calculateQuizPercentage(currentStep: Int, totalSteps: Int): Int {
    if (totalSteps <= 0) return 0
    return (((currentStep + 1) * 100) / totalSteps).coerceIn(0, 100)
}

/**
 * Visual multi-step progress bar indicator placed at the top of the style quiz screen.
 * Features an animated progress bar, step counter, completion percentage badge, and
 * interactive multi-step nodes for effortless multi-step navigation.
 */
@Composable
fun StyleQuizTopProgressBar(
    currentStep: Int,
    totalSteps: Int,
    onStepClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val targetProgress = calculateQuizProgress(currentStep, totalSteps)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "quiz_progress"
    )
    val percentComplete = calculateQuizPercentage(currentStep, totalSteps)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .testTag("style_quiz_top_progress_section")
    ) {
        // Step count and percentage banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "STEP ${currentStep + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = WalnutBrown
                )
                Text(
                    text = " OF $totalSteps",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = BrassGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$percentComplete% COMPLETED",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrassGold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Animated smooth visual progress bar
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .testTag("style_quiz_progress_bar"),
            color = BrassGold,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Segmented Step Indicator Nodes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("style_quiz_step_indicator"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (stepIndex in 0 until totalSteps) {
                val isCompleted = stepIndex < currentStep
                val isCurrent = stepIndex == currentStep

                val nodeColor = when {
                    isCompleted -> BrassGold
                    isCurrent -> WalnutBrown
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                val textColor = when {
                    isCompleted -> CharcoalTeak
                    isCurrent -> WarmWhite
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = stepIndex < totalSteps - 1)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(nodeColor)
                            .then(
                                if (isCurrent) Modifier.border(1.5.dp, BrassGold, CircleShape)
                                else Modifier
                            )
                            .clickable(enabled = isCompleted) {
                                onStepClick(stepIndex)
                            }
                            .testTag("quiz_step_node_${stepIndex + 1}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Step ${stepIndex + 1} completed",
                                tint = CharcoalTeak,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Text(
                                text = "${stepIndex + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = textColor
                            )
                        }
                    }

                    if (stepIndex < totalSteps - 1) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .padding(horizontal = 4.dp)
                                .background(
                                    if (stepIndex < currentStep) BrassGold else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }
            }
        }
    }
}

