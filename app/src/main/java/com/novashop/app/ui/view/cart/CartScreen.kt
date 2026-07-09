package com.novashop.app.ui.view.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.novashop.app.data.model.CartItem
import com.novashop.app.viewmodel.AuthViewModel
import com.novashop.app.viewmodel.CartViewModel

// UI-only composable (for preview)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreenContent(
    cartItems: List<CartItem>,
    isLoading: Boolean,
    subtotal: Double,
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    val shippingFee = if (subtotal > 5000) 0.0 else 200.0
    val total = subtotal + shippingFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Cart (${cartItems.size})",
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
        } else if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🛒", fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your cart is empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2A1F14)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add some beautiful Nepalese art",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2A1F14)
                        ),
                        shape = RoundedCornerShape(11.dp)
                    ) {
                        Text(text = "Browse Artworks")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFAF9F7))
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(
                            item = item,
                            onUpdateQuantity = { newQty ->
                                onUpdateQuantity(item.id, newQty)
                            },
                            onRemove = {
                                onRemove(item.id)
                            }
                        )
                    }
                }

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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Subtotal", color = Color.Gray)
                            Text(text = "NPR ${subtotal.toInt()}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Shipping", color = Color.Gray)
                            Text(
                                text = if (shippingFee == 0.0) "Free"
                                else "NPR ${shippingFee.toInt()}"
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "NPR ${total.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF2A1F14)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2A1F14)
                            )
                        ) {
                            Text(
                                text = "Proceed to Checkout",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Main composable with ViewModel logic
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val isLoading by cartViewModel.isLoading.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { cartViewModel.loadCart(it.id) }
    }

    val subtotal = cartViewModel.subtotal

    CartScreenContent(
        cartItems = cartItems,
        isLoading = isLoading,
        subtotal = subtotal,
        onNavigateBack = onNavigateBack,
        onNavigateToCheckout = onNavigateToCheckout,
        onUpdateQuantity = { itemId, newQty ->
            currentUser?.let { user ->
                val item = cartItems.find { it.id == itemId }
                item?.let {
                    cartViewModel.updateQuantity(itemId, newQty, it.price, user.id)
                }
            }
        },
        onRemove = { itemId ->
            currentUser?.let { user ->
                cartViewModel.removeFromCart(itemId, user.id)
            }
        }
    )
}

@Composable
fun CartItemCard(
    item: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.artworkImage,
                contentDescription = item.artworkTitle,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.artworkTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2A1F14)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "NPR ${item.price.toInt()}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (item.quantity > 1) {
                                onUpdateQuantity(item.quantity - 1)
                            }
                        },
                        modifier = Modifier.size(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "−")
                    }
                    Text(
                        text = item.quantity.toString(),
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) },
                        modifier = Modifier.size(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "+")
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "NPR ${item.totalPrice.toInt()}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A1F14)
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

// Preview with mock data
@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun CartScreenPreview() {
    val mockCartItems = listOf(
        CartItem(
            id = "1",
            artworkTitle = "Mountain Peak",
            artworkImage = "https://via.placeholder.com/100",
            quantity = 2,
            price = 2500.0,
            totalPrice = 5000.0
        ),
        CartItem(
            id = "2",
            artworkTitle = "Urban Dreams",
            artworkImage = "https://via.placeholder.com/100",
            quantity = 1,
            price = 3000.0,
            totalPrice = 3000.0
        ),
        CartItem(
            id = "3",
            artworkTitle = "Nature Call",
            artworkImage = "https://via.placeholder.com/100",
            quantity = 1,
            price = 2800.0,
            totalPrice = 2800.0
        )
    )

    val subtotal = mockCartItems.sumOf { it.totalPrice }

    CartScreenContent(
        cartItems = mockCartItems,
        isLoading = false,
        subtotal = subtotal,
        onNavigateBack = { },
        onNavigateToCheckout = { },
        onUpdateQuantity = { _, _ -> },
        onRemove = { }
    )
}