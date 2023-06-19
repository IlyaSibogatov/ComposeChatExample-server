package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatDTO(
    val name: String,
    val password: String = "",
    val owner: String,
)