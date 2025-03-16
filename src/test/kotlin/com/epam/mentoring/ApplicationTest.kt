package com.epam.mentoring

import com.epam.mentoring.models.Post
import com.epam.mentoring.services.JsonPlaceholderService
import com.epam.mentoring.services.JsonPlaceholderServiceException
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    
    private fun createTestApp(test: suspend ApplicationTestBuilder.(HttpClient) -> Unit) = testApplication {
        environment {
            developmentMode = true
        }
        
        application {
            module()
        }

        // Configure the test client with content negotiation
        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }
        
        test(client)
    }

    @Test
    fun testRoot() = runTest {
        createTestApp { client: HttpClient ->
            val response = client.get("/")
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("Ktor sample application"))
        }
    }

    @Test
    fun testGetAllPosts() = runTest {
        createTestApp { client: HttpClient ->
            val response = client.get("/api/posts")
            assertEquals(HttpStatusCode.OK, response.status)
            val responseText = response.bodyAsText()
            assertTrue(responseText.contains("title"))
            assertTrue(responseText.contains("body"))
        }
    }

    @Test
    fun testGetCommentsByUserId() = runTest {
        createTestApp { client: HttpClient ->
            val response = client.get("/api/comments?userId=1")
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun testInvalidUserIdParameter() = runTest {
        createTestApp { client: HttpClient ->
            val response = client.get("/api/comments")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            val responseText = response.bodyAsText()
            assertTrue(responseText.contains("Missing or invalid userId parameter"))
        }
    }

    @Test
    fun testCreatePost() = runTest {
        createTestApp { client: HttpClient ->
            val testPost = Post(
                userId = 1,
                title = "Test Title",
                body = "Test Body"
            )
            
            val response = client.post("/api/posts") {
                contentType(ContentType.Application.Json)
                setBody(testPost)
            }
            assertEquals(HttpStatusCode.Created, response.status)
            val responseText = response.bodyAsText()
            assertTrue(responseText.contains("Test Title"))
            assertTrue(responseText.contains("Test Body"))
        }
    }

    @Test
    fun testUpdatePost() = runTest {
        createTestApp { client: HttpClient ->
            val testPost = Post(
                id = 1,
                userId = 1,
                title = "Updated Title",
                body = "Updated Body"
            )
            
            val response = client.put("/api/posts/1") {
                contentType(ContentType.Application.Json)
                setBody(testPost)
            }
            assertEquals(HttpStatusCode.OK, response.status)
            val responseText = response.bodyAsText()
            assertTrue(responseText.contains("Updated Title"))
            assertTrue(responseText.contains("Updated Body"))
        }
    }

    @Test
    fun testDeletePost() = runTest {
        createTestApp { client: HttpClient ->
            val response = client.delete("/api/posts/1")
            assertEquals(HttpStatusCode.NoContent, response.status)
        }
    }

    @Test
    fun testInvalidPostId() = runTest {
        createTestApp { client: HttpClient ->
            val response = client.delete("/api/posts/invalid")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            val responseText = response.bodyAsText()
            assertTrue(responseText.contains("Invalid post ID"))
        }
    }

    @Test
    fun testInvalidJsonBody() = runTest {
        createTestApp { client: HttpClient ->
            val response = client.post("/api/posts") {
                contentType(ContentType.Application.Json)
                setBody("""{"userId": 1, "title": "Test", body: "Missing quotes"}""")
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }
} 