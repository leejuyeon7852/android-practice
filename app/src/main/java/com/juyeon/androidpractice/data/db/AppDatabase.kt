package com.juyeon.androidpractice.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.juyeon.androidpractice.data.db.dao.AppNotificationDao
import com.juyeon.androidpractice.data.db.dao.CommentDao
import com.juyeon.androidpractice.data.db.dao.CommentLikeDao
import com.juyeon.androidpractice.data.db.dao.LikeDao
import com.juyeon.androidpractice.data.db.dao.PostDao
import com.juyeon.androidpractice.data.db.dao.ScrapDao
import com.juyeon.androidpractice.data.db.dao.UserDao
import com.juyeon.androidpractice.data.db.entity.AppNotification
import com.juyeon.androidpractice.data.db.entity.Comment
import com.juyeon.androidpractice.data.db.entity.CommentLike
import com.juyeon.androidpractice.data.db.entity.Like
import com.juyeon.androidpractice.data.db.entity.Post
import com.juyeon.androidpractice.data.db.entity.Scrap
import com.juyeon.androidpractice.data.db.entity.User

@Database(
    entities = [User::class, Post::class, Comment::class, CommentLike::class, Like::class, Scrap::class, AppNotification::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun commentLikeDao(): CommentLikeDao
    abstract fun likeDao(): LikeDao
    abstract fun scrapDao(): ScrapDao
    abstract fun notificationDao(): AppNotificationDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
    }
}
