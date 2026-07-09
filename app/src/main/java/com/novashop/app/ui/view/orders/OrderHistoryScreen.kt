package com.novashop.app.ui.view.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.novashop.app.viewmodel.AuthViewModel
import com.novashop.app.viewmodel.OrderViewModel

// UI-only composable (for preview)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreenContent(
    orders: List<Order>,
    isLoading: Boolean,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Orders",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE07B39))
            }
        } else if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📦", fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No orders yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2A1F14)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your orders will appear here",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFAF9F7)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order = order)
                }
            }
        }
    }
}

// Main composable with ViewModel logic
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    orderViewModel: OrderViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val orders by orderViewModel.orders.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { orderViewModel.loadMyOrders(it.id) }
    }

    OrderHistoryScreenContent(
        orders = orders,
        isLoading = isLoading,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun OrderCard(order: Order) {
    val statusColor = when (order.status) {
        "delivered" -> Color(0xFF27500A)
        "shipped" -> Color(0xFF0C447C)
        "processing" -> Color(0xFF3C3489)
        "confirmed" -> Color(0xFF0C447C)
        "cancelled" -> Color(0xFF791F1F)
        else -> Color(0xFF633806)
    }

    val statusBg = when (order.status) {
        "delivered" -> Color(0xFFEAF3DE)
        "shipped" -> Color(0xFFE6F1FB)
        "processing" -> Color(0xFFEEEDFE)
        "confirmed" -> Color(0xFFE6F1FB)
        "cancelled" -> Color(0xFFFCEBEB)
        else -> Color(0xFFFAEEDA)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${order.orderNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF2A1F14)
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusBg
                ) {
                    Text(
                        text = order.status.replaceFirstChar { it.uppercase() },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            order.items.forEach { item ->
                Text(
                    text = "${item.title} × ${item.quantity}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider()

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total paid",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "NPR ${order.totalAmount.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF2A1F14)
                )
            }
        }
    }
}

// Preview with mock data
@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun OrderHistoryScreenPreview() {
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
                ),
                OrderItem(
                    artworkId = "2",
                    title = "Urban Dreams",
                    imageUrl = "https://via.placeholder.com/100",
                    quantity = 1,
                    unitPrice = 3000.0,
                    totalPrice = 3000.0
                )
            ),
            subtotal = 8000.0,
            shippingFee = 0.0,
            totalAmount = 8000.0,
            status = "delivered",
            paymentStatus = "paid",
            paymentMethod = "credit_card",
            shippingFullName = "John Doe",
            shippingPhone = "9841234567",
            shippingAddress = "Kathmandu, Nepal",
            shippingCity = "Kathmandu",
            shippingProvince = "Bagmati",
            createdAt = 1705276800000L
        ),
        Order(
            id = "2",
            orderNumber = "ORD-002",
            userId = "user1",
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
            shippingFullName = "Jane Smith",
            shippingPhone = "9851234567",
            shippingAddress = "Lalitpur, Nepal",
            shippingCity = "Lalitpur",
            shippingProvince = "Bagmati",
            createdAt = 1705363200000L
        ),
        Order(
            id = "3",
            orderNumber = "ORD-003",
            userId = "user1",
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
            shippingFee = 100.0,
            totalAmount = 3300.0,
            status = "processing",
            paymentStatus = "pending",
            paymentMethod = "bank_transfer",
            shippingFullName = "Ram Kumar",
            shippingPhone = "9861234567",
            shippingAddress = "Bhaktapur, Nepal",
            shippingCity = "Bhaktapur",
            shippingProvince = "Bagmati",
            createdAt = 1705622400000L
        )
    )

    OrderHistoryScreenContent(
        orders = mockOrders,
        isLoading = false,
        onNavigateBack = { }
    )
}
