package com.epam.mentoring.services

import com.epam.mentoring.models.Comment
import com.epam.mentoring.models.Post
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*

class JsonPlaceholderServiceException(message: String, cause: Throwable? = null) : Exception(message, cause)

class JsonPlaceholderService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
        
        // Configure timeout
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
        }
        
        // Default headers
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
    
    private val baseUrl = "https://jsonplaceholder.typicode.com"
    
    suspend fun getAllPosts(): List<Post> {
        return client.get("$baseUrl/posts").body()
    }
    
    suspend fun getCommentsByUserId(userId: Int): List<Comment> {
        return client.get("$baseUrl/comments") {
            parameter("userId", userId)
        }.body()
    }
    
    suspend fun createPost(post: Post): Post {
        return client.post("$baseUrl/posts") {
            contentType(ContentType.Application.Json)
            setBody(post)
        }.body()
    }
    
    suspend fun updatePost(postId: Int, post: Post): Post {
        return client.put("$baseUrl/posts/$postId") {
            contentType(ContentType.Application.Json)
            setBody(post)
        }.body()
    }
    
    suspend fun deletePost(postId: Int) {
        client.delete("$baseUrl/posts/$postId")
    }
    
    fun close() {
        client.close()
    }
} 