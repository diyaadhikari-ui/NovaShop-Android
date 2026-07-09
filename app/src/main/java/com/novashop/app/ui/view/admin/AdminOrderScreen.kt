package com.novashop.app.ui.view.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.novashop.app.viewmodel.OrderViewModel

// UI-only composable (for preview)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreenContent(
    orders: List<Order>,
    isLoading: Boolean,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onUpdateStatus: (String, String) -> Unit
) {
    val filters = listOf("All", "Pending", "Confirmed", "Processing", "Shipped", "Delivered", "Cancelled")

    val filteredOrders = if (selectedFilter == "All") {
        orders
    } else {
        orders.filter { it.status == selectedFilter.lowercase() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Orders Management",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F4F2))
        ) {
            // Filter chips
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterChange(filter) },
                        label = { Text(text = filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2A1F14),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Text(
                text = "${filteredOrders.size} orders",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE07B39))
                }
            } else if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No orders found",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredOrders) { order ->
                        AdminOrderCard(
                            order = order,
                            onUpdateStatus = { newStatus ->
                                onUpdateStatus(order.id, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

// Main composable with ViewModel logic
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    orderViewModel: OrderViewModel,
    onNavigateBack: () -> Unit
) {
    val orders by orderViewModel.orders.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        orderViewModel.loadAllOrders()
    }

    AdminOrdersScreenContent(
        orders = orders,
        isLoading = isLoading,
        selectedFilter = selectedFilter,
        onFilterChange = { selectedFilter = it },
        onNavigateBack = onNavigateBack,
        onUpdateStatus = { orderId, newStatus ->
            orderViewModel.updateOrderStatus(orderId, newStatus)
        }
    )
}

@Composable
fun AdminOrderCard(
    order: Order,
    onUpdateStatus: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf(
        "pending", "confirmed", "processing",
        "shipped", "delivered", "cancelled"
    )

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
                Column {
                    Text(
                        text = "#${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF2A1F14)
                    )
                    Text(
                        text = order.shippingFullName,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = order.shippingPhone,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "NPR ${order.totalAmount.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF2A1F14)
                    )
                    Text(
                        text = order.paymentMethod,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Order items
            order.items.forEach { item ->
                Text(
                    text = "• ${item.title} × ${item.quantity}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider()

            Spacer(modifier = Modifier.height(10.dp))

            // Status update
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2A1F14)
                )

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    ) {
                        Text(
                            text = order.status.replaceFirstChar { it.uppercase() },
                            fontSize = 13.sp,
                            color = Color(0xFF2A1F14)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statuses.forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = status.replaceFirstChar { it.uppercase() }
                                    )
                                },
                                onClick = {
                                    onUpdateStatus(status)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Preview with mock data
@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun AdminOrdersScreenPreview() {
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
            status = "pending",
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
            status = "confirmed",
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
            status = "processing",
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
            status = "shipped",
            paymentStatus = "paid",
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
        ),
        Order(
            id = "6",
            orderNumber = "ORD-006",
            userId = "user6",
            items = listOf(
                OrderItem(
                    artworkId = "1",
                    title = "Mountain Peak",
                    imageUrl = "https://via.placeholder.com/100",
                    quantity = 1,
                    unitPrice = 2500.0,
                    totalPrice = 2500.0
                )
            ),
            subtotal = 2500.0,
            shippingFee = 200.0,
            totalAmount = 2700.0,
            status = "cancelled",
            paymentStatus = "refunded",
            paymentMethod = "esewa",
            shippingFullName = "Priya Gupta",
            shippingPhone = "9891234567",
            shippingAddress = "Janakpur",
            shippingCity = "Janakpur",
            shippingProvince = "Province 2",
            createdAt = System.currentTimeMillis()
        )
    )

    var selectedFilter by remember { mutableStateOf("All") }

    AdminOrdersScreenContent(
        orders = mockOrders,
        isLoading = false,
        selectedFilter = selectedFilter,
        onFilterChange = { selectedFilter = it },
        onNavigateBack = { },
        onUpdateStatus = { _, _ -> }
    )
}