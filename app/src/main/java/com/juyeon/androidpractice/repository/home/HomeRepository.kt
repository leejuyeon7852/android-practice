package com.juyeon.androidpractice.repository.home

import com.juyeon.androidpractice.model.BannerItem
import com.juyeon.androidpractice.model.CardItem

interface HomeRepository {
    fun getBanners(): List<BannerItem>
    fun getCards(): List<CardItem>
}