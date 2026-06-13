package com.juyeon.androidpractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.juyeon.androidpractice.data.db.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE userId = :userId AND password = :password LIMIT 1")
    suspend fun findByCredentials(userId: String, password: String): User?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun findByUserId(userId: String): User?
}
