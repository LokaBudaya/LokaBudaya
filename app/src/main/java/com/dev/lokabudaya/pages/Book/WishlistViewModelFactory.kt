package com.dev.lokabudaya.pages.Book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dev.lokabudaya.pages.Auth.AuthViewModel

class FavoriteViewModelFactory(
    private val authViewModel: AuthViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}