package com.novashop.app.ui.view.product

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.novashop.app.data.model.Artwork
import com.novashop.app.data.model.CartItem
import com.novashop.app.viewmodel.ArtworkViewModel
import com.novashop.app.viewmodel.AuthViewModel
import com.novashop.app.viewmodel.CartViewModel

// UI-only composable (for preview)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreenContent(
    artwork: Artwork?,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    addedToCart: Boolean,
    isUserLoggedIn: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onAddToCart: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = artwork?.title ?: "Product Detail",
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
        },
        bottomBar = {
            artwork?.let { art ->
                Surface(
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!isUserLoggedIn) {
                                    onNavigateToLogin()
                                    return@Button
                                }
                                onAddToCart()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2A1F14)
                            )
                        ) {
                            Text(
                                text = if (addedToCart) "Added! ✓" else "Add to Cart",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { onNavigateToCart() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE07B39)
                            )
                        ) {
                            Text(
                                text = "View Cart",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        artwork?.let { art ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFAF9F7))
                    .verticalScroll(rememberScrollState())
            ) {
                // Image
                AsyncImage(
                    model = art.imageUrl,
                    contentDescription = art.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    // Tags
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFF5F4F2)
                        ) {
                            Text(
                                text = art.categoryName,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
                        if (art.isLimitedEdition) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFFAEEDA)
                            ) {
                                Text(
                                    text = "Limited Edition",
                                    fontSize = 12.sp,
                                    color = Color(0xFF633806),
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 4.dp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title
                    Text(
                        text = art.title,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A1F14)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price
                    Text(
                        text = "NPR ${art.basePrice.toInt()}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A1F14)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = art.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quantity selector
                    Text(
                        text = "Quantity",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2A1F14)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(40.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "−", fontSize = 18.sp)
                        }

                        Text(
                            text = quantity.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A1F14)
                        )

                        OutlinedButton(
                            onClick = { onQuantityChange(quantity + 1) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(40.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "+", fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Artist bio
                    if (art.artistBio.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F4F2)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "ABOUT THE ARTIST",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = art.artistBio,
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE07B39))
            }
        }
    }
}

// Main composable with ViewModel logic
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    artworkViewModel: ArtworkViewModel,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val artwork by artworkViewModel.selectedArtwork.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    var quantity by remember { mutableStateOf(1) }
    var addedToCart by remember { mutableStateOf(false) }

    ProductDetailScreenContent(
        artwork = artwork,
        quantity = quantity,
        onQuantityChange = { quantity = it },
        addedToCart = addedToCart,
        isUserLoggedIn = currentUser != null,
        onNavigateBack = onNavigateBack,
        onNavigateToCart = onNavigateToCart,
        onNavigateToLogin = onNavigateToLogin,
        onAddToCart = {
            val user = currentUser

            if (user == null) {
                onNavigateToLogin()
                return@ProductDetailScreenContent
            }

            artwork?.let { art ->
                val cartItem = CartItem(
                    id = "",
                    userId = user.id,
                    artworkId = art.id,
                    artworkTitle = art.title,
                    artworkImage = art.imageUrl,
                    price = art.basePrice,
                    quantity = quantity,
                    totalPrice = art.basePrice * quantity
                )

                cartViewModel.addToCart(cartItem, user.id)
                addedToCart = true
            }
        }

    )
}

// Preview with mock data
@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun ProductDetailScreenPreview() {
    val mockArtwork = Artwork(
        id = "1",
        title = "Mountain Peak",
        description = "A stunning landscape artwork featuring majestic mountains with snow-capped peaks. This beautiful piece captures the essence of nature's grandeur and is perfect for adding a touch of serenity to any space. Hand-crafted with exceptional attention to detail.",
        artistBio = "Local Nepalese artist specializing in landscape and nature-inspired artwork. With over 10 years of experience, this artist has created numerous masterpieces that celebrate the beauty of the Himalayan region.",
        categoryName = "Landscape",
        basePrice = 2500.0,
        imageUrl = "https://via.placeholder.com/400x300?text=Mountain+Peak",
        isLimitedEdition = false,
        isFeatured = true,
        tags = listOf("landscape", "nature", "mountains")
    )

    ProductDetailScreenContent(
        artwork = mockArtwork,
        quantity = 1,
        onQuantityChange = { },
        addedToCart = false,
        isUserLoggedIn = true,
        onNavigateBack = { },
        onNavigateToCart = { },
        onNavigateToLogin = { },
        onAddToCart = { }
    )
}