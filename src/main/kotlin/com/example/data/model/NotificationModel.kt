package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationModel(
    val title: String,
    val body: String,
    val token: String,
)
