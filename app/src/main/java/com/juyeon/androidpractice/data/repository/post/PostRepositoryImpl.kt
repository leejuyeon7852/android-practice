package com.juyeon.androidpractice.data.repository.post

import com.juyeon.androidpractice.data.db.dao.PostDao
import com.juyeon.androidpractice.data.db.entity.Post
import kotlinx.coroutines.flow.Flow

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {

    override fun getAllPosts(): Flow<List<Post>> = postDao.getAllPosts()

    override fun getPostsByAuthor(authorId: Int): Flow<List<Post>> =
        postDao.getPostsByAuthor(authorId)

    override suspend fun getPostById(postId: Int): Post? = postDao.getPostById(postId)

    override suspend fun savePost(post: Post): Long = postDao.insert(post)

    override suspend fun updatePost(post: Post) = postDao.update(post)

    override suspend fun deletePost(postId: Int) = postDao.deletePost(postId)
}
