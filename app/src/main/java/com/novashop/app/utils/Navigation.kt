package com.novashop.app.utils

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ProductList : Screen("product_list")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object OrderSuccess : Screen("order_success/{orderId}") {
        fun createRoute(orderId: String) = "order_success/$orderId"
    }
    object OrderHistory : Screen("order_history")
    object AdminDashboard : Screen("admin_dashboard")
    object AdminInventory : Screen("admin_inventory")
    object AdminOrders : Screen("admin_orders")
    object AddEditProduct : Screen("add_edit_product/{productId}") {
        fun createRoute(productId: String = "") = "add_edit_product/$productId"
    }
}