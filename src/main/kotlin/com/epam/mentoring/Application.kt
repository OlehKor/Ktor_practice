package com.epam.mentoring

import io.ktor.server.application.log
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main() {
    // Create an embedded server using Netty
    embeddedServer(Netty, port = 8080) {
        // Define our routing
        routing {
            get("/") {
                // Respond with "Hello, World!"
                log.debug("Received request to /")
                call.respondText("This is a Ktor sample application.")
            }
            // Handle GET requests to /hello
            get("/hello") {
                // Respond with "Hello, World!"
                log.debug("Received request to /hello")
                call.respondText("Hello, World!")
            }
        }
    }.start(wait = true) // Start the server and wait for it to finish
}
