package com.novashop.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novashop.app.data.model.Artwork
import com.novashop.app.data.model.Category
import com.novashop.app.data.repository.ArtworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArtworkViewModel : ViewModel() {

    private val repository = ArtworkRepository()

    private val _artworks = MutableStateFlow<List<Artwork>>(emptyList())
    val artworks: StateFlow<List<Artwork>> = _artworks

    private val _featuredArtworks = MutableStateFlow<List<Artwork>>(emptyList())
    val featuredArtworks: StateFlow<List<Artwork>> = _featuredArtworks

    private val _selectedArtwork = MutableStateFlow<Artwork?>(null)
    val selectedArtwork: StateFlow<Artwork?> = _selectedArtwork

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    init {
        Log.d("ArtworkViewModel", "Init: Loading artworks, categories, featured")
        loadArtworks()
        loadCategories()
        loadFeaturedArtworks()
    }

    fun loadArtworks() {
        Log.d("ArtworkViewModel", "loadArtworks() called")
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAllArtworks()
            if (result.isSuccess) {
                val data = result.getOrNull() ?: emptyList()
                Log.d("ArtworkViewModel", "✅ Artworks loaded: ${data.size} items")
                data.forEach { artwork ->
                    Log.d("ArtworkViewModel", "  - ${artwork.title} (${artwork.imageUrl})")
                }
                _artworks.value = data
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e("ArtworkViewModel", "❌ Error loading artworks: $error")
                _error.value = error
            }
            _isLoading.value = false
        }
    }

    fun loadFeaturedArtworks() {
        Log.d("ArtworkViewModel", "loadFeaturedArtworks() called")
        viewModelScope.launch {
            val result = repository.getFeaturedArtworks()
            if (result.isSuccess) {
                val data = result.getOrNull() ?: emptyList()
                Log.d("ArtworkViewModel", "✅ Featured artworks loaded: ${data.size} items")
                _featuredArtworks.value = data
            } else {
                Log.e("ArtworkViewModel", "❌ Error loading featured: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun loadCategories() {
        Log.d("ArtworkViewModel", "loadCategories() called")
        viewModelScope.launch {
            val result = repository.getCategories()
            if (result.isSuccess) {
                val data = result.getOrNull() ?: emptyList()
                Log.d("ArtworkViewModel", "✅ Categories loaded: ${data.size} items")
                _categories.value = data
            } else {
                Log.e("ArtworkViewModel", "❌ Error loading categories: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun selectArtwork(artwork: Artwork) {
        _selectedArtwork.value = artwork
    }

    fun loadArtworkById(id: String) {
        Log.d("ArtworkViewModel", "loadArtworkById($id) called")
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getArtworkById(id)
            if (result.isSuccess) {
                _selectedArtwork.value = result.getOrNull()
                Log.d("ArtworkViewModel", "✅ Artwork loaded: ${result.getOrNull()?.title}")
            } else {
                Log.e("ArtworkViewModel", "❌ Error loading artwork: ${result.exceptionOrNull()?.message}")
            }
            _isLoading.value = false
        }
    }

    fun filterByCategory(category: String) {
        Log.d("ArtworkViewModel", "filterByCategory($category) called")
        _selectedCategory.value = category
        viewModelScope.launch {
            _isLoading.value = true
            if (category == "All") {
                val result = repository.getAllArtworks()
                if (result.isSuccess) {
                    _artworks.value = result.getOrNull() ?: emptyList()
                    Log.d("ArtworkViewModel", "✅ Filtered (All): ${_artworks.value.size} items")
                }
            } else {
                val result = repository.getArtworksByCategory(category)
                if (result.isSuccess) {
                    _artworks.value = result.getOrNull() ?: emptyList()
                    Log.d("ArtworkViewModel", "✅ Filtered ($category): ${_artworks.value.size} items")
                }
            }
            _isLoading.value = false
        }
    }

    fun searchArtworks(query: String) {
        Log.d("ArtworkViewModel", "searchArtworks($query) called")
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.searchArtworks(query)
            if (result.isSuccess) {
                _artworks.value = result.getOrNull() ?: emptyList()
                Log.d("ArtworkViewModel", "✅ Search results: ${_artworks.value.size} items")
            }
            _isLoading.value = false
        }
    }

    fun addArtwork(artwork: Artwork) {
        Log.d("ArtworkViewModel", "addArtwork() called")
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addArtwork(artwork)
            if (result.isSuccess) {
                Log.d("ArtworkViewModel", "✅ Artwork added")
                loadArtworks()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e("ArtworkViewModel", "❌ Error adding artwork: $error")
                _error.value = error
            }
            _isLoading.value = false
        }
    }

    fun updateArtwork(artwork: Artwork) {
        Log.d("ArtworkViewModel", "updateArtwork() called")
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateArtwork(artwork)
            if (result.isSuccess) {
                Log.d("ArtworkViewModel", "✅ Artwork updated")
                loadArtworks()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e("ArtworkViewModel", "❌ Error updating artwork: $error")
                _error.value = error
            }
            _isLoading.value = false
        }
    }

    fun deleteArtwork(id: String) {
        Log.d("ArtworkViewModel", "deleteArtwork($id) called")
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteArtwork(id)
            if (result.isSuccess) {
                Log.d("ArtworkViewModel", "✅ Artwork deleted")
                loadArtworks()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e("ArtworkViewModel", "❌ Error deleting artwork: $error")
                _error.value = error
            }
            _isLoading.value = false
        }
    }
}