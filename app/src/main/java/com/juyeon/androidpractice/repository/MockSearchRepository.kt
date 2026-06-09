package com.juyeon.androidpractice.repository

import com.juyeon.androidpractice.model.PostItem

class MockSearchRepository : SearchRepository {
    override fun getPosts() = listOf(
        PostItem(1, "첫 번째 게시글", "작성자1"),
        PostItem(2, "두 번째 게시글", "작성자2"),
        PostItem(3, "세 번째 게시글", "작성자3"),
        PostItem(4, "네 번째 게시글", "작성자4"),
        PostItem(5, "다섯 번째 게시글", "작성자5"),
        PostItem(6, "여섯 번째 게시글", "작성자6"),
        PostItem(7, "일곱 번째 게시글", "작성자7"),
    )
}
