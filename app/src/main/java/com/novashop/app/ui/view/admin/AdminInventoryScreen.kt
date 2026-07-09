package com.novashop.app.ui.view.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.storage.FirebaseStorage
import coil.compose.AsyncImage
import com.novashop.app.data.model.Artwork
import com.novashop.app.viewmodel.ArtworkViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInventoryScreen(
    artworkViewModel: ArtworkViewModel,
    onNavigateBack: () -> Unit
) {
    val artworks by artworkViewModel.artworks.collectAsState()
    val isLoading by artworkViewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingArtwork by remember { mutableStateOf<Artwork?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inventory",
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
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Artwork",
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F4F2)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "${artworks.size} artworks listed",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                items(artworks) { artwork ->
                    InventoryItemCard(
                        artwork = artwork,
                        onEdit = { editingArtwork = artwork },
                        onDelete = { artworkViewModel.deleteArtwork(artwork.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ArtworkFormDialog(
            artwork = null,
            onDismiss = { showAddDialog = false },
            onSave = { artwork ->
                artworkViewModel.addArtwork(artwork)
                showAddDialog = false
            }
        )
    }

    editingArtwork?.let { artwork ->
        ArtworkFormDialog(
            artwork = artwork,
            onDismiss = { editingArtwork = null },
            onSave = { updatedArtwork ->
                artworkViewModel.updateArtwork(updatedArtwork)
                editingArtwork = null
            }
        )
    }
}

@Composable
fun InventoryItemCard(
    artwork: Artwork,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
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
                model = artwork.imageUrl,
                contentDescription = artwork.title,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artwork.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color(0xFF2A1F14)
                )

                Text(
                    text = artwork.categoryName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    text = "NPR ${artwork.basePrice.toInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A1F14)
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF2A1F14)
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun ArtworkFormDialog(
    artwork: Artwork?,
    onDismiss: () -> Unit,
    onSave: (Artwork) -> Unit
) {
    var title by remember { mutableStateOf(artwork?.title ?: "") }
    var description by remember { mutableStateOf(artwork?.description ?: "") }
    var price by remember { mutableStateOf(artwork?.basePrice?.toString() ?: "") }
    var category by remember { mutableStateOf(artwork?.categoryName ?: "") }

    var imageUrl by remember { mutableStateOf(artwork?.imageUrl ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            errorMessage = ""
        }
    }

    Dialog(onDismissRequest = {
        if (!isUploading) onDismiss()
    }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = if (artwork == null) "Add Artwork" else "Edit Artwork",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A1F14)
                    )
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isUploading
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 2,
                        enabled = !isUploading
                    )
                }

                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isUploading
                    )
                }

                item {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Base Price (NPR) *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isUploading
                    )
                }

                item {
                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUploading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE07B39)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (selectedImageUri != null || imageUrl.isNotEmpty())
                                "Change Image"
                            else
                                "Choose Image from Phone"
                        )
                    }
                }

                item {
                    val previewImage = selectedImageUri ?: imageUrl

                    if (previewImage.toString().isNotEmpty()) {
                        AsyncImage(
                            model = previewImage,
                            contentDescription = "Artwork Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                if (isUploading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color(0xFFE07B39),
                                strokeWidth = 2.dp
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Uploading image...",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    item {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isUploading
                        ) {
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = {
                                if (title.isBlank()) {
                                    errorMessage = "Title is required"
                                    return@Button
                                }

                                if (price.isBlank()) {
                                    errorMessage = "Price is required"
                                    return@Button
                                }

                                if (selectedImageUri == null && imageUrl.isBlank()) {
                                    errorMessage = "Please choose an image"
                                    return@Button
                                }

                                isUploading = true
                                errorMessage = ""

                                if (selectedImageUri != null) {
                                    uploadArtworkImageToFirebase(
                                        imageUri = selectedImageUri!!,
                                        onSuccess = { uploadedUrl ->
                                            isUploading = false

                                            val newArtwork = Artwork(
                                                id = artwork?.id ?: "",
                                                title = title,
                                                description = description,
                                                categoryName = category,
                                                basePrice = price.toDoubleOrNull() ?: 0.0,
                                                imageUrl = uploadedUrl,
                                                isFeatured = artwork?.isFeatured ?: false,
                                                isLimitedEdition = artwork?.isLimitedEdition ?: false,
                                                artistBio = artwork?.artistBio ?: "",
                                                tags = artwork?.tags ?: emptyList()
                                            )

                                            onSave(newArtwork)
                                        },
                                        onFailure = { error ->
                                            isUploading = false
                                            errorMessage = error
                                        }
                                    )
                                } else {
                                    isUploading = false

                                    val updatedArtwork = Artwork(
                                        id = artwork?.id ?: "",
                                        title = title,
                                        description = description,
                                        categoryName = category,
                                        basePrice = price.toDoubleOrNull() ?: 0.0,
                                        imageUrl = imageUrl,
                                        isFeatured = artwork?.isFeatured ?: false,
                                        isLimitedEdition = artwork?.isLimitedEdition ?: false,
                                        artistBio = artwork?.artistBio ?: "",
                                        tags = artwork?.tags ?: emptyList()
                                    )

                                    onSave(updatedArtwork)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isUploading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2A1F14)
                            )
                        ) {
                            Text(text = "Save")
                        }
                    }
                }
            }
        }
    }
}

fun uploadArtworkImageToFirebase(
    imageUri: Uri,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageName = "artworks/${UUID.randomUUID()}.jpg"
    val imageRef = storageRef.child(imageName)

    imageRef.putFile(imageUri)
        .addOnSuccessListener {
            imageRef.downloadUrl
                .addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Failed to get image URL")
                }
        }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Image upload failed")
        }
}