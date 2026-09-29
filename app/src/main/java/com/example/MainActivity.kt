package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.WciTopBar
import com.example.ui.screens.*
import com.example.ui.theme.BrassGold
import com.example.ui.theme.WalnutBrown
import com.example.ui.theme.WciFurnitureTheme
import com.example.ui.viewmodel.WciViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Atelier", Icons.Outlined.Weekend)
    object SpatialAr : Screen("spatial_ar?productId={productId}", "AR Spatial", Icons.Outlined.ViewInAr)
    object RoomPlanner : Screen("room_planner", "3D Planner", Icons.Outlined.Architecture)
    object Concierge : Screen("consultation", "Concierge", Icons.Outlined.Forum)
    object Orders : Screen("order_tracking", "Live GPS", Icons.Outlined.LocalShipping)
    object Hub : Screen("profile_hub", "Atelier Hub", Icons.Outlined.AccountCircle)

    // Sub-screens
    object ProductDetail : Screen("product_detail/{productId}", "Details", Icons.Outlined.Info)
    object Cart : Screen("cart", "Cart", Icons.Outlined.ShoppingBag)
    object ServicesCare : Screen("services_care", "Care & Warranty", Icons.Outlined.Handyman)
    object TradePortal : Screen("trade_portal", "Trade Pro", Icons.Outlined.Store)
    object SellerStudio : Screen("seller_studio", "Seller Studio", Icons.Outlined.Storefront)
    object Community : Screen("community", "Moodboards", Icons.Outlined.Collections)
    object ShowroomTour : Screen("showroom_tour", "360° Showroom", Icons.Outlined.CropFree)
    object BarcodeScanner : Screen("barcode_scanner", "Scan", Icons.Outlined.QrCodeScanner)
    object StyleQuiz : Screen("style_quiz", "Style Quiz", Icons.Outlined.AutoAwesome)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WciFurnitureTheme {
                val navController = rememberNavController()
                val viewModel: WciViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                val cartItems by viewModel.cartItems.collectAsState()
                val wishlistItems by viewModel.wishlistItems.collectAsState()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomNavItems = listOf(
                    Screen.Home,
                    Screen.SpatialAr,
                    Screen.RoomPlanner,
                    Screen.Concierge,
                    Screen.Orders,
                    Screen.Hub
                )

                val showBottomNav = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.RoomPlanner.route,
                    Screen.Concierge.route,
                    Screen.Orders.route,
                    Screen.Hub.route,
                    Screen.TradePortal.route,
                    Screen.ServicesCare.route,
                    Screen.Community.route
                )

                val showTopBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.RoomPlanner.route,
                    Screen.Concierge.route,
                    Screen.Orders.route,
                    Screen.Hub.route,
                    Screen.TradePortal.route,
                    Screen.ServicesCare.route,
                    Screen.Community.route,
                    Screen.SellerStudio.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (showTopBar) {
                            WciTopBar(
                                title = when (currentRoute) {
                                    Screen.RoomPlanner.route -> "3D Room Studio"
                                    Screen.Concierge.route -> "Atelier Concierge"
                                    Screen.Orders.route -> "Live GPS Dispatch"
                                    Screen.Hub.route -> "Atelier Hub & IoT"
                                    Screen.TradePortal.route -> "Trade Pro Portal"
                                    Screen.ServicesCare.route -> "Care & Warranties"
                                    Screen.Community.route -> "Shoppable Looks"
                                    Screen.SellerStudio.route -> "Atelier Seller Studio"
                                    else -> "WCI Atelier"
                                },
                                wishlistCount = wishlistItems.size,
                                cartCount = cartItems.sumOf { it.quantity },
                                isOffline = uiState.isOfflineMode,
                                onSearchClick = {
                                    navController.navigate(Screen.Home.route)
                                },
                                onWishlistClick = {
                                    navController.navigate(Screen.Home.route)
                                },
                                onCartClick = {
                                    navController.navigate(Screen.Cart.route)
                                },
                                onScannerClick = {
                                    navController.navigate(Screen.BarcodeScanner.route)
                                },
                                onOfflineToggle = {
                                    viewModel.toggleOfflineMode()
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("bottom_navigation_bar")
                            ) {
                                bottomNavItems.forEach { screen ->
                                    val isSelected = when (screen) {
                                        Screen.Home -> currentRoute == Screen.Home.route
                                        Screen.SpatialAr -> currentRoute?.startsWith("spatial_ar") == true
                                        Screen.RoomPlanner -> currentRoute == Screen.RoomPlanner.route
                                        Screen.Concierge -> currentRoute == Screen.Concierge.route
                                        Screen.Orders -> currentRoute == Screen.Orders.route
                                        Screen.Hub -> currentRoute in listOf(Screen.Hub.route, Screen.TradePortal.route, Screen.ServicesCare.route)
                                        else -> false
                                    }

                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = screen.icon,
                                                contentDescription = screen.title
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = screen.title,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        selected = isSelected,
                                        onClick = {
                                            if (screen == Screen.SpatialAr) {
                                                navController.navigate("spatial_ar?productId=WCI-SOFA-01")
                                            } else {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = WalnutBrown,
                                            selectedTextColor = WalnutBrown,
                                            indicatorColor = BrassGold.copy(alpha = 0.35f),
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("nav_item_${screen.title.lowercase().replace(" ", "_")}")
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. Home Catalog Screen
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                onProductClick = { pid ->
                                    navController.navigate("product_detail/$pid")
                                },
                                onNavigateToAr = { pid ->
                                    navController.navigate("spatial_ar?productId=$pid")
                                },
                                onNavigateToShowroomTour = {
                                    navController.navigate(Screen.ShowroomTour.route)
                                },
                                onNavigateToStyleQuiz = {
                                    navController.navigate(Screen.StyleQuiz.route)
                                },
                                onNavigateToMoodboard = { _ ->
                                    navController.navigate(Screen.Community.route)
                                },
                                onNavigateToSellerStudio = {
                                    navController.navigate(Screen.SellerStudio.route)
                                }
                            )
                        }

                        // 2. Product Detail Screen
                        composable(
                            route = Screen.ProductDetail.route,
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""
                            ProductDetailScreen(
                                productId = productId,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToAr = { pid ->
                                    navController.navigate("spatial_ar?productId=$pid")
                                },
                                onNavigateToCart = {
                                    navController.navigate(Screen.Cart.route)
                                }
                            )
                        }

                        // 3. Spatial AR Screen
                        composable(
                            route = Screen.SpatialAr.route,
                            arguments = listOf(navArgument("productId") {
                                type = NavType.StringType
                                defaultValue = "WCI-SOFA-01"
                            })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: "WCI-SOFA-01"
                            SpatialArScreen(
                                initialProductId = productId,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // 4. Room Planner Screen
                        composable(Screen.RoomPlanner.route) {
                            RoomPlannerScreen(
                                viewModel = viewModel,
                                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
                            )
                        }

                        // 5. Cart & Checkout Screen
                        composable(Screen.Cart.route) {
                            CartCheckoutScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onOrderPlaced = { _ ->
                                    navController.navigate(Screen.Orders.route) {
                                        popUpTo(Screen.Home.route)
                                    }
                                }
                            )
                        }

                        // 6. Order Tracking & Live GPS Screen
                        composable(Screen.Orders.route) {
                            OrderTrackingScreen(viewModel = viewModel)
                        }

                        // 7. Services & Care Screen
                        composable(Screen.ServicesCare.route) {
                            ServicesCareScreen(viewModel = viewModel)
                        }

                        // 8. Concierge & Chat Screen
                        composable(Screen.Concierge.route) {
                            ConsultationAndChatScreen(viewModel = viewModel)
                        }

                        // 9. Trade Pro Portal Screen
                        composable(Screen.TradePortal.route) {
                            TradePortalScreen(
                                viewModel = viewModel,
                                onProductClick = { pid -> navController.navigate("product_detail/$pid") },
                                onNavigateToSellerStudio = { navController.navigate(Screen.SellerStudio.route) }
                            )
                        }

                        // 10. Community & Moodboard Screen
                        composable(Screen.Community.route) {
                            CommunityMoodboardScreen(
                                viewModel = viewModel,
                                onProductClick = { pid -> navController.navigate("product_detail/$pid") },
                                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
                            )
                        }

                        // 11. Profile & Hub Screen
                        composable(Screen.Hub.route) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                ScrollableTabRow(
                                    selectedTabIndex = 0,
                                    edgePadding = 16.dp,
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    Tab(
                                        selected = true,
                                        onClick = { },
                                        text = { Text("Profile & IoT", fontWeight = FontWeight.Bold) }
                                    )
                                    Tab(
                                        selected = false,
                                        onClick = { navController.navigate(Screen.TradePortal.route) },
                                        text = { Text("Trade Pro (B2B)") }
                                    )
                                    Tab(
                                        selected = false,
                                        onClick = { navController.navigate(Screen.SellerStudio.route) },
                                        text = { Text("Seller Studio (AI Guide)") }
                                    )
                                    Tab(
                                        selected = false,
                                        onClick = { navController.navigate(Screen.ServicesCare.route) },
                                        text = { Text("Care & Warranty") }
                                    )
                                    Tab(
                                        selected = false,
                                        onClick = { navController.navigate(Screen.Community.route) },
                                        text = { Text("Community Looks") }
                                    )
                                }
                                ProfileAndHubScreen(
                                    viewModel = viewModel,
                                    onNavigateToStyleQuiz = { navController.navigate(Screen.StyleQuiz.route) },
                                    onNavigateToSellerStudio = { navController.navigate(Screen.SellerStudio.route) }
                                )
                            }
                        }

                        // 12. Showroom 360 Tour Screen
                        composable(Screen.ShowroomTour.route) {
                            ShowroomTourScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onProductClick = { pid -> navController.navigate("product_detail/$pid") }
                            )
                        }

                        // 13. Barcode & QR Scanner Screen
                        composable(Screen.BarcodeScanner.route) {
                            BarcodeQrScannerScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onProductFound = { pid -> navController.navigate("product_detail/$pid") }
                            )
                        }

                        // 14. Style Diagnostic Quiz Screen
                        composable(Screen.StyleQuiz.route) {
                            StyleQuizScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onQuizComplete = { navController.navigate(Screen.Home.route) }
                            )
                        }

                        // 15. Seller Studio & AI Furniture Listing Assistant
                        composable(Screen.SellerStudio.route) {
                            SellerStudioScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onViewProductInStorefront = { pid -> navController.navigate("product_detail/$pid") }
                            )
                        }
                    }
                }
            }
        }
    }
}
