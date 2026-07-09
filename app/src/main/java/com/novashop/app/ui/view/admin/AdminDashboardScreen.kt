package com.novashop.app.ui.view.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novashop.app.data.model.Order
import com.novashop.app.data.model.OrderItem
import com.novashop.app.viewmodel.ArtworkViewModel
import com.novashop.app.viewmodel.AuthViewModel
import com.novashop.app.viewmodel.OrderViewModel

// UI-only composable (for preview)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreenContent(
    adminEmail: String,
    totalRevenue: Double,
    totalOrders: Int,
    totalArtworks: Int,
    pendingOrders: Int,
    recentOrders: List<Order>,
    onNavigateToInventory: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Admin Dashboard",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = adminEmail,
                            color = Color(0xFFE07B39),
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2A1F14)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F4F2)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats
            item {
                Text(
                    text = "Analytics Overview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A1F14)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Total Revenue",
                        value = "NPR ${totalRevenue.toInt()}",
                        bgColor = Color(0xFFEAF3DE),
                        textColor = Color(0xFF27500A)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Total Orders",
                        value = totalOrders.toString(),
                        bgColor = Color(0xFFE6F1FB),
                        textColor = Color(0xFF0C447C)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Artworks",
                        value = totalArtworks.toString(),
                        bgColor = Color(0xFFEEEDFE),
                        textColor = Color(0xFF3C3489)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Pending Orders",
                        value = pendingOrders.toString(),
                        bgColor = Color(0xFFFAEEDA),
                        textColor = Color(0xFF633806)
                    )
                }
            }

            // Quick Actions
            item {
                Text(
                    text = "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A1F14)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Manage Inventory",
                        subtitle = "$totalArtworks artworks",
                        emoji = "🖼️",
                        onClick = onNavigateToInventory
                    )
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Manage Orders",
                        subtitle = "$totalOrders orders",
                        emoji = "📦",
                        onClick = onNavigateToOrders
                    )
                }
            }

            // Recent Orders
            item {
                Text(
                    text = "Recent Orders",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A1F14)
                )
            }

            items(
                items = recentOrders.take(5),
                key = { order ->
                    order.id.ifBlank { order.orderNumber.ifBlank { order.createdAt.toString() } }
                }
            ) { order ->
                OrderCard(order = order)
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "#${order.orderNumber}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = Color(0xFF2A1F14)
                )
                Text(
                    text = order.shippingFullName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "NPR ${order.totalAmount.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF2A1F14)
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFAEEDA)
                ) {
                    Text(
                        text = order.status,
                        fontSize = 10.sp,
                        color = Color(0xFF633806),
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 3.dp
                        )
                    )
                }
            }
        }
    }
}

// Main composable with ViewModel logic
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    authViewModel: AuthViewModel,
    artworkViewModel: ArtworkViewModel,
    orderViewModel: OrderViewModel,
    onNavigateToInventory: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit
) {
    val artworks by artworkViewModel.artworks.collectAsState()
    val orders by orderViewModel.orders.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        artworkViewModel.loadArtworks()
        orderViewModel.loadAllOrders()
    }

    // ✅ FIX #3: Memoize expensive calculations with remember
    val totalRevenue = remember(orders) {
        orders
            .filter { it.paymentStatus == "paid" }
            .fold(0.0) { acc, order -> acc + order.totalAmount }
    }

    val pendingOrders = remember(orders) {
        orders.count { it.status == "pending" }
    }

    val adminEmail = remember(currentUser) {
        currentUser?.email ?: ""
    }

    AdminDashboardScreenContent(
        adminEmail = adminEmail,
        totalRevenue = totalRevenue,
        totalOrders = orders.size,
        totalArtworks = artworks.size,
        pendingOrders = pendingOrders,
        recentOrders = orders,
        onNavigateToInventory = onNavigateToInventory,
        onNavigateToOrders = onNavigateToOrders,
        onLogout = {
            authViewModel.logout()
            onLogout()
        }
    )
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    bgColor: Color,
    textColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = textColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    emoji: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color(0xFF2A1F14)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

// Preview with mock data
@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun AdminDashboardScreenPreview() {
    val mockOrders = listOf(
        Order(
            id = "1",
            orderNumber = "ORD-001",
            userId = "user1",
            items = listOf(
                OrderItem(
                    artworkId = "1",
                    title = "Mountain Peak",
                    imageUrl = "https://via.placeholder.com/100",
                    quantity = 2,
                    unitPrice = 2500.0,
                    totalPrice = 5000.0
                )
            ),
            subtotal = 5000.0,
            shippingFee = 0.0,
            totalAmount = 5000.0,
            status = "delivered",
            paymentStatus = "paid",
            paymentMethod = "esewa",
            shippingFullName = "John Doe",
            shippingPhone = "9841234567",
            shippingAddress = "Kathmandu",
            shippingCity = "Kathmandu",
            shippingProvince = "Bagmati",
            createdAt = System.currentTimeMillis()
        ),
        Order(
            id = "2",
            orderNumber = "ORD-002",
            userId = "user2",
            items = listOf(
                OrderItem(
                    artworkId = "2",
                    title = "Urban Dreams",
                    imageUrl = "https://via.placeholder.com/100",
                    quantity = 1,
                    unitPrice = 3000.0,
                    totalPrice = 3000.0
                )
            ),
            subtotal = 3000.0,
            shippingFee = 200.0,
            totalAmount = 3200.0,
            status = "pending",
            paymentStatus = "paid",
            paymentMethod = "khalti",
            shippingFullName = "Jane Smith",
            shippingPhone = "9851234567",
            shippingAddress = "Lalitpur",
            shippingCity = "Lalitpur",
            shippingProvince = "Bagmati",
            createdAt = System.currentTimeMillis()
        ),
        Order(
            id = "3",
            orderNumber = "ORD-003",
            userId = "user3",
            items = listOf(
                OrderItem(
                    artworkId = "3",
                    title = "Nature Call",
                    imageUrl = "https://via.placeholder.com/100",
                    quantity = 1,
                    unitPrice = 2800.0,
                    totalPrice = 2800.0
                )
            ),
            subtotal = 2800.0,
            shippingFee = 0.0,
            totalAmount = 2800.0,
            status = "shipped",
            paymentStatus = "paid",
            paymentMethod = "esewa",
            shippingFullName = "Ram Kumar",
            shippingPhone = "9861234567",
            shippingAddress = "Bhaktapur",
            shippingCity = "Bhaktapur",
            shippingProvince = "Bagmati",
            createdAt = System.currentTimeMillis()
        ),
        Order(
            id = "4",
            orderNumber = "ORD-004",
            userId = "user4",
            items = listOf(
                OrderItem(
                    artworkId = "4",
                    title = "Abstract Flow",
                    imageUrl = "https://via.placeholder.com/100",
                    quantity = 1,
                    unitPrice = 3200.0,
                    totalPrice = 3200.0
                )
            ),
            subtotal = 3200.0,
            shippingFee = 200.0,
            totalAmount = 3400.0,
            status = "pending",
            paymentStatus = "unpaid",
            paymentMethod = "bank_transfer",
            shippingFullName = "Sita Sharma",
            shippingPhone = "9871234567",
            shippingAddress = "Pokhara",
            shippingCity = "Pokhara",
            shippingProvince = "Gandaki",
            createdAt = System.currentTimeMillis()
        ),
        Order(
            id = "5",
            orderNumber = "ORD-005",
            userId = "user5",
            items = listOf(
                OrderItem(
                    artworkId = "5",
                    title = "Serene Sunset",
                    imageUrl = "https://via.placeholder.com/100",
                    quantity = 1,
                    unitPrice = 2600.0,
                    totalPrice = 2600.0
                )
            ),
            subtotal = 2600.0,
            shippingFee = 0.0,
            totalAmount = 2600.0,
            status = "delivered",
            paymentStatus = "paid",
            paymentMethod = "esewa",
            shippingFullName = "Arjun Patel",
            shippingPhone = "9881234567",
            shippingAddress = "Biratnagar",
            shippingCity = "Biratnagar",
            shippingProvince = "Province 1",
            createdAt = System.currentTimeMillis()
        )
    )

    val totalRevenue = mockOrders
        .filter { it.paymentStatus == "paid" }
        .fold(0.0) { acc, order -> acc + order.totalAmount }

    val pendingOrders = mockOrders.count { it.status == "pending" }

    AdminDashboardScreenContent(
        adminEmail = "admin@novashop.com",
        totalRevenue = totalRevenue,
        totalOrders = mockOrders.size,
        totalArtworks = 25,
        pendingOrders = pendingOrders,
        recentOrders = mockOrders,
        onNavigateToInventory = { },
        onNavigateToOrders = { },
        onLogout = { }
    )
}