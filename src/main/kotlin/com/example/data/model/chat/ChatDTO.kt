package com.example.data.model.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatDTO(
    val id: String,
    val name: String,
    val password: String = "",
    val owner: String,
)