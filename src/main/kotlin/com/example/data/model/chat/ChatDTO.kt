package com.example.data.model.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatDTO(
    val name: String,
    val password: String = "",
    val owner: String,
    val ownerId: String,
    val id: String = "",
)