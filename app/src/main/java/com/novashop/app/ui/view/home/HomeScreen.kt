package com.novashop.app.ui.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.novashop.app.data.model.Artwork
import com.novashop.app.data.model.Category
import com.novashop.app.viewmodel.ArtworkViewModel
import com.novashop.app.viewmodel.AuthViewModel
import com.novashop.app.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    featuredArtworks: List<Artwork>,
    categories: List<Category>,
    isLoading: Boolean,
    itemCount: Int,
    currentUserLoggedIn: Boolean,
    onNavigateToProductList: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Nova Shop",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Nepalese Wall Art",
                            color = Color(0xFFE07B39),
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (itemCount > 0) {
                                Badge {
                                    Text(text = itemCount.toString())
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = {
                            if (currentUserLoggedIn) onNavigateToCart()
                            else onNavigateToLogin()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color.White
                            )
                        }
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
                .background(Color(0xFFFAF9F7))
        ) {
            // Hero Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFFF5EDE3)
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Elevate Your Space",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A1F14)
                        )
                        Text(
                            text = "with Modern Nepalese Art",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2A1F14)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hand-crafted wall décor prints",
                            fontSize = 14.sp,
                            color = Color(0xFF6B4F38)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToProductList,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2A1F14)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Shop Collection")
                        }
                    }
                }
            }

            // Categories
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Browse by Category",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A1F14)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            CategoryChip(
                                name = "All",
                                isSelected = true,
                                onClick = onNavigateToProductList
                            )
                        }
                        items(categories) { category ->
                            CategoryChip(
                                name = category.name,
                                isSelected = false,
                                onClick = onNavigateToProductList
                            )
                        }
                    }
                }
            }

            // Featured Artworks Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Trending Prints",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A1F14)
                    )
                    Text(
                        text = "See all →",
                        fontSize = 14.sp,
                        color = Color(0xFFE07B39),
                        modifier = Modifier.clickable { onNavigateToProductList() }
                    )
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFE07B39)
                        )
                    }
                }
            } else {
                items(featuredArtworks.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { artwork ->
                            ArtworkCard(
                                artwork = artwork,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigateToProductDetail(artwork.id) }
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// Main composable with ViewModel logic
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    artworkViewModel: ArtworkViewModel,
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    onNavigateToProductList: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val featuredArtworks by artworkViewModel.featuredArtworks.collectAsState()
    val categories by artworkViewModel.categories.collectAsState()
    val isLoading by artworkViewModel.isLoading.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val itemCount by remember { derivedStateOf { cartViewModel.itemCount } }

    HomeScreenContent(
        featuredArtworks = featuredArtworks,
        categories = categories,
        isLoading = isLoading,
        itemCount = itemCount,
        currentUserLoggedIn = currentUser != null,
        onNavigateToProductList = onNavigateToProductList,
        onNavigateToProductDetail = onNavigateToProductDetail,
        onNavigateToCart = onNavigateToCart,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF2A1F14) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            if (isSelected) Color(0xFF2A1F14) else Color.LightGray
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = name,
            color = if (isSelected) Color.White else Color(0xFF2A1F14),
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ArtworkCard(
    artwork: Artwork,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = artwork.imageUrl,
                contentDescription = artwork.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = artwork.categoryName,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Text(
                    text = artwork.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2A1F14),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "NPR ${artwork.basePrice.toInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A1F14)
                )
            }
        }
    }
}

// Preview with mock data
@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun HomeScreenPreview() {
    val mockArtworks = listOf(
        Artwork(
            id = "1",
            title = "Mountain Peak",
            categoryName = "Landscape",
            basePrice = 2500.0,
            imageUrl = "https://via.placeholder.com/200x200?text=Art1",
            description = "Beautiful mountain landscape",
            artistBio = "Local Nepalese artist",
            isLimitedEdition = false,
            isFeatured = true,
            tags = listOf("landscape", "nature")
        ),
        Artwork(
            id = "2",
            title = "Urban Dreams",
            categoryName = "Modern",
            basePrice = 3000.0,
            imageUrl = "https://via.placeholder.com/200x200?text=Art2",
            description = "Modern urban art",
            artistBio = "Contemporary artist",
            isLimitedEdition = true,
            isFeatured = true,
            tags = listOf("urban", "modern")
        ),
        Artwork(
            id = "3",
            title = "Nature Call",
            categoryName = "Nature",
            basePrice = 2800.0,
            imageUrl = "https://via.placeholder.com/200x200?text=Art3",
            description = "Nature inspired artwork",
            artistBio = "Nature photographer",
            isLimitedEdition = false,
            isFeatured = true,
            tags = listOf("nature", "wildlife")
        ),
        Artwork(
            id = "4",
            title = "Abstract Flow",
            categoryName = "Abstract",
            basePrice = 3200.0,
            imageUrl = "https://via.placeholder.com/200x200?text=Art4",
            description = "Abstract modern art",
            artistBio = "Abstract artist",
            isLimitedEdition = true,
            isFeatured = true,
            tags = listOf("abstract", "modern")
        )
    )

    val mockCategories = listOf(
        Category(id = "1", name = "Landscape"),
        Category(id = "2", name = "Modern"),
        Category(id = "3", name = "Abstract")
    )

    HomeScreenContent(
        featuredArtworks = mockArtworks,
        categories = mockCategories,
        isLoading = false,
        itemCount = 2,
        currentUserLoggedIn = true,
        onNavigateToProductList = { },
        onNavigateToProductDetail = { },
        onNavigateToCart = { },
        onNavigateToLogin = { }
    )
}