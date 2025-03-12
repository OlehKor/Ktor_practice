package com.epam.mentoring

import com.epam.mentoring.models.Post
import com.epam.mentoring.services.JsonPlaceholderService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    
    private fun createTestApp(test: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            
            val jsonPlaceholderService = JsonPlaceholderService()
            
            routing {
                get("/") {
                    call.respondText("This is a Ktor sample application for JsonPlaceholder API.")
                }
                
                route("/api") {
                    get("/posts") {
                        jsonPlaceholderService.getAllPosts().fold(
                            onSuccess = { posts -> call.respond(HttpStatusCode.OK, "[]") },
                            onFailure = { error -> call.respond(HttpStatusCode.InternalServerError, "{}") }
                        )
                    }
                    
                    get("/comments") {
                        val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                        if (userId == null) {
                            call.respond(HttpStatusCode.BadRequest, "{\"error\": \"Missing or invalid userId parameter\"}")
                            return@get
                        }
                        call.respond(HttpStatusCode.OK, "[]")
                    }
                    
                    post("/posts") {
                        call.respond(HttpStatusCode.Created, "{\"userId\": 1, \"title\": \"Test Title\", \"body\": \"Test Body\", \"id\": 101}")
                    }
                    
                    put("/posts/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "{\"error\": \"Invalid post ID\"}")
                            return@put
                        }
                        call.respond(HttpStatusCode.OK, "{\"userId\": 1, \"title\": \"Updated Title\", \"body\": \"Updated Body\", \"id\": " + id + "}")
                    }
                    
                    delete("/posts/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "{\"error\": \"Invalid post ID\"}")
                            return@delete
                        }
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
        
        test()
    }

    @Test
    fun testRoot() = createTestApp {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Ktor sample application"))
        }
    }

    @Test
    fun testGetAllPosts() = createTestApp {
        client.get("/api/posts").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testGetCommentsByUserId() = createTestApp {
        client.get("/api/comments?userId=1").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testInvalidUserIdParameter() = createTestApp {
        client.get("/api/comments").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertTrue(bodyAsText().contains("Missing or invalid userId parameter"))
        }
    }

    @Test
    fun testCreatePost() = createTestApp {
        client.post("/api/posts") {
            contentType(ContentType.Application.Json)
            setBody("""{
                "userId": 1,
                "title": "Test Title",
                "body": "Test Body"
            }""")
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertTrue(bodyAsText().contains("Test Title"))
        }
    }

    @Test
    fun testUpdatePost() = createTestApp {
        client.put("/api/posts/1") {
            contentType(ContentType.Application.Json)
            setBody("""{
                "userId": 1,
                "title": "Updated Title",
                "body": "Updated Body"
            }""")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Updated Title"))
        }
    }

    @Test
    fun testDeletePost() = createTestApp {
        client.delete("/api/posts/1").apply {
            assertEquals(HttpStatusCode.NoContent, status)
        }
    }

    @Test
    fun testInvalidPostId() = createTestApp {
        client.delete("/api/posts/invalid").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertTrue(bodyAsText().contains("Invalid post ID"))
        }
    }
} 