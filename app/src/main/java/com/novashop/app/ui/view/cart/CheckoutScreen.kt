package com.novashop.app.ui.view.cart

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novashop.app.data.model.CartItem
import com.novashop.app.data.model.Order
import com.novashop.app.data.model.OrderItem
import com.novashop.app.viewmodel.*

// UI-only composable (for preview)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreenContent(
    cartItems: List<CartItem>,
    fullName: String,
    onFullNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    province: String,
    onProvinceChange: (String) -> Unit,
    selectedPayment: String,
    onPaymentChange: (String) -> Unit,
    subtotal: Double,
    isLoading: Boolean,
    errorMessage: String,
    onNavigateBack: () -> Unit,
    onPlaceOrder: () -> Unit
) {
    val shippingFee = if (subtotal > 5000) 0.0 else 200.0
    val total = subtotal + shippingFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout",
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
                .background(Color(0xFFFAF9F7))
                .verticalScroll(rememberScrollState())
        ) {
            // Shipping Info Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF2A1F14),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "1",
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Shipping Information",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A1F14)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = onFullNameChange,
                            label = { Text("Full Name *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = onPhoneChange,
                            label = { Text("Phone *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = onCityChange,
                            label = { Text("City *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = province,
                            onValueChange = onProvinceChange,
                            label = { Text("Province") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = onAddressChange,
                        label = { Text("Street Address *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            }

            // Payment Method Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF2A1F14),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "2",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Payment Method",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A1F14)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Options List
                    listOf(
                        Triple("esewa", "eSewa", "Digital wallet · Recommended"),
                        Triple("khalti", "Khalti", "Digital wallet"),
                        Triple("stripe", "Stripe", "Credit/Debit card"),
                        Triple("cod", "Cash on Delivery", "Pay when it arrives")
                    ).forEach { (id, name, sub) ->
                        val isSelected = selectedPayment == id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    Color(0xFFEAF3DE)
                                else Color(0xFFF5F4F2)
                            ),
                            border = if (isSelected)
                                androidx.compose.foundation.BorderStroke(
                                    1.5.dp, Color(0xFF60BB46)
                                )
                            else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onPaymentChange(id) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFF60BB46)
                                    )
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = Color(0xFF2A1F14)
                                    )
                                    Text(
                                        text = sub,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Order Summary Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Order Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A1F14)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.artworkTitle} x${item.quantity}",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "NPR ${item.totalPrice.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Subtotal", color = Color.Gray, fontSize = 13.sp)
                        Text(text = "NPR ${subtotal.toInt()}", fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Shipping", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            text = if (shippingFee == 0.0) "Free"
                            else "NPR ${shippingFee.toInt()}",
                            fontSize = 13.sp
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2A1F14)
                        )
                        Text(
                            text = "NPR ${total.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2A1F14)
                        )
                    }
                }
            }

            // Error Message Display
            if (errorMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFC62828),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Place Order Button
            Button(
                onClick = onPlaceOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (selectedPayment) {
                        "esewa" -> Color(0xFF60BB46)
                        "stripe" -> Color(0xFF635BFF)
                        "khalti" -> Color(0xFF6C3AED)
                        else -> Color(0xFF2A1F14)
                    }
                ),
                enabled = !isLoading && cartItems.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = when (selectedPayment) {
                            "esewa" -> "Pay with eSewa"
                            "stripe" -> "Pay with Stripe"
                            "khalti" -> "Pay with Khalti"
                            else -> "Place Order"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Main composable with ViewModel logic
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    orderViewModel: OrderViewModel,
    stripePaymentViewModel: StripePaymentViewModel,
    onNavigateBack: () -> Unit,
    onOrderSuccess: (String) -> Unit
) {
    val context = LocalContext.current

    // Cart and user data
    val cartItems by cartViewModel.cartItems.collectAsState(initial = emptyList())
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)

    // Order ViewModel states
    val orderPlaced by orderViewModel.orderPlaced.collectAsState(initial = false)
    val orderLoading by orderViewModel.isLoading.collectAsState(initial = false)

    // Stripe Payment ViewModel states
    val stripePaymentUrl by stripePaymentViewModel.paymentUrl.collectAsState(initial = null)
    val stripePaymentStatus by stripePaymentViewModel.paymentStatus.collectAsState(initial = PaymentStatus.IDLE)
    val stripeLoading by stripePaymentViewModel.isLoading.collectAsState(initial = false)
    val stripeError by stripePaymentViewModel.errorMessage.collectAsState(initial = "")

    // Form state
    var fullName by remember { mutableStateOf(currentUser?.fullName ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var address by remember { mutableStateOf(currentUser?.address ?: "") }
    var city by remember { mutableStateOf(currentUser?.city ?: "") }
    var province by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("esewa") }
    var errorMessage by remember { mutableStateOf("") }

    val subtotal = cartViewModel.subtotal
    val isLoading = orderLoading || stripeLoading

    // Update form when user data loads
    LaunchedEffect(currentUser) {
        currentUser?.let {
            fullName = it.fullName ?: ""
            phone = it.phone ?: ""
            address = it.address ?: ""
            city = it.city ?: ""
        }
    }

    // Handle order success
    LaunchedEffect(orderPlaced) {
        if (orderPlaced) {
            onOrderSuccess("order_placed")
            orderViewModel.resetOrderPlaced()
        }
    }

    // Handle Stripe payment link opening
    LaunchedEffect(stripePaymentUrl) {
        stripePaymentUrl?.let { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                errorMessage = "Failed to open payment link: ${e.message}"
            } finally {
                stripePaymentViewModel.clearPaymentUrl()
            }
        }
    }

    // Handle Stripe payment success
    LaunchedEffect(stripePaymentStatus) {
        if (stripePaymentStatus == PaymentStatus.SUCCESS) {
            onOrderSuccess("stripe_payment_success")
            stripePaymentViewModel.resetPaymentState()
        }
    }

    // Update error message from Stripe
    LaunchedEffect(stripeError) {
        if (stripeError.isNotEmpty()) {
            errorMessage = stripeError
        }
    }

    CheckoutScreenContent(
        cartItems = cartItems,
        fullName = fullName,
        onFullNameChange = {
            fullName = it
            errorMessage = ""
        },
        phone = phone,
        onPhoneChange = {
            phone = it
            errorMessage = ""
        },
        address = address,
        onAddressChange = {
            address = it
            errorMessage = ""
        },
        city = city,
        onCityChange = {
            city = it
            errorMessage = ""
        },
        province = province,
        onProvinceChange = {
            province = it
            errorMessage = ""
        },
        selectedPayment = selectedPayment,
        onPaymentChange = { selectedPayment = it },
        subtotal = subtotal,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onNavigateBack = onNavigateBack,
        onPlaceOrder = {
            // Validate all required fields
            if (fullName.isEmpty()) {
                errorMessage = "Full Name is required"
                return@CheckoutScreenContent
            }
            if (phone.isEmpty()) {
                errorMessage = "Phone number is required"
                return@CheckoutScreenContent
            }
            if (address.isEmpty()) {
                errorMessage = "Street Address is required"
                return@CheckoutScreenContent
            }
            if (city.isEmpty()) {
                errorMessage = "City is required"
                return@CheckoutScreenContent
            }
            if (cartItems.isEmpty()) {
                errorMessage = "Your cart is empty"
                return@CheckoutScreenContent
            }

            // Create order items from cart
            val orderItems = cartItems.map { item ->
                OrderItem(
                    artworkId = item.artworkId,
                    title = item.artworkTitle,
                    imageUrl = item.artworkImage,
                    quantity = item.quantity,
                    unitPrice = item.price,
                    totalPrice = item.totalPrice
                )
            }

            // Calculate shipping fee
            val shippingFee = if (subtotal > 5000) 0.0 else 200.0
            val totalAmount = subtotal + shippingFee

            // Create order object
            val order = Order(
                userId = currentUser?.id ?: "",
                orderNumber = "NS-${System.currentTimeMillis()}",
                status = "pending",
                paymentMethod = selectedPayment,
                paymentStatus = "unpaid",
                subtotal = subtotal,
                shippingFee = shippingFee,
                totalAmount = totalAmount,
                shippingFullName = fullName,
                shippingPhone = phone,
                shippingAddress = address,
                shippingCity = city,
                shippingProvince = province,
                items = orderItems,
                createdAt = System.currentTimeMillis()
            )

            // Handle different payment methods
            when (selectedPayment) {
                "stripe" -> {
                    // Use StripePaymentViewModel for Stripe payments
                    val stripePaymentLink = "https://buy.stripe.com/YOUR_LINK_ID"  // Replace with actual link
                    stripePaymentViewModel.initiateStripePayment(order, stripePaymentLink)
                }
                "esewa" -> {
                    // Use OrderViewModel for eSewa
                    orderViewModel.createOrder(order)
                }
                "khalti" -> {
                    // Use OrderViewModel for Khalti
                    orderViewModel.createOrder(order)
                }
                "cod" -> {
                    // Use OrderViewModel for Cash on Delivery
                    orderViewModel.createOrder(order)
                }
                else -> {
                    errorMessage = "Invalid payment method selected"
                }
            }
        }
    )
}

// Preview with mock data
@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun CheckoutScreenPreview() {
    val mockCartItems = listOf(
        CartItem(
            id = "1",
            artworkId = "art1",
            artworkTitle = "Mountain Peak",
            artworkImage = "https://via.placeholder.com/100",
            quantity = 2,
            price = 2500.0,
            totalPrice = 5000.0
        ),
        CartItem(
            id = "2",
            artworkId = "art2",
            artworkTitle = "Urban Dreams",
            artworkImage = "https://via.placeholder.com/100",
            quantity = 1,
            price = 3000.0,
            totalPrice = 3000.0
        )
    )

    val subtotal = mockCartItems.sumOf { it.totalPrice }

    CheckoutScreenContent(
        cartItems = mockCartItems,
        fullName = "Diya Adhikari",
        onFullNameChange = { },
        phone = "9841234567",
        onPhoneChange = { },
        address = "Kathmandu, Nepal",
        onAddressChange = { },
        city = "Kathmandu",
        onCityChange = { },
        province = "Bagmati",
        onProvinceChange = { },
        selectedPayment = "esewa",
        onPaymentChange = { },
        subtotal = subtotal,
        isLoading = false,
        errorMessage = "",
        onNavigateBack = { },
        onPlaceOrder = { }
    )
}