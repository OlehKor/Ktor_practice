package com.epam.mentoring

import com.epam.mentoring.models.Post
import com.epam.mentoring.services.JsonPlaceholderService
import com.epam.mentoring.services.JsonPlaceholderServiceException
import com.fasterxml.jackson.core.JsonParseException
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.client.plugins.*
import io.ktor.server.plugins.*
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
    try {
        install(ContentNegotiation) {
            jackson()
        }
    } catch (e: Exception) {
        // Plugin is already installed, ignore the error
    }

    // Install Status Pages for centralized error handling
    try {
        install(StatusPages) {
            exception<JsonPlaceholderServiceException> { call, cause ->
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf("error" to (cause.message ?: "An error occurred"))
                )
            }
            exception<ResponseException> { call, cause ->
                call.respond(
                    status = HttpStatusCode.fromValue(cause.response.status.value),
                    message = mapOf("error" to "HTTP error: ${cause.response.status}")
                )
            }
            exception<IllegalArgumentException> { call, cause ->
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to (cause.message ?: "Bad request"))
                )
            }
            exception<BadRequestException> { call, cause ->
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to (cause.message ?: "Invalid request format"))
                )
            }
            exception<Exception> { call, cause ->
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = mapOf("error" to (cause.message ?: "An unexpected error occurred"))
                )
            }
        }
    } catch (e: Exception) {
        // Plugin is already installed, ignore the error
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
                val posts = jsonPlaceholderService.getAllPosts()
                call.respond(posts)
            }
            
            // Get comments by user ID
            get("/comments") {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Missing or invalid userId parameter")
                
                val comments = jsonPlaceholderService.getCommentsByUserId(userId)
                call.respond(comments)
            }
            
            // Create a new post
            post("/posts") {
                val post = call.receive<Post>()
                val createdPost = jsonPlaceholderService.createPost(post)
                call.respond(HttpStatusCode.Created, createdPost)
            }
            
            // Update an existing post
            put("/posts/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Invalid post ID")
                
                val post = call.receive<Post>()
                val updatedPost = jsonPlaceholderService.updatePost(id, post)
                call.respond(updatedPost)
            }
            
            // Delete a post
            delete("/posts/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Invalid post ID")
                
                jsonPlaceholderService.deletePost(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
