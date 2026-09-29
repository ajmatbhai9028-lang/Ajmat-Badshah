package com.example.data.model

data class Product(
    val id: String,
    val name: String,
    val subtitle: String,
    val price: Double,
    val originalPrice: Double,
    val category: ProductCategory,
    val roomType: RoomType,
    val material: String,
    val color: String,
    val dimensions: String, // e.g. "88\"W x 38\"D x 32\"H (224 x 96 x 81 cm)"
    val widthCm: Float,
    val depthCm: Float,
    val heightCm: Float,
    val weightKg: Float,
    val rating: Float,
    val reviewCount: Int,
    val isFastShipEligible: Boolean,
    val sustainabilityScore: Int, // 0-100
    val ecoFriendlyBadge: String,
    val carbonOffsetKg: Int,
    val fscCertifiedWood: Boolean,
    val description: String,
    val specifications: Map<String, String>,
    val imageResName: String,
    val availableSwatches: List<SwatchOption>,
    val stockCount: Int,
    val isPreOrder: Boolean = false,
    val isArtisanHandcrafted: Boolean = false,
    val isModular: Boolean = false,
    val groupBuyActive: Boolean = false,
    val groupBuyCurrentCount: Int = 0,
    val groupBuyTargetCount: Int = 5,
    val barcode: String,
    val qrManualUrl: String = "wci://manuals/assembly/",
    val styles: List<InteriorStyle> = emptyList(),
    val listingStatus: ProductListingStatus = ProductListingStatus.PUBLISHED,
    val multiAnglePhotos: MultiAnglePhotos? = null,
    val interactive360: Interactive360Data? = null,
    val videoWalkthrough: VideoWalkthroughData? = null
)

enum class InteriorStyle(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val keyMaterials: List<String>,
    val colorPaletteName: String,
    val colorHexes: List<Long>,
    val imageResName: String,
    val designAdvice: String
) {
    MODERN(
        id = "MODERN",
        title = "Modern Minimalist",
        subtitle = "Clean architectural lines, neutral harmony & sculptural purity",
        description = "Your style celebrates sleek silhouettes, functional elegance, and tactile organic neutral palettes. You value uncluttered spaces where sculptural furniture pieces become quiet works of art.",
        keyMaterials = listOf("Honed Travertine", "Bleached White Oak", "Boucle Fabric", "Brushed Brass"),
        colorPaletteName = "Warm Atelier Neutrals",
        colorHexes = listOf(0xFFEDE7DF, 0xFFD6CEBF, 0xFF8C7E72, 0xFF4A3B32),
        imageResName = "img_style_modern",
        designAdvice = "Anchor with low-slung horizontal profiles and allow negative space to breathe. Use warm 2700K lighting to soften geometric edges."
    ),
    BOHEMIAN(
        id = "BOHEMIAN",
        title = "Earthy Bohemian",
        subtitle = "Layered organic textures, botanical warmth & relaxed luxury",
        description = "Your aesthetic is free-spirited, inviting, and steeped in nature. You love textural layering—rattan, textured linens, ceramic pots, and earthy terracotta that bring warmth to every corner.",
        keyMaterials = listOf("Handwoven Rattan", "Belgian Linen", "Unglazed Terracotta", "Raw Teak"),
        colorPaletteName = "Sun-Baked Earth & Terracotta",
        colorHexes = listOf(0xFFB8664D, 0xFFD89D6A, 0xFF637059, 0xFFF2ECE1),
        imageResName = "img_style_bohemian",
        designAdvice = "Mix woven natural fibers with living houseplants. Layer tactile rugs and soft organic throws for an effortlessly collected sanctuary."
    ),
    TRADITIONAL(
        id = "TRADITIONAL",
        title = "Curated Traditional",
        subtitle = "Heirloom craftsmanship, deep walnut & timeless refinement",
        description = "You cherish enduring heritage, classic proportions, and artisanal mastery. Solid dark woods, hand-tailored leather, and stately symmetry anchor your sophisticated sanctuary.",
        keyMaterials = listOf("American Black Walnut", "Aniline Leather", "Solid Brass", "Fine Wool"),
        colorPaletteName = "Heritage Cognac & Deep Timber",
        colorHexes = listOf(0xFF2A2421, 0xFFB36738, 0xFF855836, 0xFFE0D8CB),
        imageResName = "img_style_traditional",
        designAdvice = "Highlight classical joinery, tailored upholstery with refined piping, and ambient shaded lamps for an intimate, historic atmosphere."
    ),
    INDUSTRIAL(
        id = "INDUSTRIAL",
        title = "Architectural Industrial",
        subtitle = "Raw exposed metals, smoked timber & utilitarian grandeur",
        description = "You are drawn to urban loft heritage, structural honesty, and tactile durability. You admire blackened steel frames, salvaged rough-sawn timber, and bold architectural statements.",
        keyMaterials = listOf("Blackened Gunmetal Steel", "Smoked Ash Wood", "Aged Aniline Leather", "Cast Concrete"),
        colorPaletteName = "Loft Gunmetal & Smoked Ash",
        colorHexes = listOf(0xFF1F1E1D, 0xFF3D3A37, 0xFF7A7D82, 0xFFB0A89F),
        imageResName = "img_style_industrial",
        designAdvice = "Balance bold metallic and dark timber pieces with soft wool rugs and tactile throw cushions to maintain comfortable warmth."
    )
}

data class UserStyleProfile(
    val primaryStyle: InteriorStyle,
    val secondaryStyle: InteriorStyle? = null,
    val styleScores: Map<InteriorStyle, Int> = emptyMap(), // Percentages e.g. MODERN -> 65
    val preferredMaterials: List<String> = emptyList(),
    val preferredPalette: String = "",
    val spaceGoal: String = "",
    val completedTimestamp: Long = System.currentTimeMillis()
)

enum class ProductCategory(val displayName: String) {
    SEATING("Seating & Sofas"),
    TABLES("Tables & Desks"),
    STORAGE("Cabinets & Shelving"),
    BEDROOM("Beds & Nightstands"),
    LIGHTING_DECOR("Lighting & Decor"),
    ARTISAN("Artisan Crafted")
}

enum class RoomType(val displayName: String) {
    ALL("All Rooms"),
    LIVING_ROOM("Living Room"),
    DINING_ROOM("Dining Room"),
    BEDROOM("Bedroom"),
    HOME_OFFICE("Home Office"),
    OUTDOOR("Patio & Terrace")
}

data class SwatchOption(
    val name: String,
    val hexColor: Long,
    val materialType: String, // "Boucle Fabric", "Italian Aniline Leather", "Solid American Walnut"
    val inStock: Boolean = true
)

data class Review(
    val id: String,
    val author: String,
    val rating: Int,
    val date: String,
    val title: String,
    val comment: String,
    val verifiedPurchase: Boolean = true,
    val helpfulCount: Int = 12
)

data class QuestionAnswer(
    val id: String,
    val question: String,
    val askedBy: String,
    val answer: String,
    val answeredBy: String,
    val date: String
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1,
    val selectedSwatch: SwatchOption,
    val assemblyBooked: Boolean = false,
    val destinationAddress: String = "Default Residence"
)

data class Order(
    val orderId: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val orderDate: String,
    val status: OrderStatus,
    val isExpress: Boolean,
    val deliveryTimeSlot: String,
    val shippingAddress: String,
    val liveDriverName: String = "Marcus Vance",
    val driverPhone: String = "+1 (555) 382-9901",
    val vehicleGpsLat: Double = 37.7749,
    val vehicleGpsLng: Double = -122.4194,
    val etaMinutes: Int = 38,
    val isCustomOrder: Boolean = false,
    val customOrderStage: CustomProductionStage = CustomProductionStage.COMPLETED,
    val canModify: Boolean = true,
    val insuranceActive: Boolean = true
)

enum class OrderStatus(val title: String, val stepIndex: Int) {
    CONFIRMED("Order Confirmed", 0),
    IN_PRODUCTION("In Atelier Production", 1),
    DISPATCHED("Dispatched from Warehouse", 2),
    OUT_FOR_DELIVERY("Out for Delivery", 3),
    DELIVERED("Delivered & Assembled", 4)
}

enum class CustomProductionStage(val stageName: String, val description: String) {
    TIMBER_SELECT("Wood Selection", "Selecting kiln-dried FSC-certified American Walnut"),
    JOINERY("Atelier Joinery", "Traditional mortise and tenon precision crafting"),
    UPHOLSTERY("Fabric & Foam Fit", "Hand-tailoring selected boucle and high-density core"),
    HAND_FINISH("Organic Wax Buffing", "Applying hand-rubbed non-toxic matte hardwax oil"),
    QUALITY_INSPECTION("Master Craftsman QC", "Laser alignment and structural stress verification"),
    COMPLETED("Ready for White-Glove Dispatch", "Packaged in reusable protective cotton blankets")
}

data class PlacedBlueprintItem(
    val id: String,
    val productId: String,
    val productName: String,
    val x: Float, // Relative in room meters (0..width)
    val y: Float, // Relative in room meters (0..length)
    val widthMeters: Float,
    val depthMeters: Float,
    val rotationDegrees: Float = 0f,
    val colorHex: Long = 0xFF4A3B32
)

data class RoomBlueprint(
    val id: String,
    val name: String,
    val roomType: RoomType,
    val widthMeters: Float = 5.0f,
    val lengthMeters: Float = 4.2f,
    val wallColorHex: Long = 0xFFF7F5F0,
    val flooringType: String = "Herringbone Oak",
    val items: List<PlacedBlueprintItem> = emptyList(),
    val estimatedTotalCost: Double = 0.0
)

data class MoodboardLook(
    val id: String,
    val title: String,
    val designer: String,
    val style: String,
    val description: String,
    val imageResName: String,
    val bundledProductIds: List<String>,
    val bundleDiscountPct: Int = 15,
    val matchingStyle: InteriorStyle? = null
)

data class WarrantyRecord(
    val serialNumber: String,
    val productName: String,
    val purchaseDate: String,
    val warrantyYears: Int = 10,
    val status: String = "Active (Verified)",
    val claimsSubmitted: Int = 0
)

data class ConsultationSlot(
    val id: String,
    val consultantName: String,
    val role: String,
    val specialty: String,
    val date: String,
    val timeSlot: String,
    val isVideo: Boolean = true
)

enum class ProductListingStatus(val label: String, val badgeColorHex: Long) {
    DRAFT("Draft", 0xFF8C7E72),
    IN_REVIEW("In Review", 0xFFD89D6A),
    PUBLISHED("Published", 0xFF2E6930),
    ARCHIVED("Archived", 0xFF544941)
}

data class MultiAnglePhotos(
    val frontUri: String? = null,
    val backUri: String? = null,
    val detailUri: String? = null,
    val sideUri: String? = null,
    val lifestyleUri: String? = null
) {
    val totalUploaded: Int
        get() = listOfNotNull(frontUri, backUri, detailUri, sideUri, lifestyleUri).size

    val hasRequiredAngles: Boolean
        get() = !frontUri.isNullOrBlank() && !backUri.isNullOrBlank() && !detailUri.isNullOrBlank()
}

data class Interactive360Data(
    val frameCount: Int = 24,
    val rotationalAngle: Float = 0f,
    val isAutoRotate: Boolean = false,
    val aiLightingQualityScore: Int = 98,
    val aiSmoothnessScore: Int = 96,
    val customFrameUris: List<String> = emptyList(),
    val samplePresetName: String = "sofa_nordic"
)

data class WalkthroughHotspot(
    val id: String,
    val timestampSec: Int,
    val title: String,
    val description: String,
    val xPercent: Float = 50f,
    val yPercent: Float = 50f
)

data class VideoWalkthroughData(
    val videoUri: String? = null,
    val videoTitle: String = "Craftsmanship & Detail Walkthrough",
    val durationSec: Int = 28,
    val hotspots: List<WalkthroughHotspot> = emptyList()
)

data class SellerProductListing(
    val id: String,
    val title: String,
    val category: ProductCategory,
    val material: String,
    val dimensions: String,
    val widthCm: Float = 200f,
    val depthCm: Float = 90f,
    val heightCm: Float = 78f,
    val color: String,
    val originalPrice: Double,
    val discountedPrice: Double,
    val discountPercent: Int = 0,
    val discountBadgeText: String = "",
    val stockCount: Int,
    val lowStockThreshold: Int = 3,
    val sku: String,
    val barcode: String = "890" + (100000000..999999999).random(),
    val status: ProductListingStatus = ProductListingStatus.DRAFT,
    val multiAnglePhotos: MultiAnglePhotos = MultiAnglePhotos(),
    val interactive360: Interactive360Data = Interactive360Data(),
    val videoWalkthrough: VideoWalkthroughData = VideoWalkthroughData(),
    val description: String = "",
    val sampleDrawableRes: String = "sofa_nordic",
    val viewsCount: Int = 0,
    val ordersCount: Int = 0,
    val createdTimestamp: Long = System.currentTimeMillis()
)

