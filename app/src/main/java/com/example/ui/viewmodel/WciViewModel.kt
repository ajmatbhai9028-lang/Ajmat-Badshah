package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.repository.WciRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiState(
    val selectedCategory: ProductCategory? = null,
    val selectedRoomType: RoomType = RoomType.ALL,
    val searchQuery: String = "",
    val fastShipOnly: Boolean = false,
    val maxPriceFilter: Double = 5000.0,
    val selectedCurrency: String = "USD ($)",
    val currencyMultiplier: Double = 1.0,
    val isOfflineMode: Boolean = false,
    val activeBannerIndex: Int = 0,
    val countdownSecondsRemaining: Long = 27840L, // 7h 44m
    // Smart Cart
    val multiAddressSplitEnabled: Boolean = false,
    val deliveryInsuranceOptIn: Boolean = true,
    val ecoPackagingOptIn: Boolean = true,
    val selectedTimeSlot: String = "Thursday, 2:00 PM - 5:00 PM (White Glove)",
    val promoCode: String = "",
    val promoDiscountPct: Int = 0,
    // AR state
    val arSelectedProductId: String = "WCI-SOFA-01",
    val arItemScale: Float = 1.0f,
    val arItemRotation: Float = 0f,
    val arLightingMode: String = "Warm Atelier Sunset (3000K)",
    val arSurfaceDetected: Boolean = true,
    // AR Tape Measure
    val measureStartPoint: Pair<Float, Float>? = Pair(100f, 300f),
    val measureEndPoint: Pair<Float, Float>? = Pair(380f, 300f),
    val measuredDistanceCm: Float = 214f,
    val doorClearancePass: Boolean = true,
    // AR Wall Hanging Guide
    val wallSpiritLevelAngle: Float = 0.2f, // near level
    val wallEyeLevelOffsetCm: Float = 145f,
    // AI Color Palette Generator
    val extractedRoomColors: List<Long> = listOf(0xFFD6CEBF, 0xFF8C7E72, 0xFF544941, 0xFFEBE7DD),
    // Smart Furniture control
    val smartDeskHeightCm: Int = 74,
    val smartBedUnderglowBrightness: Float = 0.65f,
    val smartBedColorTempK: Int = 2700,
    // Consultation state
    val consultationBooked: Boolean = false,
    // Trade Portal state
    val tradeCompanyTaxId: String = "",
    val tradeDiscountTier: Int = 25, // 25% for verified interior architects
    // Personalized Style Profile
    val userStyleProfile: UserStyleProfile? = null
)

class WciViewModel(application: Application) : AndroidViewModel(application) {
    val repository = WciRepository(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistItems: StateFlow<List<WishlistItemEntity>> = repository.wishlistItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userOrders: StateFlow<List<OrderEntity>> = repository.ordersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedBlueprints: StateFlow<List<SavedBlueprintEntity>> = repository.blueprintsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val warranties: StateFlow<List<WarrantyEntity>> = repository.warrantiesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mock Live GPS Delivery Tracker
    private val _liveGpsProgress = MutableStateFlow(0.42f)
    val liveGpsProgress: StateFlow<Float> = _liveGpsProgress.asStateFlow()

    private val _liveEtaMinutes = MutableStateFlow(34)
    val liveEtaMinutes: StateFlow<Int> = _liveEtaMinutes.asStateFlow()

    // 3D Room Planner state
    private val _sellerListings = MutableStateFlow<List<SellerProductListing>>(
        listOf(
            SellerProductListing(
                id = "SELLER-TRV-01",
                title = "Aura Travertine & Smoked Oak Coffee Table",
                category = ProductCategory.TABLES,
                material = "Honed Italian Travertine & Rift-Sawn Smoked Oak",
                dimensions = "48\"W x 28\"D x 15\"H (122 x 71 x 38 cm)",
                widthCm = 122f,
                depthCm = 71f,
                heightCm = 38f,
                color = "Roman Ivory Travertine / Smoked Timber",
                originalPrice = 1450.0,
                discountedPrice = 1160.0,
                discountPercent = 20,
                discountBadgeText = "20% OFF",
                stockCount = 14,
                lowStockThreshold = 3,
                sku = "WCI-TRV-110",
                barcode = "890412095502",
                status = ProductListingStatus.PUBLISHED,
                sampleDrawableRes = "table_travertine",
                multiAnglePhotos = MultiAnglePhotos(
                    frontUri = "preset://table_travertine",
                    backUri = "preset://table_travertine_rear",
                    detailUri = "preset://table_travertine_detail"
                ),
                interactive360 = Interactive360Data(
                    frameCount = 24,
                    samplePresetName = "table_travertine",
                    aiLightingQualityScore = 99,
                    aiSmoothnessScore = 97
                ),
                videoWalkthrough = VideoWalkthroughData(
                    durationSec = 25,
                    hotspots = listOf(
                        WalkthroughHotspot("h1", 3, "Honed Edge Profile", "Beveled 15° bullnose edge reveals natural travertine vein patterns", 30f, 40f),
                        WalkthroughHotspot("h2", 11, "Mortise Wood Base", "Smoked oak pedestal with internal steel tension rods", 50f, 65f),
                        WalkthroughHotspot("h3", 18, "Matte Protective Seal", "Breathable zero-VOC hydrophobic impregnator repellent", 70f, 35f)
                    )
                ),
                description = "A bold monolithic celebration of geological time and master woodworking. Hand-carved from solid travertine blocks with floating oak pedestal base.",
                viewsCount = 384,
                ordersCount = 9
            ),
            SellerProductListing(
                id = "SELLER-ARM-02",
                title = "Oslo Cognac Lounge Chair & Ottoman",
                category = ProductCategory.SEATING,
                material = "Full-Grain Italian Aniline Leather & Steam-Bent Walnut",
                dimensions = "34\"W x 35\"D x 33\"H (86 x 89 x 84 cm)",
                widthCm = 86f,
                depthCm = 89f,
                heightCm = 84f,
                color = "Amber Cognac Leather",
                originalPrice = 1890.0,
                discountedPrice = 1512.0,
                discountPercent = 20,
                discountBadgeText = "SAVE $378 • 20% OFF",
                stockCount = 4,
                lowStockThreshold = 5,
                sku = "WCI-OSL-204",
                barcode = "890412096603",
                status = ProductListingStatus.PUBLISHED,
                sampleDrawableRes = "armchair_cognac",
                multiAnglePhotos = MultiAnglePhotos(
                    frontUri = "preset://armchair_cognac",
                    backUri = "preset://armchair_cognac_rear",
                    detailUri = "preset://armchair_cognac_detail"
                ),
                interactive360 = Interactive360Data(
                    frameCount = 24,
                    samplePresetName = "armchair_cognac",
                    aiLightingQualityScore = 98,
                    aiSmoothnessScore = 96
                ),
                videoWalkthrough = VideoWalkthroughData(
                    durationSec = 22,
                    hotspots = listOf(
                        WalkthroughHotspot("h4", 4, "Saddle-Stitched Seams", "Waxed German thread hand-stitched with double topstitching", 40f, 50f),
                        WalkthroughHotspot("h5", 14, "Swivel Mechanism", "Concealed 360-degree silent ball-bearing rotation plate", 50f, 80f)
                    )
                ),
                description = "Hand-tailored cognac leather seating with supple patina that gracefully matures over decades of use.",
                viewsCount = 521,
                ordersCount = 14
            )
        )
    )
    val sellerListings: StateFlow<List<SellerProductListing>> = _sellerListings.asStateFlow()

    private val _customProducts = MutableStateFlow<List<Product>>(emptyList())
    val customProducts: StateFlow<List<Product>> = _customProducts.asStateFlow()

    private fun createInitialListingDraft(): SellerProductListing {
        return SellerProductListing(
            id = "SELLER-" + (1000..9999).random(),
            title = "",
            category = ProductCategory.SEATING,
            material = "",
            dimensions = "",
            widthCm = 210f,
            depthCm = 95f,
            heightCm = 80f,
            color = "",
            originalPrice = 0.0,
            discountedPrice = 0.0,
            discountPercent = 0,
            discountBadgeText = "",
            stockCount = 12,
            lowStockThreshold = 3,
            sku = "WCI-ART-" + (100..999).random(),
            status = ProductListingStatus.DRAFT,
            multiAnglePhotos = MultiAnglePhotos(),
            interactive360 = Interactive360Data(),
            videoWalkthrough = VideoWalkthroughData()
        )
    }

    private val _activeListingDraft = MutableStateFlow(createInitialListingDraft())
    val activeListingDraft: StateFlow<SellerProductListing> = _activeListingDraft.asStateFlow()

    private val _sellerWizardStep = MutableStateFlow(0)
    val sellerWizardStep: StateFlow<Int> = _sellerWizardStep.asStateFlow()

    // 3D Room Planner state
    private val _currentBlueprint = MutableStateFlow(
        RoomBlueprint(
            id = "DEFAULT-BLUEPRINT",
            name = "Main Living Suite",
            roomType = RoomType.LIVING_ROOM,
            widthMeters = 5.2f,
            lengthMeters = 4.4f,
            wallColorHex = 0xFFF7F5F0,
            flooringType = "Natural Chevron Oak",
            items = listOf(
                PlacedBlueprintItem(
                    id = "ITEM-1",
                    productId = "WCI-SOFA-01",
                    productName = "København 3-Seater Sofa",
                    x = 1.2f,
                    y = 1.0f,
                    widthMeters = 2.28f,
                    depthMeters = 0.96f,
                    rotationDegrees = 0f
                ),
                PlacedBlueprintItem(
                    id = "ITEM-2",
                    productId = "WCI-CHAIR-02",
                    productName = "Stockholm Lounge Chair",
                    x = 3.6f,
                    y = 1.8f,
                    widthMeters = 0.84f,
                    depthMeters = 0.86f,
                    rotationDegrees = -35f
                ),
                PlacedBlueprintItem(
                    id = "ITEM-3",
                    productId = "WCI-LAMP-04",
                    productName = "Kyoto Ceramic Lamp",
                    x = 0.5f,
                    y = 3.2f,
                    widthMeters = 0.4f,
                    depthMeters = 0.4f,
                    rotationDegrees = 0f
                )
            ),
            estimatedTotalCost = 4470.0
        )
    )
    val currentBlueprint: StateFlow<RoomBlueprint> = _currentBlueprint.asStateFlow()

    // Chat messages for Expert Consultation
    private val _chatMessages = MutableStateFlow(
        listOf(
            "Hello! I'm Astrid, Principal Interior Architect at WCI Atelier. I can assist with space clearances, fabric swatches, or styling moodboards for your home." to false,
            "For urgent assistance, you can call us directly at 7320054330 or message our team on WhatsApp at 9572349911!" to false,
            "Hi Astrid! I'm looking for a sofa that works in a 4.5m living room with south-facing natural light." to true,
            "The København in Warm Oat Boucle is exceptional for that! Boucle diffuses south-facing sunlight without glare, and the 2.28m footprint leaves a generous 1.1m walkway on either side." to false
        )
    )
    val chatMessages: StateFlow<List<Pair<String, Boolean>>> = _chatMessages.asStateFlow()

    init {
        // Countdown timer loop
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { current ->
                    current.copy(
                        countdownSecondsRemaining = if (current.countdownSecondsRemaining > 0)
                            current.countdownSecondsRemaining - 1 else 86400L
                    )
                }
            }
        }

        // Initialize default sample order & sample warranty if empty
        viewModelScope.launch {
            seedSampleDataIfFirstRun()
        }

        // Restore saved Style Profile
        val savedProfile = repository.getUserStyleProfile()
        if (savedProfile != null) {
            _uiState.update { it.copy(userStyleProfile = savedProfile) }
        }
    }

    private suspend fun seedSampleDataIfFirstRun() {
        // Insert sample warranty
        repository.registerWarranty(
            WarrantyEntity(
                serialNumber = "WCI-2026-98124",
                productName = "København Modular 3-Seater Sofa (Oat Boucle)",
                purchaseDate = "June 14, 2026",
                warrantyYears = 10,
                status = "Active – Covered under WCI Master Atelier 10-Yr Guarantee"
            )
        )
        // Insert sample order
        repository.saveOrder(
            OrderEntity(
                orderId = "WCI-ORD-88219",
                productIdsJson = "[\"WCI-SOFA-01\"]",
                totalAmount = 2539.0,
                orderDate = "Sept 21, 2026",
                status = "OUT_FOR_DELIVERY",
                isExpress = true,
                timeSlot = "Today, 2:00 PM - 5:00 PM",
                address = "742 Evergreen Terrace, San Francisco, CA"
            )
        )
    }

    // Filter updates
    fun setCategory(category: ProductCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setRoomType(roomType: RoomType) {
        _uiState.update { it.copy(selectedRoomType = roomType) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFastShipOnly() {
        _uiState.update { it.copy(fastShipOnly = !it.fastShipOnly) }
    }

    fun setCurrency(currency: String, rate: Double) {
        _uiState.update { it.copy(selectedCurrency = currency, currencyMultiplier = rate) }
    }

    fun toggleOfflineMode() {
        _uiState.update { it.copy(isOfflineMode = !it.isOfflineMode) }
    }

    // Cart actions
    fun addToCart(product: Product, swatch: SwatchOption, assembly: Boolean = false) {
        viewModelScope.launch {
            repository.addToCart(product, swatch, assembly)
        }
    }

    fun removeCartItem(item: CartItemEntity) {
        viewModelScope.launch {
            repository.removeCartItem(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            val isWish = wishlistItems.value.any { it.productId == productId }
            repository.toggleWishlist(productId, isWish)
        }
    }

    // AR controls
    fun setArProduct(productId: String) {
        _uiState.update { it.copy(arSelectedProductId = productId) }
    }

    fun setArScale(scale: Float) {
        _uiState.update { it.copy(arItemScale = scale.coerceIn(0.5f, 2.0f)) }
    }

    fun setArRotation(rot: Float) {
        _uiState.update { it.copy(arItemRotation = (rot % 360f)) }
    }

    fun setArLighting(mode: String) {
        _uiState.update { it.copy(arLightingMode = mode) }
    }

    // AR Measuring
    fun setMeasurePoints(p1: Pair<Float, Float>, p2: Pair<Float, Float>) {
        val dx = p2.first - p1.first
        val dy = p2.second - p1.second
        val distPx = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        // Assume calibration: 1 px ≈ 0.65 cm
        val distanceCm = (distPx * 0.65f).coerceAtLeast(10f)
        val passes = distanceCm >= 80f // standard door opening clearance
        _uiState.update {
            it.copy(
                measureStartPoint = p1,
                measureEndPoint = p2,
                measuredDistanceCm = distanceCm,
                doorClearancePass = passes
            )
        }
    }

    // 3D Room Planner operations
    fun addBlueprintItem(product: Product) {
        val newItem = PlacedBlueprintItem(
            id = "ITEM-${System.currentTimeMillis()}",
            productId = product.id,
            productName = product.name,
            x = (_currentBlueprint.value.widthMeters / 2) - 0.5f,
            y = (_currentBlueprint.value.lengthMeters / 2) - 0.5f,
            widthMeters = (product.widthCm / 100f),
            depthMeters = (product.depthCm / 100f),
            rotationDegrees = 0f
        )
        val updated = _currentBlueprint.value.items + newItem
        val cost = updated.sumOf { item ->
            repository.getProductById(item.productId)?.price ?: 0.0
        }
        _currentBlueprint.value = _currentBlueprint.value.copy(
            items = updated,
            estimatedTotalCost = cost
        )
    }

    fun moveBlueprintItem(itemId: String, newX: Float, newY: Float) {
        val updated = _currentBlueprint.value.items.map {
            if (it.id == itemId) {
                it.copy(
                    x = newX.coerceIn(0.1f, _currentBlueprint.value.widthMeters - it.widthMeters),
                    y = newY.coerceIn(0.1f, _currentBlueprint.value.lengthMeters - it.depthMeters)
                )
            } else it
        }
        _currentBlueprint.value = _currentBlueprint.value.copy(items = updated)
    }

    fun rotateBlueprintItem(itemId: String) {
        val updated = _currentBlueprint.value.items.map {
            if (it.id == itemId) {
                it.copy(rotationDegrees = (it.rotationDegrees + 45f) % 360f)
            } else it
        }
        _currentBlueprint.value = _currentBlueprint.value.copy(items = updated)
    }

    fun removeBlueprintItem(itemId: String) {
        val updated = _currentBlueprint.value.items.filterNot { it.id == itemId }
        val cost = updated.sumOf { item ->
            repository.getProductById(item.productId)?.price ?: 0.0
        }
        _currentBlueprint.value = _currentBlueprint.value.copy(
            items = updated,
            estimatedTotalCost = cost
        )
    }

    fun setRoomDimensions(width: Float, length: Float) {
        _currentBlueprint.value = _currentBlueprint.value.copy(
            widthMeters = width.coerceIn(2.5f, 12f),
            lengthMeters = length.coerceIn(2.5f, 12f)
        )
    }

    fun setFlooring(flooring: String) {
        _currentBlueprint.value = _currentBlueprint.value.copy(flooringType = flooring)
    }

    // Smart Furniture Controls
    fun setSmartDeskHeight(height: Int) {
        _uiState.update { it.copy(smartDeskHeightCm = height.coerceIn(65, 125)) }
    }

    fun setSmartBedLighting(brightness: Float, tempK: Int) {
        _uiState.update { it.copy(smartBedUnderglowBrightness = brightness, smartBedColorTempK = tempK) }
    }

    // Chat sending
    fun sendChatMessage(msg: String) {
        if (msg.isBlank()) return
        val current = _chatMessages.value.toMutableList()
        current.add(msg to true)
        _chatMessages.value = current

        // Simulated instant architect response
        viewModelScope.launch {
            delay(1200)
            val reply = when {
                msg.contains("call", ignoreCase = true) || msg.contains("phone", ignoreCase = true) ||
                msg.contains("number", ignoreCase = true) || msg.contains("whatsapp", ignoreCase = true) ||
                msg.contains("contact", ignoreCase = true) || msg.contains("support", ignoreCase = true) ||
                msg.contains("baat", ignoreCase = true) || msg.contains("dial", ignoreCase = true) ||
                msg.contains("7320054330") || msg.contains("9572349911") ->
                    "You can reach our team immediately! Call directly at +91 7320054330, or message us on WhatsApp at +91 9572349911. You can also tap the direct Call or WhatsApp button above to connect in one tap!"
                msg.contains("shipping", ignoreCase = true) || msg.contains("deliver", ignoreCase = true) ->
                    "We offer complimentary white-glove inside placement and packaging removal on all orders above $1,500. Standard delivery window is scheduled directly in your app calendar."
                msg.contains("wood", ignoreCase = true) || msg.contains("material", ignoreCase = true) ->
                    "Every timber piece uses FSC-certified American Walnut or European White Oak, hand-treated with natural VOC-free plant wax oil for lasting heirloom patina."
                msg.contains("measure", ignoreCase = true) || msg.contains("size", ignoreCase = true) ->
                    "You can test exact room clearances anytime using our in-app AR Measuring Tape tool in the AR tab! It even evaluates doorway pivot passes."
                else ->
                    "That sounds wonderful for your aesthetic! Would you like me to curate a personalized 3D room moodboard with matching swatches sent to your address?"
            }
            val updated = _chatMessages.value.toMutableList()
            updated.add(reply to false)
            _chatMessages.value = updated
        }
    }

    // Checkout order submission
    fun placeOrder(
        cartItemList: List<CartItemEntity>,
        total: Double,
        address: String,
        isExpress: Boolean,
        timeSlot: String
    ): String {
        val newOrderId = "WCI-ORD-${(10000..99999).random()}"
        viewModelScope.launch {
            repository.saveOrder(
                OrderEntity(
                    orderId = newOrderId,
                    productIdsJson = cartItemList.joinToString(",") { it.productId },
                    totalAmount = total,
                    orderDate = "Sept 23, 2026",
                    status = "CONFIRMED",
                    isExpress = isExpress,
                    timeSlot = timeSlot,
                    address = address
                )
            )
            repository.clearCart()
        }
        return newOrderId
    }

    fun applyPromoCode(code: String): Boolean {
        return if (code.trim().equals("WCIATELIER", ignoreCase = true)) {
            _uiState.update { it.copy(promoCode = "WCIATELIER", promoDiscountPct = 10) }
            true
        } else if (code.trim().equals("VIPTRADE", ignoreCase = true)) {
            _uiState.update { it.copy(promoCode = "VIPTRADE", promoDiscountPct = 20) }
            true
        } else {
            false
        }
    }

    // Style Profile management
    fun saveStyleProfile(profile: UserStyleProfile) {
        repository.saveUserStyleProfile(profile)
        _uiState.update { it.copy(userStyleProfile = profile) }
    }

    fun clearStyleProfile() {
        repository.clearUserStyleProfile()
        _uiState.update { it.copy(userStyleProfile = null) }
    }

    fun getPersonalizedRecommendations(): List<Product> {
        val profile = _uiState.value.userStyleProfile
        val all = repository.catalogProducts
        return if (profile != null) {
            all.sortedByDescending { prod ->
                var score = 0
                if (prod.styles.contains(profile.primaryStyle)) score += 100
                if (profile.secondaryStyle != null && prod.styles.contains(profile.secondaryStyle)) score += 50
                if (profile.preferredMaterials.any { mat -> prod.material.contains(mat, ignoreCase = true) }) score += 25
                score
            }
        } else {
            all.sortedByDescending { it.rating }
        }
    }

    fun getPersonalizedMoodboards(): List<MoodboardLook> {
        val profile = _uiState.value.userStyleProfile
        val all = repository.moodboards
        return if (profile != null) {
            all.sortedByDescending { look ->
                if (look.matchingStyle == profile.primaryStyle) 100
                else if (look.matchingStyle == profile.secondaryStyle) 50
                else 0
            }
        } else {
            all
        }
    }

    init {
        // Add published seller products to catalog
        _sellerListings.value.filter { it.status == ProductListingStatus.PUBLISHED }.forEach { listing ->
            val prod = convertSellerListingToProduct(listing)
            repository.addSellerProduct(prod)
        }
    }

    private fun convertSellerListingToProduct(listing: SellerProductListing): Product {
        return Product(
            id = listing.id,
            name = listing.title,
            subtitle = "${listing.material} • ${listing.color}",
            price = listing.discountedPrice,
            originalPrice = listing.originalPrice,
            category = listing.category,
            roomType = when (listing.category) {
                ProductCategory.SEATING, ProductCategory.TABLES -> RoomType.LIVING_ROOM
                ProductCategory.BEDROOM -> RoomType.BEDROOM
                ProductCategory.STORAGE -> RoomType.LIVING_ROOM
                else -> RoomType.ALL
            },
            material = listing.material,
            color = listing.color,
            dimensions = listing.dimensions.ifBlank { "${listing.widthCm.toInt()}\"W x ${listing.depthCm.toInt()}\"D x ${listing.heightCm.toInt()}\"H" },
            widthCm = listing.widthCm,
            depthCm = listing.depthCm,
            heightCm = listing.heightCm,
            weightKg = 42f,
            rating = 5.0f,
            reviewCount = 1,
            isFastShipEligible = true,
            sustainabilityScore = 96,
            ecoFriendlyBadge = "Atelier Verified • FSC Sustainably Harvested",
            carbonOffsetKg = 38,
            fscCertifiedWood = true,
            description = listing.description,
            specifications = mapOf(
                "Material" to listing.material,
                "Dimensions" to listing.dimensions,
                "Finish" to listing.color,
                "SKU" to listing.sku,
                "Stock Availability" to "${listing.stockCount} units in atelier inventory"
            ),
            imageResName = listing.sampleDrawableRes,
            availableSwatches = listOf(
                SwatchOption(listing.color, 0xFF8C7E72, listing.material, inStock = true)
            ),
            stockCount = listing.stockCount,
            barcode = listing.barcode,
            listingStatus = ProductListingStatus.PUBLISHED,
            multiAnglePhotos = listing.multiAnglePhotos,
            interactive360 = listing.interactive360,
            videoWalkthrough = listing.videoWalkthrough
        )
    }

    fun setSellerWizardStep(step: Int) {
        _sellerWizardStep.value = step.coerceIn(0, 4)
    }

    fun updateListingDraft(update: (SellerProductListing) -> SellerProductListing) {
        _activeListingDraft.update { current ->
            val updated = update(current)
            val orig = updated.originalPrice
            val disc = updated.discountedPrice
            val pct = if (orig > 0 && disc > 0 && orig > disc) {
                (((orig - disc) / orig) * 100).toInt()
            } else 0
            val badge = if (pct > 0) "$pct% OFF" else ""
            updated.copy(discountPercent = pct, discountBadgeText = badge)
        }
    }

    fun resetListingDraft() {
        _activeListingDraft.value = createInitialListingDraft()
        _sellerWizardStep.value = 0
    }

    fun applySampleListingPreset(presetType: String = "credenza") {
        when (presetType) {
            "chair" -> {
                _activeListingDraft.value = SellerProductListing(
                    id = "SELLER-" + (1000..9999).random(),
                    title = "Nordic Curved Armchair in Boucle",
                    category = ProductCategory.SEATING,
                    material = "Kiln-Dried Hardwood & Tactile Boucle",
                    dimensions = "32\"W x 34\"D x 31\"H (81 x 86 x 79 cm)",
                    widthCm = 81f,
                    depthCm = 86f,
                    heightCm = 79f,
                    color = "Warm Oat Cream",
                    originalPrice = 1200.0,
                    discountedPrice = 960.0,
                    discountPercent = 20,
                    discountBadgeText = "20% OFF",
                    stockCount = 8,
                    lowStockThreshold = 2,
                    sku = "WCI-CHR-882",
                    status = ProductListingStatus.DRAFT,
                    sampleDrawableRes = "sofa_nordic",
                    multiAnglePhotos = MultiAnglePhotos(
                        frontUri = "preset://sofa_nordic",
                        backUri = "preset://sofa_nordic_back",
                        detailUri = "preset://sofa_nordic_detail"
                    ),
                    interactive360 = Interactive360Data(
                        frameCount = 24,
                        samplePresetName = "sofa_nordic",
                        aiLightingQualityScore = 98,
                        aiSmoothnessScore = 96
                    ),
                    videoWalkthrough = VideoWalkthroughData(
                        durationSec = 20,
                        hotspots = listOf(
                            WalkthroughHotspot("h1", 2, "Boucle Weave", "35,000 double-rub tactile heavy boucle weave", 45f, 40f),
                            WalkthroughHotspot("h2", 9, "Ergonomic Lumbar", "Continuous steam-formed lumbar contour support", 50f, 60f)
                        )
                    ),
                    description = "Sculptural organic silhouette featuring plush cocooning comfort, wrapped in stain-resistant Scandinavian boucle."
                )
            }
            else -> {
                _activeListingDraft.value = SellerProductListing(
                    id = "SELLER-" + (1000..9999).random(),
                    title = "Kyoto Architectural Oak Credenza",
                    category = ProductCategory.STORAGE,
                    material = "Solid White Oak, Smoked Cane Webbing & Patinated Brass",
                    dimensions = "68\"W x 20\"D x 32\"H (173 x 51 x 81 cm)",
                    widthCm = 173f,
                    depthCm = 51f,
                    heightCm = 81f,
                    color = "Natural Bleached White Oak",
                    originalPrice = 2200.0,
                    discountedPrice = 1760.0,
                    discountPercent = 20,
                    discountBadgeText = "SAVE $440 • 20% OFF",
                    stockCount = 10,
                    lowStockThreshold = 3,
                    sku = "WCI-CRD-491",
                    status = ProductListingStatus.DRAFT,
                    sampleDrawableRes = "table_travertine",
                    multiAnglePhotos = MultiAnglePhotos(
                        frontUri = "preset://table_travertine",
                        backUri = "preset://table_travertine_rear",
                        detailUri = "preset://table_travertine_detail"
                    ),
                    interactive360 = Interactive360Data(
                        frameCount = 24,
                        samplePresetName = "table_travertine",
                        aiLightingQualityScore = 99,
                        aiSmoothnessScore = 98
                    ),
                    videoWalkthrough = VideoWalkthroughData(
                        durationSec = 24,
                        hotspots = listOf(
                            WalkthroughHotspot("h1", 3, "Hand-Woven Cane Doors", "Breathable natural rattan cane woven panels", 35f, 45f),
                            WalkthroughHotspot("h2", 10, "Soft-Close German Slides", "Concealed under-mount Austrian hardware with push-latch", 60f, 55f),
                            WalkthroughHotspot("h3", 16, "Waterfall Mitred Joints", "Continuous wood grain waterfall edge across all corners", 50f, 25f)
                        )
                    ),
                    description = "Architectural credenza with floating silhouette, acoustic damping tambour doors, and integrated cable routing for media and audio components."
                )
            }
        }
    }

    fun generateAiListingCopy(category: ProductCategory, material: String, color: String) {
        val suggestedTitle = when (category) {
            ProductCategory.SEATING -> "Atelier Sculptural Seating in $material"
            ProductCategory.TABLES -> "Architectural Monolith Table in $material"
            ProductCategory.STORAGE -> "Bespoke Low Credenza in $material"
            ProductCategory.BEDROOM -> "Sanctuary Platform Bed in $material"
            ProductCategory.LIGHTING_DECOR -> "Brutalist Ambient Lighting in $material"
            ProductCategory.ARTISAN -> "Master Artisan Handcrafted Piece in $material"
        }
        val suggestedDesc = "Hand-built in limited atelier runs using authentic $material finished in rich $color tones. Features precision mortise-and-tenon construction, hand-rubbed organic wax finish, and timeless heirloom durability."
        _activeListingDraft.update { current ->
            current.copy(
                title = if (current.title.isBlank()) suggestedTitle else current.title,
                description = if (current.description.isBlank()) suggestedDesc else current.description
            )
        }
    }

    fun publishProductListing(listing: SellerProductListing): Product {
        val publishedListing = listing.copy(
            status = ProductListingStatus.PUBLISHED,
            discountPercent = if (listing.originalPrice > listing.discountedPrice && listing.originalPrice > 0) {
                (((listing.originalPrice - listing.discountedPrice) / listing.originalPrice) * 100).toInt()
            } else 0,
            discountBadgeText = if (listing.originalPrice > listing.discountedPrice && listing.originalPrice > 0) {
                "${(((listing.originalPrice - listing.discountedPrice) / listing.originalPrice) * 100).toInt()}% OFF"
            } else ""
        )

        _sellerListings.update { currentList ->
            val index = currentList.indexOfFirst { it.id == publishedListing.id }
            if (index >= 0) {
                currentList.toMutableList().apply { set(index, publishedListing) }
            } else {
                listOf(publishedListing) + currentList
            }
        }

        val newProduct = convertSellerListingToProduct(publishedListing)
        repository.addSellerProduct(newProduct)

        _customProducts.update { current ->
            listOf(newProduct) + current.filterNot { it.id == newProduct.id }
        }

        _activeListingDraft.value = publishedListing
        return newProduct
    }

    fun updateStockCount(listingId: String, delta: Int) {
        _sellerListings.update { currentList ->
            currentList.map { listing ->
                if (listing.id == listingId) {
                    val updatedCount = (listing.stockCount + delta).coerceAtLeast(0)
                    listing.copy(stockCount = updatedCount)
                } else listing
            }
        }
        _customProducts.update { current ->
            current.map { prod ->
                if (prod.id == listingId) {
                    val updatedCount = (prod.stockCount + delta).coerceAtLeast(0)
                    prod.copy(stockCount = updatedCount)
                } else prod
            }
        }
    }

    fun setStockDirectly(listingId: String, newCount: Int) {
        val safeCount = newCount.coerceAtLeast(0)
        _sellerListings.update { currentList ->
            currentList.map { listing ->
                if (listing.id == listingId) {
                    listing.copy(stockCount = safeCount)
                } else listing
            }
        }
        _customProducts.update { current ->
            current.map { prod ->
                if (prod.id == listingId) {
                    prod.copy(stockCount = safeCount)
                } else prod
            }
        }
    }

    fun toggleListingStatus(listingId: String) {
        _sellerListings.update { currentList ->
            currentList.map { listing ->
                if (listing.id == listingId) {
                    val nextStatus = if (listing.status == ProductListingStatus.PUBLISHED) {
                        ProductListingStatus.DRAFT
                    } else {
                        ProductListingStatus.PUBLISHED
                    }
                    listing.copy(status = nextStatus)
                } else listing
            }
        }
    }

    fun getProductOrCustom(id: String): Product? {
        return _customProducts.value.find { it.id == id } ?: repository.getProductById(id)
    }
}
