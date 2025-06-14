package com.dev.lokabudaya.pages.Book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.KulinerItem
import com.dev.lokabudaya.data.TourItem
import com.dev.lokabudaya.pages.Auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val authViewModel: AuthViewModel) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _favoriteItems = MutableStateFlow<Set<String>>(emptySet())
    val favoriteItems: StateFlow<Set<String>> = _favoriteItems

    init {
        loadUserFavorites()
    }

    fun refreshFavorites() {
        loadUserFavorites()
    }

    fun toggleFavorite(item: Any) {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        val itemId = getItemId(item)
        val itemType = getItemType(item)

        when (item) {
            is KulinerItem -> item.isFavorite = !item.isFavorite
            is EventItem -> item.isFavorite = !item.isFavorite
            is TourItem -> item.isFavorite = !item.isFavorite
        }

        val currentFavorites = _favoriteItems.value.toMutableSet()
        if (getFavoriteState(item)) {
            currentFavorites.add(itemId)
        } else {
            currentFavorites.remove(itemId)
        }
        _favoriteItems.value = currentFavorites

        viewModelScope.launch {
            try {
                if (getFavoriteState(item)) {
                    addToFirestoreFavorites(currentUser.uid, itemId, itemType)
                } else {
                    removeFromFirestoreFavorites(currentUser.uid, itemId)
                }
            } catch (e: Exception) {
                when (item) {
                    is KulinerItem -> item.isFavorite = !item.isFavorite
                    is EventItem -> item.isFavorite = !item.isFavorite
                    is TourItem -> item.isFavorite = !item.isFavorite
                }

                val rollbackFavorites = _favoriteItems.value.toMutableSet()
                if (getFavoriteState(item)) {
                    rollbackFavorites.remove(itemId)
                } else {
                    rollbackFavorites.add(itemId)
                }
                _favoriteItems.value = rollbackFavorites
            }
        }
        fun getItemId(item: Any): String {
            return when (item) {
                is KulinerItem -> "${item.title}_${item.price}_kuliner"
                is EventItem -> "${item.title}_${item.price}_event"
                is TourItem -> "${item.title}_${item.price}_tour"
                else -> ""
            }
        }
    }

    fun getFavoriteState(item: Any): Boolean {
        return when (item) {
            is KulinerItem -> item.isFavorite
            is EventItem -> item.isFavorite
            is TourItem -> item.isFavorite
            else -> false
        }
    }

    fun getItemId(item: Any): String {
        return when (item) {
            is KulinerItem -> "${item.title}_${item.price}_kuliner"
            is EventItem -> "${item.title}_${item.price}_event"
            is TourItem -> "${item.title}_${item.price}_tour"
            else -> ""
        }
    }

    private fun getItemType(item: Any): String {
        return when (item) {
            is KulinerItem -> "kuliner"
            is EventItem -> "event"
            is TourItem -> "tour"
            else -> "unknown"
        }
    }

    private fun loadUserFavorites() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val favorites = snapshot?.documents?.map { it.id }?.toSet() ?: emptySet()
                _favoriteItems.value = favorites

                // Update local item states
                updateLocalItemStates(favorites)
            }
    }

    private fun updateLocalItemStates(favoriteIds: Set<String>) {
        // Update KulinerItem states
        DataProvider.kulinerItemLists.forEach { item ->
            item.isFavorite = favoriteIds.contains(getItemId(item))
        }

        // Update EventItem states
        DataProvider.eventItemLists.forEach { item ->
            item.isFavorite = favoriteIds.contains(getItemId(item))
        }

        // Update TourItem states
        DataProvider.tourItemLists.forEach { item ->
            item.isFavorite = favoriteIds.contains(getItemId(item))
        }
    }

    private suspend fun addToFirestoreFavorites(userId: String, itemId: String, itemType: String) {
        try {
            val favoriteData = hashMapOf(
                "itemId" to itemId,
                "itemType" to itemType,
                "addedAt" to FieldValue.serverTimestamp()
            )

            firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(itemId)
                .set(favoriteData)
        } catch (e: Exception) {
            // Handle error
        }
    }

    private suspend fun removeFromFirestoreFavorites(userId: String, itemId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(itemId)
                .delete()
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun getFavoriteItemsByType(type: String): List<Any> {
        val currentFavorites = _favoriteItems.value

        return when (type) {
            "kuliner" -> DataProvider.kulinerItemLists.filter {
                currentFavorites.contains(getItemId(it))
            }
            "event" -> DataProvider.eventItemLists.filter {
                currentFavorites.contains(getItemId(it))
            }
            "tour" -> DataProvider.tourItemLists.filter {
                currentFavorites.contains(getItemId(it))
            }
            else -> emptyList()
        }
    }

    fun getAllFavoriteItems(): List<Any> {
        val currentFavorites = _favoriteItems.value
        val allFavorites = mutableListOf<Any>()

        allFavorites.addAll(DataProvider.kulinerItemLists.filter {
            currentFavorites.contains(getItemId(it))
        })
        allFavorites.addAll(DataProvider.eventItemLists.filter {
            currentFavorites.contains(getItemId(it))
        })
        allFavorites.addAll(DataProvider.tourItemLists.filter {
            currentFavorites.contains(getItemId(it))
        })

        return allFavorites
    }

    fun clearAllFavorites() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                val batch = firestore.batch()

                firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("favorites")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            batch.delete(document.reference)
                        }
                        batch.commit()
                    }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}