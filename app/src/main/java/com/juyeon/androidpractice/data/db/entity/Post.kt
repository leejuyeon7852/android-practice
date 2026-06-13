package com.juyeon.androidpractice.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["authorId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val authorId: Int,
    val authorNickname: String,
    val title: String,
    val body: String,
    val imageUri: String? = null,
    val createdAt: String,
)
