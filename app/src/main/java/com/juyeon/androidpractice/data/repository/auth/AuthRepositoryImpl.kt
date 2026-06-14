package com.juyeon.androidpractice.data.repository.auth

import com.juyeon.androidpractice.data.db.dao.UserDao
import com.juyeon.androidpractice.data.db.entity.User

class AuthRepositoryImpl(private val userDao: UserDao) : AuthRepository {

    override suspend fun login(userId: String, password: String): User? =
        userDao.findByCredentials(userId, password)

    override suspend fun signup(user: User): Boolean {
        if (userDao.findByUserId(user.userId) != null) return false
        userDao.insert(user)
        return true
    }

    override suspend fun updateUser(user: User) = userDao.update(user)
}
