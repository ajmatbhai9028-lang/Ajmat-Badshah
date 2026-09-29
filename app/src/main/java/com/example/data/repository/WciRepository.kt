package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WciRepository(private val context: Context) {
    private val database = WciDatabase.getDatabase(context)
    private val dao = database.wciDao()

    val catalogProducts: List<Product> = listOf(
        Product(
            id = "WCI-SOFA-01",
            name = "København Modular 3-Seater Sofa",
            subtitle = "Customizable FSC-Certified Oak & Boucle",
            price = 2450.0,
            originalPrice = 2890.0,
            category = ProductCategory.SEATING,
            roomType = RoomType.LIVING_ROOM,
            material = "Kiln-dried White Oak & Heavy Boucle Fabric",
            color = "Warm Oat Cream",
            dimensions = "90\"W x 38\"D x 31\"H (228 x 96 x 79 cm)",
            widthCm = 228f,
            depthCm = 96f,
            heightCm = 79f,
            weightKg = 68f,
            rating = 4.9f,
            reviewCount = 142,
            isFastShipEligible = true,
            sustainabilityScore = 98,
            ecoFriendlyBadge = "100% FSC Certified & OEKO-TEX",
            carbonOffsetKg = 42,
            fscCertifiedWood = true,
            description = "Sculpted with serene Scandinavian lines, the København combines deep lounging comfort with architectural elegance. Hand-built with sustainably harvested kiln-dried white oak and upholstered in tactile, stain-guarded textured boucle.",
            specifications = mapOf(
                "Seat Depth" to "26 inches (66 cm)",
                "Frame" to "Solid White Oak with Mortise-and-Tenon Joinery",
                "Cushion Fill" to "High-resiliency foam core wrapped in cruelty-free down alternative",
                "Suspension" to "Heavy-gauge sinuous springs",
                "Care" to "Vacuum with soft brush attachment; spot clean with water-free solvent"
            ),
            imageResName = "sofa_nordic",
            availableSwatches = listOf(
                SwatchOption("Warm Oat Boucle", 0xFFEDE7DF, "Boucle"),
                SwatchOption("Nordic Slate Gray", 0xFF7A7D82, "Wool Blend"),
                SwatchOption("Forest Moss Velvet", 0xFF435848, "Performance Velvet"),
                SwatchOption("Cognac Terracotta", 0xFFA8634B, "Aniline Leather")
            ),
            stockCount = 18,
            isModular = true,
            groupBuyActive = true,
            groupBuyCurrentCount = 3,
            groupBuyTargetCount = 5,
            barcode = "890412094401",
            styles = listOf(InteriorStyle.MODERN)
        ),
        Product(
            id = "WCI-CHAIR-02",
            name = "Stockholm Atelier Lounge Chair & Ottoman",
            subtitle = "Hand-Molded Walnut Shell with Cognac Aniline Leather",
            price = 1680.0,
            originalPrice = 1950.0,
            category = ProductCategory.SEATING,
            roomType = RoomType.LIVING_ROOM,
            material = "7-ply Walnut Veneer & Top-Grain Aniline Leather",
            color = "Cognac Amber",
            dimensions = "33\"W x 34\"D x 33\"H (84 x 86 x 84 cm)",
            widthCm = 84f,
            depthCm = 86f,
            heightCm = 84f,
            weightKg = 34f,
            rating = 4.95f,
            reviewCount = 98,
            isFastShipEligible = true,
            sustainabilityScore = 94,
            ecoFriendlyBadge = "Zero VOC Organic Oil Finish",
            carbonOffsetKg = 28,
            fscCertifiedWood = true,
            description = "An icon of ergonomic balance and quiet grandeur. Seven layers of hand-pressed American walnut cradle premium glove-soft Italian leather, calibrated at an optimal 15-degree recline for timeless restorative relaxation.",
            specifications = mapOf(
                "Recline Angle" to "Fixed 15° ergonomic lounge tilt",
                "Base Mechanism" to "Die-cast brushed black aluminum 360° swivel",
                "Leather Origin" to "Full-grain Tuscany aniline leather",
                "Assembly" to "Ottoman ships assembled; 2-bolt chair base setup"
            ),
            imageResName = "armchair_cognac",
            availableSwatches = listOf(
                SwatchOption("Cognac Amber", 0xFFB36738, "Italian Aniline"),
                SwatchOption("Espresso Noir", 0xFF2A2421, "Italian Aniline"),
                SwatchOption("Camel Saddle", 0xFFC99564, "Top-Grain"),
                SwatchOption("Cream Alabaster", 0xFFF2ECE1, "Full Grain")
            ),
            stockCount = 9,
            barcode = "890412094402",
            styles = listOf(InteriorStyle.MODERN, InteriorStyle.TRADITIONAL)
        ),
        Product(
            id = "WCI-TABLE-03",
            name = "Tivoli Fluted Travertine Dining Table",
            subtitle = "Honed Italian Travertine on Solid Walnut Pedestal",
            price = 3100.0,
            originalPrice = 3450.0,
            category = ProductCategory.TABLES,
            roomType = RoomType.DINING_ROOM,
            material = "Natural Roman Travertine Stone & Solid American Walnut",
            color = "Warm Ivory Travertine",
            dimensions = "55\"Dia x 30\"H (140 x 140 x 76 cm)",
            widthCm = 140f,
            depthCm = 140f,
            heightCm = 76f,
            weightKg = 92f,
            rating = 4.88f,
            reviewCount = 64,
            isFastShipEligible = false,
            sustainabilityScore = 91,
            ecoFriendlyBadge = "Quarried with Carbon-Neutral Logistics",
            carbonOffsetKg = 65,
            fscCertifiedWood = true,
            description = "Sculptural drama meets tactile stonework. A bullnose honed travertine top sits upon a ribbed architectural walnut pedestal, comfortably seating up to 6 guests for intimate dining.",
            specifications = mapOf(
                "Seating Capacity" to "Comfortably seats 6 adults",
                "Tabletop Thickness" to "1.25 inches (3.2 cm) solid sealed stone",
                "Stone Sealant" to "Food-safe penetrating matte sealer",
                "Weight" to "Pedestal 42 kg + Stone top 50 kg"
            ),
            imageResName = "table_travertine",
            availableSwatches = listOf(
                SwatchOption("Honed Travertine", 0xFFE0D8CB, "Natural Stone"),
                SwatchOption("Nero Marquina", 0xFF2B2B2C, "Spanish Marble"),
                SwatchOption("Carrara White", 0xFFF0EFEF, "Italian Marble")
            ),
            stockCount = 5,
            barcode = "890412094403",
            styles = listOf(InteriorStyle.MODERN, InteriorStyle.TRADITIONAL)
        ),
        Product(
            id = "WCI-LAMP-04",
            name = "Kyoto Wabi-Sabi Ceramic Lamp",
            subtitle = "Hand-thrown Clay Base with Textured Linen Cone Shade",
            price = 340.0,
            originalPrice = 410.0,
            category = ProductCategory.LIGHTING_DECOR,
            roomType = RoomType.ALL,
            material = "Unglazed Stoneware Ceramic & French Linen",
            color = "Matte Chalk Cream",
            dimensions = "13\"Dia x 21\"H (33 x 33 x 53 cm)",
            widthCm = 33f,
            depthCm = 33f,
            heightCm = 53f,
            weightKg = 4.5f,
            rating = 4.92f,
            reviewCount = 87,
            isFastShipEligible = true,
            sustainabilityScore = 99,
            ecoFriendlyBadge = "Artisanal Low-Emission Kiln Firing",
            carbonOffsetKg = 12,
            fscCertifiedWood = false,
            description = "Embodying Japanese minimalism and tactile organic textures, each Kyoto lamp is hand-thrown by master potters. Emits a soft, diffused 2700K warm ambient glow ideal for bedside or credenza styling.",
            specifications = mapOf(
                "Bulb" to "Dimmable warm 2700K E26 LED included (450 lumens)",
                "Switch" to "Solid brass rotary dimmer on fabric cord",
                "Cord Length" to "8 ft (2.4 m) twisted brown woven cloth cord",
                "Voltage" to "110-240V universal adapter included"
            ),
            imageResName = "lamp_ceramic",
            availableSwatches = listOf(
                SwatchOption("Matte Chalk Cream", 0xFFEBE6DC, "Ceramic"),
                SwatchOption("Smoked Charcoal", 0xFF353432, "Ceramic"),
                SwatchOption("Terracotta Earth", 0xFFB8664D, "Ceramic")
            ),
            stockCount = 35,
            barcode = "890412094404",
            styles = listOf(InteriorStyle.BOHEMIAN, InteriorStyle.MODERN)
        ),
        Product(
            id = "WCI-BED-05",
            name = "Artemis Heritage Oak Platform Bed",
            subtitle = "Integrated Floating Nightstands & Warm Ambient Headboard Glow",
            price = 2890.0,
            originalPrice = 3200.0,
            category = ProductCategory.BEDROOM,
            roomType = RoomType.BEDROOM,
            material = "Solid European White Oak & Warm LED Integration",
            color = "Natural Bleached Oak",
            dimensions = "88\"W x 92\"L x 36\"H (224 x 234 x 91 cm)",
            widthCm = 224f,
            depthCm = 234f,
            heightCm = 91f,
            weightKg = 86f,
            rating = 4.96f,
            reviewCount = 52,
            isFastShipEligible = true,
            sustainabilityScore = 97,
            ecoFriendlyBadge = "Plant a Tree Program with Every Bed",
            carbonOffsetKg = 55,
            fscCertifiedWood = true,
            description = "A floating architecture centerpiece designed for deep, peaceful slumber. Features a continuous solid oak headboard with concealed downward ambient LED lighting, hidden wireless phone charging pads, and cantilevered nightstands.",
            specifications = mapOf(
                "Mattress Size" to "Standard King (76\" x 80\")",
                "Underbed Clearance" to "7 inches (18 cm) floating aesthetic",
                "Smart Features" to "Dual Qi 15W wireless chargers & touch dimmer",
                "Slat System" to "Curved solid beech wood flexing slats"
            ),
            imageResName = "ic_launcher_wci",
            availableSwatches = listOf(
                SwatchOption("Natural White Oak", 0xFFE6DBC9, "Solid Oak"),
                SwatchOption("Smoked Walnut", 0xFF48372D, "Solid Walnut"),
                SwatchOption("Matte Black Ash", 0xFF1F1E1D, "Ash Wood")
            ),
            stockCount = 7,
            barcode = "890412094405",
            styles = listOf(InteriorStyle.MODERN, InteriorStyle.TRADITIONAL)
        ),
        Product(
            id = "WCI-ARTISAN-06",
            name = "Komorebi Craftsman Solid Cherrywood Credenza",
            subtitle = "Hand-planed local artisan woodwork with exposed dovetail joinery",
            price = 3750.0,
            originalPrice = 4200.0,
            category = ProductCategory.ARTISAN,
            roomType = RoomType.LIVING_ROOM,
            material = "Old-growth Sustainable Cherrywood & Brass Pulls",
            color = "Amber Wild Cherry",
            dimensions = "75\"W x 19\"D x 30\"H (190 x 48 x 76 cm)",
            widthCm = 190f,
            depthCm = 48f,
            heightCm = 76f,
            weightKg = 64f,
            rating = 5.0f,
            reviewCount = 31,
            isFastShipEligible = false,
            sustainabilityScore = 100,
            ecoFriendlyBadge = "Handmade by Master Craftsman Henrik Lind",
            carbonOffsetKg = 80,
            fscCertifiedWood = true,
            description = "Each Komorebi credenza is individually crafted by hand in our Oregon timber studio. The sliding slatted tambours reveal adjustable shelving, wire management ports, and soft-close velvet lined cutlery/media drawers.",
            specifications = mapOf(
                "Craftsman Signature" to "Numbered and signed brass plate under top",
                "Joinery" to "Exposed through-tenons and hand-cut dovetails",
                "Finish" to "3 coats hand-buffed organic carnauba and beeswax",
                "Lead Time" to "In-stock atelier piece, ships in 5 business days"
            ),
            imageResName = "hero_showroom",
            availableSwatches = listOf(
                SwatchOption("Wild Cherry", 0xFF9E5738, "Solid Hardwood"),
                SwatchOption("Claro Walnut", 0xFF46382E, "Solid Hardwood")
            ),
            stockCount = 2,
            isArtisanHandcrafted = true,
            isPreOrder = false,
            barcode = "890412094406",
            styles = listOf(InteriorStyle.TRADITIONAL, InteriorStyle.BOHEMIAN)
        ),
        Product(
            id = "WCI-BOHO-07",
            name = "Tulum Handwoven Rattan & Teak Lounger",
            subtitle = "Artisanal handwoven cane webbing with sun-washed teak frame",
            price = 1420.0,
            originalPrice = 1650.0,
            category = ProductCategory.SEATING,
            roomType = RoomType.LIVING_ROOM,
            material = "Sustainably Harvested Teak & Handwoven Natural Rattan",
            color = "Natural Warm Rattan",
            dimensions = "31\"W x 35\"D x 32\"H (79 x 89 x 81 cm)",
            widthCm = 79f,
            depthCm = 89f,
            heightCm = 81f,
            weightKg = 21f,
            rating = 4.93f,
            reviewCount = 48,
            isFastShipEligible = true,
            sustainabilityScore = 98,
            ecoFriendlyBadge = "100% Bio-Renewable Natural Cane",
            carbonOffsetKg = 35,
            fscCertifiedWood = true,
            description = "Channel relaxed coastal sanctuary living. Hand-bent organic rattan webbing curves around an oiled teak frame, creating an inviting textural centerpiece that pairs effortlessly with linen throws and lush tropical botanicals.",
            specifications = mapOf(
                "Weave Type" to "Traditional Octagonal Hand-Cane Weaving",
                "Frame Material" to "Kiln-dried plantation teak with organic oil sealant",
                "Cushion" to "Removable Belgian linen seat cushion with down-blend filling",
                "Weight Capacity" to "320 lbs (145 kg)"
            ),
            imageResName = "img_style_bohemian",
            availableSwatches = listOf(
                SwatchOption("Sun-Washed Teak", 0xFFC49A6C, "Natural Cane"),
                SwatchOption("Smoked Charcoal Rattan", 0xFF403C38, "Stained Cane")
            ),
            stockCount = 14,
            isArtisanHandcrafted = true,
            barcode = "890412094407",
            styles = listOf(InteriorStyle.BOHEMIAN)
        ),
        Product(
            id = "WCI-IND-08",
            name = "Foundry Smoked Oak & Gunmetal Architect Desk",
            subtitle = "Solid 2\" rift-sawn oak top with laser-welded blackened steel trestles",
            price = 2150.0,
            originalPrice = 2400.0,
            category = ProductCategory.TABLES,
            roomType = RoomType.HOME_OFFICE,
            material = "Rough-sawn Smoked Oak & Blackened Structural Steel",
            color = "Smoked Raw Oak",
            dimensions = "68\"W x 32\"D x 30\"H (173 x 81 x 76 cm)",
            widthCm = 173f,
            depthCm = 81f,
            heightCm = 76f,
            weightKg = 72f,
            rating = 4.91f,
            reviewCount = 57,
            isFastShipEligible = true,
            sustainabilityScore = 95,
            ecoFriendlyBadge = "Upcycled Industrial Structural Steel",
            carbonOffsetKg = 50,
            fscCertifiedWood = true,
            description = "Built with utilitarian honesty and architectural power. Heavy blackened steel I-beam trestles anchor a 2-inch solid European smoked oak slab with hand-planed edges, integrated cable raceway, and magnetic power hub.",
            specifications = mapOf(
                "Desktop Thickness" to "2.0 inches (5 cm) solid continuous plank oak",
                "Base Construction" to "Cold-rolled structural steel with matte blackened patina",
                "Cable Integration" to "Concealed under-desk magnetic steel wire raceway",
                "Finish" to "Matte non-reflective polyurethane hardwax"
            ),
            imageResName = "img_style_industrial",
            availableSwatches = listOf(
                SwatchOption("Smoked Raw Oak", 0xFF5C4F44, "Smoked Oak"),
                SwatchOption("Blackened Ash", 0xFF242220, "Ash Timber")
            ),
            stockCount = 11,
            barcode = "890412094408",
            styles = listOf(InteriorStyle.INDUSTRIAL, InteriorStyle.MODERN)
        ),
        Product(
            id = "WCI-TRAD-09",
            name = "Kensington Deep-Tufted Chesterfield Chair",
            subtitle = "Hand-rubbed Italian saddle leather with deep diamond tufting",
            price = 2380.0,
            originalPrice = 2750.0,
            category = ProductCategory.SEATING,
            roomType = RoomType.LIVING_ROOM,
            material = "Full-Grain Waxed Saddle Leather & Solid Ash Frame",
            color = "Rich Cigar Brown",
            dimensions = "44\"W x 38\"D x 33\"H (112 x 96 x 84 cm)",
            widthCm = 112f,
            depthCm = 96f,
            heightCm = 84f,
            weightKg = 48f,
            rating = 4.97f,
            reviewCount = 73,
            isFastShipEligible = false,
            sustainabilityScore = 92,
            ecoFriendlyBadge = "Vegetable Tanned Leather & Heirloom Longevity",
            carbonOffsetKg = 45,
            fscCertifiedWood = true,
            description = "The quintessence of gentlemanly library luxury. Over 120 individually hand-tied leather buttons create dramatic deep diamond tufting across rolled bolster arms and antiqued brass nailhead trim.",
            specifications = mapOf(
                "Leather Treatment" to "Vegetable-tanned full-grain aniline with hand-buffed wax pull-up",
                "Frame" to "Corner-blocked, double-doweled kiln-dried hardwood",
                "Nailheads" to "Hand-hammered antiqued solid brass",
                "Suspension" to "Eight-way hand-tied steel coil springs"
            ),
            imageResName = "img_style_traditional",
            availableSwatches = listOf(
                SwatchOption("Cigar Saddle Leather", 0xFF653920, "Waxed Pull-Up"),
                SwatchOption("Oxblood Burgundy", 0xFF5B2222, "Aniline Leather"),
                SwatchOption("Midnight Navy Velvet", 0xFF1B263B, "Cotton Velvet")
            ),
            stockCount = 6,
            barcode = "890412094409",
            styles = listOf(InteriorStyle.TRADITIONAL)
        ),
        Product(
            id = "WCI-IND-10",
            name = "SoHo Modular Steel & Smoked Ash Etagere",
            subtitle = "Architectural multi-tier shelving with blackened iron turnbuckles",
            price = 1890.0,
            originalPrice = 2150.0,
            category = ProductCategory.STORAGE,
            roomType = RoomType.LIVING_ROOM,
            material = "Blackened Iron, Aircraft Wire & Solid Smoked Ash",
            color = "Gunmetal & Smoked Ash",
            dimensions = "48\"W x 16\"D x 78\"H (122 x 41 x 198 cm)",
            widthCm = 122f,
            depthCm = 41f,
            heightCm = 198f,
            weightKg = 54f,
            rating = 4.89f,
            reviewCount = 39,
            isFastShipEligible = true,
            sustainabilityScore = 96,
            ecoFriendlyBadge = "Cradle to Cradle Certified Steel",
            carbonOffsetKg = 38,
            fscCertifiedWood = true,
            description = "Open architectural verticality. Five thick solid smoked ash wood shelves float within a precision-engineered blackened tube steel exoskeleton braced by functional stainless aircraft turnbuckles.",
            specifications = mapOf(
                "Shelves" to "5 solid smoked European ash tiers (1.5\" thick)",
                "Weight Capacity" to "120 lbs per shelf",
                "Bracing" to "Heavy-duty tensioned industrial turnbuckles",
                "Wall Anchor" to "Included seismic zero-clearance wall bracket"
            ),
            imageResName = "img_style_industrial",
            availableSwatches = listOf(
                SwatchOption("Smoked Ash & Gunmetal", 0xFF3E3C3A, "Ash & Steel"),
                SwatchOption("Raw Bleached Oak & White Steel", 0xFFD8D2C6, "Oak & Steel")
            ),
            stockCount = 8,
            barcode = "890412094410",
            styles = listOf(InteriorStyle.INDUSTRIAL)
        )
    )

    val moodboards: List<MoodboardLook> = listOf(
        MoodboardLook(
            id = "LOOK-01",
            title = "Warm Scandinavian Sanctuary",
            designer = "Astrid Lindgren, WCI Principal Architect",
            style = "Nordic Organic Minimalist",
            description = "A serene harmony of natural oat boucle, tactile oak timber, and sculptural ceramic accents for an airy, grounded living space.",
            imageResName = "img_style_modern",
            bundledProductIds = listOf("WCI-SOFA-01", "WCI-CHAIR-02", "WCI-LAMP-04"),
            bundleDiscountPct = 15,
            matchingStyle = InteriorStyle.MODERN
        ),
        MoodboardLook(
            id = "LOOK-02",
            title = "Atelier Wabi-Sabi Dining Experience",
            designer = "Kenzo Takahashi, Spatial Master",
            style = "Japanese-Scandinavian (Japandi)",
            description = "Dramatic travertine stonework anchored by handcrafted cherrywood storage and warm diffused overhead illumination.",
            imageResName = "table_travertine",
            bundledProductIds = listOf("WCI-TABLE-03", "WCI-ARTISAN-06", "WCI-LAMP-04"),
            bundleDiscountPct = 18,
            matchingStyle = InteriorStyle.MODERN
        ),
        MoodboardLook(
            id = "LOOK-03",
            title = "Sun-Baked Bohemian Haven",
            designer = "Luna Del Mar, Textile Artisan",
            style = "Textural Earthy Bohemian",
            description = "Warm terracotta tones, handwoven rattan webbing, tactile Belgian linen, and sculptural stoneware for a relaxed, nature-steeped retreat.",
            imageResName = "img_style_bohemian",
            bundledProductIds = listOf("WCI-BOHO-07", "WCI-LAMP-04", "WCI-SOFA-01"),
            bundleDiscountPct = 20,
            matchingStyle = InteriorStyle.BOHEMIAN
        ),
        MoodboardLook(
            id = "LOOK-04",
            title = "Curated Heritage Library",
            designer = "Julian Montgomery, Historic Restorer",
            style = "Refined Classical Traditional",
            description = "Rich full-grain cigar leather, artisanal joinery credenza, and deep walnut tones honoring centuries of classical architectural proportion.",
            imageResName = "img_style_traditional",
            bundledProductIds = listOf("WCI-TRAD-09", "WCI-ARTISAN-06", "WCI-CHAIR-02"),
            bundleDiscountPct = 18,
            matchingStyle = InteriorStyle.TRADITIONAL
        ),
        MoodboardLook(
            id = "LOOK-05",
            title = "Architectural Loft Studio",
            designer = "Stefan Vance, Structural Architect",
            style = "Urban Loft Industrial",
            description = "Blackened steel exoskeleton shelving, heavy smoked oak trestle desk, and tactile leather seating calibrated for productive grandeur.",
            imageResName = "img_style_industrial",
            bundledProductIds = listOf("WCI-IND-08", "WCI-IND-10", "WCI-CHAIR-02"),
            bundleDiscountPct = 15,
            matchingStyle = InteriorStyle.INDUSTRIAL
        )
    )

    val verifiedReviews: List<Review> = listOf(
        Review(
            id = "REV-1",
            author = "Elena Rostova",
            rating = 5,
            date = "September 14, 2026",
            title = "Exceeded every high expectation – Museum quality",
            comment = "The tactile weight of the boucle and the hand-finished oak frame are breathtaking. The AR tool showed it fits our alcove perfectly, and the white-glove assembly team arrived exactly on the chosen slot.",
            verifiedPurchase = true,
            helpfulCount = 38
        ),
        Review(
            id = "REV-2",
            author = "David Chen, AIA Architect",
            rating = 5,
            date = "August 29, 2026",
            title = "Superior joinery and sustainable craftsmanship",
            comment = "As an architect, I inspect joints and tolerances closely. The mortise-and-tenon construction is flawless. The 10-year warranty gives our client total confidence.",
            verifiedPurchase = true,
            helpfulCount = 24
        ),
        Review(
            id = "REV-3",
            author = "Sophia Martinez",
            rating = 5,
            date = "August 12, 2026",
            title = "The 3D Room Planner made our remodel so easy",
            comment = "We designed our whole open plan in the app planner, checked door clearances with the AR tape tool, and ordered swatches beforehand. Seamless from start to finish.",
            verifiedPurchase = true,
            helpfulCount = 19
        )
    )

    val productQuestions: List<QuestionAnswer> = listOf(
        QuestionAnswer(
            id = "QA-1",
            question = "Will this sofa fit through an apartment doorway that is 30 inches wide?",
            askedBy = "Marcus K.",
            answer = "Yes! The legs unscrew easily, reducing the transport height to 25 inches. It will slide through standard 30-inch doors without issue.",
            answeredBy = "WCI Technical Team",
            date = "September 02, 2026"
        ),
        QuestionAnswer(
            id = "QA-2",
            question = "Is the boucle fabric treated with non-toxic stain resistance?",
            askedBy = "Rachel G.",
            answer = "Yes, our fabrics are treated with a PFAS-free, water-based nanotechnology shield that repels liquids while maintaining OEKO-TEX environmental safety.",
            answeredBy = "Elena S., Material Science Lead",
            date = "August 18, 2026"
        )
    )

    // Flow queries
    val cartItemsFlow: Flow<List<CartItemEntity>> = dao.getAllCartItems()
    val wishlistItemsFlow: Flow<List<WishlistItemEntity>> = dao.getAllWishlistItems()
    val ordersFlow: Flow<List<OrderEntity>> = dao.getAllOrders()
    val blueprintsFlow: Flow<List<SavedBlueprintEntity>> = dao.getAllBlueprints()
    val warrantiesFlow: Flow<List<WarrantyEntity>> = dao.getAllWarranties()

    fun isWishlisted(productId: String): Flow<Boolean> = dao.isWishlisted(productId)

    suspend fun addToCart(product: Product, swatch: SwatchOption, assembly: Boolean = false, address: String = "Default Residence") {
        dao.insertCartItem(
            CartItemEntity(
                productId = product.id,
                quantity = 1,
                swatchName = swatch.name,
                swatchHex = swatch.hexColor,
                assemblyBooked = assembly,
                destinationAddress = address
            )
        )
    }

    suspend fun removeCartItem(item: CartItemEntity) {
        dao.deleteCartItem(item)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    suspend fun toggleWishlist(productId: String, currentWishlisted: Boolean) {
        if (currentWishlisted) {
            dao.removeWishlist(productId)
        } else {
            dao.insertWishlist(WishlistItemEntity(productId = productId))
        }
    }

    suspend fun saveBlueprint(blueprint: SavedBlueprintEntity) {
        dao.insertBlueprint(blueprint)
    }

    suspend fun saveOrder(order: OrderEntity) {
        dao.insertOrder(order)
    }

    suspend fun registerWarranty(warranty: WarrantyEntity) {
        dao.insertWarranty(warranty)
    }

    private val _customSellerProducts = mutableListOf<Product>()

    fun addSellerProduct(product: Product) {
        _customSellerProducts.removeAll { it.id == product.id }
        _customSellerProducts.add(0, product)
    }

    fun getAllCatalogProducts(): List<Product> {
        return _customSellerProducts + catalogProducts
    }

    fun getProductById(id: String): Product? {
        return _customSellerProducts.find { it.id == id } ?: catalogProducts.find { it.id == id } ?: catalogProducts.firstOrNull()
    }

    // Interior Style Quiz Preferences Persistence
    private val prefs = context.getSharedPreferences("wci_user_prefs", Context.MODE_PRIVATE)

    fun saveUserStyleProfile(profile: UserStyleProfile) {
        val editor = prefs.edit()
        editor.putString("style_primary", profile.primaryStyle.name)
        profile.secondaryStyle?.let { editor.putString("style_secondary", it.name) } ?: editor.remove("style_secondary")
        editor.putString("style_palette", profile.preferredPalette)
        editor.putString("style_goal", profile.spaceGoal)
        editor.putString("style_materials", profile.preferredMaterials.joinToString(","))
        val scoresStr = profile.styleScores.entries.joinToString(",") { "${it.key.name}:${it.value}" }
        editor.putString("style_scores", scoresStr)
        editor.putLong("style_timestamp", profile.completedTimestamp)
        editor.apply()
    }

    fun getUserStyleProfile(): UserStyleProfile? {
        val primaryStr = prefs.getString("style_primary", null) ?: return null
        val primary = try { InteriorStyle.valueOf(primaryStr) } catch (e: Exception) { return null }
        val secondaryStr = prefs.getString("style_secondary", null)
        val secondary = secondaryStr?.let { try { InteriorStyle.valueOf(it) } catch (e: Exception) { null } }
        val palette = prefs.getString("style_palette", "") ?: ""
        val goal = prefs.getString("style_goal", "") ?: ""
        val materialsStr = prefs.getString("style_materials", "") ?: ""
        val materials = if (materialsStr.isNotBlank()) materialsStr.split(",") else emptyList()
        val scoresStr = prefs.getString("style_scores", "") ?: ""
        val scores = mutableMapOf<InteriorStyle, Int>()
        if (scoresStr.isNotBlank()) {
            scoresStr.split(",").forEach { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    try {
                        scores[InteriorStyle.valueOf(parts[0])] = parts[1].toInt()
                    } catch (_: Exception) {}
                }
            }
        }
        val ts = prefs.getLong("style_timestamp", System.currentTimeMillis())
        return UserStyleProfile(
            primaryStyle = primary,
            secondaryStyle = secondary,
            styleScores = scores,
            preferredMaterials = materials,
            preferredPalette = palette,
            spaceGoal = goal,
            completedTimestamp = ts
        )
    }

    fun clearUserStyleProfile() {
        prefs.edit()
            .remove("style_primary")
            .remove("style_secondary")
            .remove("style_scores")
            .remove("style_palette")
            .remove("style_goal")
            .remove("style_materials")
            .apply()
    }
}
