package com.juyeon.androidpractice.data.db.entity

import androidx.room.Entity

@Entity(tableName = "follows", primaryKeys = ["followerId", "followeeId"])
data class Follow(
    val followerId: Int,
    val followeeId: Int
)
