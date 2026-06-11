package com.juyeon.androidpractice.repository.home

import com.juyeon.androidpractice.model.BannerItem
import com.juyeon.androidpractice.model.CardItem

class MockHomeRepository : HomeRepository {
    override fun getBanners() = listOf(
        BannerItem(1, "첫 번째 배너", "배너 설명입니다"),
        BannerItem(2, "두 번째 배너", "배너 설명입니다"),
        BannerItem(3, "세 번째 배너", "배너 설명입니다"),
    )

    override fun getCards() = listOf(
        CardItem(1, "카드 1", "https://picsum.photos/200/200?random=1"),
        CardItem(2, "카드 2", "https://picsum.photos/200/200?random=2"),
        CardItem(3, "카드 3", "https://picsum.photos/200/200?random=3"),
        CardItem(4, "카드 4", "https://picsum.photos/200/200?random=4"),
        CardItem(5, "카드 5", "https://picsum.photos/200/200?random=5"),
        CardItem(6, "카드 6", "https://picsum.photos/200/200?random=6"),
        CardItem(7, "카드 7", "https://picsum.photos/200/200?random=7"),
        CardItem(8, "카드 8", "https://picsum.photos/200/200?random=8"),
        CardItem(9, "카드 9", "https://picsum.photos/200/200?random=9"),
    )
}