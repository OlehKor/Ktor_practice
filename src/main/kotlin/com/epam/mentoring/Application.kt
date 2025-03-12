package com.epam.mentoring

import com.epam.mentoring.models.Post
import com.epam.mentoring.services.JsonPlaceholderService
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

// This function is referenced in application.conf
fun Application.main() {
    module()
}

fun Application.module() {
    // Install the ContentNegotiation feature with Jackson for JSON serialization
    install(ContentNegotiation) {
        jackson()
    }
    
    // Create an instance of JsonPlaceholderService
    val jsonPlaceholderService = JsonPlaceholderService()
    
    // Define our routing
    routing {
        // Base endpoint
        get("/") {
            call.respondText("This is a Ktor sample application for JsonPlaceholder API.")
        }
        
        // API endpoints
        route("/api") {
            // Get all posts
            get("/posts") {
                jsonPlaceholderService.getAllPosts().fold(
                    onSuccess = { posts ->
                        call.respond(posts)
                    },
                    onFailure = { error ->
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to error.message))
                    }
                )
            }
            
            // Get comments by user ID
            get("/comments") {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing or invalid userId parameter"))
                    return@get
                }
                
                jsonPlaceholderService.getCommentsByUserId(userId).fold(
                    onSuccess = { comments ->
                        call.respond(comments)
                    },
                    onFailure = { error ->
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to error.message))
                    }
                )
            }
            
            // Create a new post
            post("/posts") {
                try {
                    val post = call.receive<Post>()
                    jsonPlaceholderService.createPost(post).fold(
                        onSuccess = { createdPost ->
                            call.respond(HttpStatusCode.Created, createdPost)
                        },
                        onFailure = { error ->
                            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to error.message))
                        }
                    )
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid post data: ${e.message}"))
                }
            }
            
            // Update an existing post
            put("/posts/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid post ID"))
                    return@put
                }
                
                try {
                    val post = call.receive<Post>()
                    jsonPlaceholderService.updatePost(id, post).fold(
                        onSuccess = { updatedPost ->
                            call.respond(updatedPost)
                        },
                        onFailure = { error ->
                            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to error.message))
                        }
                    )
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid post data: ${e.message}"))
                }
            }
            
            // Delete a post
            delete("/posts/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid post ID"))
                    return@delete
                }
                
                jsonPlaceholderService.deletePost(id).fold(
                    onSuccess = {
                        call.respond(HttpStatusCode.NoContent)
                    },
                    onFailure = { error ->
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to error.message))
                    }
                )
            }
        }
    }
}
