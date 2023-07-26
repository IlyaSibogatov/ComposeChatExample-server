package com.example.plugins

import com.example.session.ChatSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(ApplicationCallPipeline.Features) {
        if (call.sessions.get<ChatSession>() == null) {
            val username = call.parameters["username"] ?: "Guest"
            val userId = call.parameters["userId"] ?: "0"
            val chatId = call.parameters["chatId"] ?: "0"
            call.sessions.set(
                ChatSession(
                    username = username,
                    userId = userId,
                    sessionId = generateNonce(),
                    chatId = chatId,
                )
            )
        }
    }
}