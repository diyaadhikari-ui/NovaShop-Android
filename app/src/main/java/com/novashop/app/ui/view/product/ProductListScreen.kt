package com.novashop.app.ui.view.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.novashop.app.ui.view.home.CategoryChip
import com.novashop.app.viewmodel.ArtworkViewModel

// UI-only composable (for preview)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreenContent(
    artworks: List<Artwork>,
    categories: List<Category>,
    isLoading: Boolean,
    selectedCategory: String,
    searchQuery: String,
    showSearch: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onCategorySelect: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search artworks...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = "Contemporary Prints",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                actions = {
                    IconButton(onClick = onSearchToggle) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
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
        ) {
            // Category filter
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        name = "All",
                        isSelected = selectedCategory == "All",
                        onClick = { onCategorySelect("All") }
                    )
                }
                items(categories) { category ->
                    CategoryChip(
                        name = category.name,
                        isSelected = selectedCategory == category.name,
                        onClick = { onCategorySelect(category.name) }
                    )
                }
            }

            // Products count
            Text(
                text = "${artworks.size} artworks in collection",
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
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(artworks.chunked(2)) { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { artwork ->
                                ProductCard(
                                    artwork = artwork,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        onNavigateToProductDetail(artwork.id)
                                    }
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    artworkViewModel: ArtworkViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit
) {
    val artworks by artworkViewModel.artworks.collectAsState()
    val categories by artworkViewModel.categories.collectAsState()
    val isLoading by artworkViewModel.isLoading.collectAsState()
    val selectedCategory by artworkViewModel.selectedCategory.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    ProductListScreenContent(
        artworks = artworks,
        categories = categories,
        isLoading = isLoading,
        selectedCategory = selectedCategory,
        searchQuery = searchQuery,
        showSearch = showSearch,
        onSearchQueryChange = {
            searchQuery = it
            if (it.isNotEmpty()) {
                artworkViewModel.searchArtworks(it)
            } else {
                artworkViewModel.loadArtworks()
            }
        },
        onSearchToggle = { showSearch = !showSearch },
        onCategorySelect = { artworkViewModel.filterByCategory(it) },
        onNavigateBack = onNavigateBack,
        onNavigateToProductDetail = { id ->
            val selected = artworks.find { it.id == id }
            selected?.let {
                artworkViewModel.selectArtwork(it)
                onNavigateToProductDetail(id)
            }
        }
    )
}

@Composable
fun ProductCard(
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
            Box {
                AsyncImage(
                    model = artwork.imageUrl,
                    contentDescription = artwork.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )
                if (artwork.isLimitedEdition) {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF2A1F14).copy(alpha = 0.85f)
                    ) {
                        Text(
                            text = "Limited",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = artwork.categoryName,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Text(
                    text = artwork.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2A1F14),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Starting from",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "NPR ${artwork.basePrice.toInt()}",
                    fontSize = 14.sp,
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
fun ProductListScreenPreview() {
    val mockArtworks = listOf(
        Artwork(
            id = "1",
            title = "Mountain Peak",
            categoryName = "Landscape",
            basePrice = 2500.0,
            imageUrl = "https://via.placeholder.com/200x200?text=Art1",
            description = "Beautiful mountain landscape",
            artistBio = "Local artist",
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
        ),
        Artwork(
            id = "5",
            title = "Serene Sunset",
            categoryName = "Landscape",
            basePrice = 2600.0,
            imageUrl = "https://via.placeholder.com/200x200?text=Art5",
            description = "Beautiful sunset landscape",
            artistBio = "Landscape specialist",
            isLimitedEdition = false,
            isFeatured = true,
            tags = listOf("sunset", "landscape")
        ),
        Artwork(
            id = "6",
            title = "City Lights",
            categoryName = "Modern",
            basePrice = 3100.0,
            imageUrl = "https://via.placeholder.com/200x200?text=Art6",
            description = "Modern city artwork",
            artistBio = "Urban artist",
            isLimitedEdition = false,
            isFeatured = true,
            tags = listOf("city", "modern")
        )
    )

    val mockCategories = listOf(
        Category(id = "1", name = "Landscape"),
        Category(id = "2", name = "Modern"),
        Category(id = "3", name = "Abstract")
    )

    ProductListScreenContent(
        artworks = mockArtworks,
        categories = mockCategories,
        isLoading = false,
        selectedCategory = "All",
        searchQuery = "",
        showSearch = false,
        onSearchQueryChange = { },
        onSearchToggle = { },
        onCategorySelect = { },
        onNavigateBack = { },
        onNavigateToProductDetail = { }
    )
}