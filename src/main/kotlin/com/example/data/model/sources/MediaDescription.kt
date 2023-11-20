package com.example.data.model.sources

import kotlinx.serialization.Serializable

@Serializable
data class MediaDescription(
    val id: String,
    val name: String,
    val description: String,
)