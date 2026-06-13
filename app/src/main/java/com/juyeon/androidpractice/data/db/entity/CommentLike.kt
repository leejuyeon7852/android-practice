package com.juyeon.androidpractice.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "comment_likes",
    primaryKeys = ["userId", "commentId"],
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Comment::class, parentColumns = ["id"], childColumns = ["commentId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CommentLike(
    val userId: Int,
    val commentId: Int,
)
