package com.dev.lokabudaya.pages.Book

import androidx.lifecycle.ViewModel
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.KulinerItem
import com.dev.lokabudaya.data.TourItem

class FavoriteViewModel : ViewModel() {
    fun toggleFavorite(item: Any) {
        when (item) {
            is KulinerItem -> {
                item.isFavorite = !item.isFavorite
            }
            is EventItem -> {
                item.isFavorite = !item.isFavorite
            }
            is TourItem -> {
                item.isFavorite = !item.isFavorite
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
}
