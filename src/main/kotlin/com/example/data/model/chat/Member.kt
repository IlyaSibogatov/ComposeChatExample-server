package com.example.data.model.chat

import io.ktor.websocket.*

data class Member(
    val username: String,
    val userId: String,
    val sessionId: String,
    val socket: WebSocketSession,
    val chatId: String,
)