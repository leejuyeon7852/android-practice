package com.juyeon.androidpractice.ui.home

import androidx.lifecycle.ViewModel
import com.juyeon.androidpractice.model.BannerItem
import com.juyeon.androidpractice.model.CardItem
import com.juyeon.androidpractice.repository.home.MockHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val repository = MockHomeRepository()

    private val _banners = MutableStateFlow<List<BannerItem>>(emptyList())
    val banners: StateFlow<List<BannerItem>> = _banners

    private val _cards = MutableStateFlow<List<CardItem>>(emptyList())
    val cards: StateFlow<List<CardItem>> = _cards

    init {
        _banners.value = repository.getBanners()
        _cards.value = repository.getCards()
    }
}
