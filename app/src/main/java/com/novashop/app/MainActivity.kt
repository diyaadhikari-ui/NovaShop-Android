package com.novashop.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.novashop.app.data.model.StripeConfig
import com.novashop.app.ui.theme.NovaShopTheme
import com.novashop.app.ui.view.admin.AdminDashboardScreen
import com.novashop.app.ui.view.admin.AdminInventoryScreen
import com.novashop.app.ui.view.admin.AdminOrdersScreen
import com.novashop.app.ui.view.auth.LoginScreen
import com.novashop.app.ui.view.auth.RegisterScreen
import com.novashop.app.ui.view.auth.SplashScreen
import com.novashop.app.ui.view.cart.CartScreen
import com.novashop.app.ui.view.cart.CheckoutScreen
import com.novashop.app.ui.view.home.HomeScreen
import com.novashop.app.ui.view.orders.OrderHistoryScreen
import com.novashop.app.ui.view.orders.OrderSuccessScreen
import com.novashop.app.ui.view.product.ProductDetailScreen
import com.novashop.app.ui.view.product.ProductListScreen
import com.novashop.app.utils.Screen
import com.novashop.app.viewmodel.ArtworkViewModel
import com.novashop.app.viewmodel.AuthViewModel
import com.novashop.app.viewmodel.CartViewModel
import com.novashop.app.viewmodel.OrderViewModel
import com.novashop.app.viewmodel.StripePaymentViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        StripeConfig.initialize(this)

        setContent {
            NovaShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NovaShopApp()
                }
            }
        }
    }
}

@Composable
fun NovaShopApp() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val artworkViewModel: ArtworkViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()
    val stripePaymentViewModel: StripePaymentViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                authViewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                artworkViewModel = artworkViewModel,
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                onNavigateToProductList = {
                    navController.navigate(Screen.ProductList.route)
                },
                onNavigateToProductDetail = { id ->
                    navController.navigate(
                        Screen.ProductDetail.createRoute(id)
                    )
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.ProductList.route) {
            ProductListScreen(
                artworkViewModel = artworkViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProductDetail = { id ->
                    navController.navigate(
                        Screen.ProductDetail.createRoute(id)
                    )
                }
            )
        }

        composable(Screen.ProductDetail.route) {
            ProductDetailScreen(
                artworkViewModel = artworkViewModel,
                cartViewModel = cartViewModel,
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                cartViewModel = cartViewModel,
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                }
            )
        }

        composable(Screen.Checkout.route) {
            CheckoutScreen(
                cartViewModel = cartViewModel,
                authViewModel = authViewModel,
                orderViewModel = orderViewModel,
                stripePaymentViewModel = stripePaymentViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOrderSuccess = { orderId ->
                    navController.navigate(
                        Screen.OrderSuccess.createRoute(orderId)
                    ) {
                        popUpTo(Screen.Cart.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.OrderSuccess.route) {
            OrderSuccessScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.OrderHistory.route)
                }
            )
        }

        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(
                orderViewModel = orderViewModel,
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                authViewModel = authViewModel,
                artworkViewModel = artworkViewModel,
                orderViewModel = orderViewModel,
                onNavigateToInventory = {
                    navController.navigate(Screen.AdminInventory.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.AdminOrders.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminInventory.route) {
            AdminInventoryScreen(
                artworkViewModel = artworkViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AdminOrders.route) {
            AdminOrdersScreen(
                orderViewModel = orderViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
