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
import java.io.IOException

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
    
    suspend fun getAllPosts(): Result<List<Post>> {
        return try {
            val response: List<Post> = client.get("$baseUrl/posts").body()
            Result.success(response)
        } catch (e: ResponseException) {
            Result.failure(Exception("HTTP error: ${e.response.status}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
    
    suspend fun getCommentsByUserId(userId: Int): Result<List<Comment>> {
        return try {
            val response: List<Comment> = client.get("$baseUrl/comments") {
                parameter("userId", userId)
            }.body()
            Result.success(response)
        } catch (e: ResponseException) {
            Result.failure(Exception("HTTP error: ${e.response.status}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
    
    suspend fun createPost(post: Post): Result<Post> {
        return try {
            val response: Post = client.post("$baseUrl/posts") {
                contentType(ContentType.Application.Json)
                setBody(post)
            }.body()
            Result.success(response)
        } catch (e: ResponseException) {
            Result.failure(Exception("HTTP error: ${e.response.status}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
    
    suspend fun updatePost(postId: Int, post: Post): Result<Post> {
        return try {
            val response: Post = client.put("$baseUrl/posts/$postId") {
                contentType(ContentType.Application.Json)
                setBody(post)
            }.body()
            Result.success(response)
        } catch (e: ResponseException) {
            Result.failure(Exception("HTTP error: ${e.response.status}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
    
    suspend fun deletePost(postId: Int): Result<Unit> {
        return try {
            client.delete("$baseUrl/posts/$postId")
            Result.success(Unit)
        } catch (e: ResponseException) {
            Result.failure(Exception("HTTP error: ${e.response.status}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
    
    fun close() {
        client.close()
    }
} 