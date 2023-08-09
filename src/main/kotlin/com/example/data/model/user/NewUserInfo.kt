package com.example.data.model.user

import kotlinx.serialization.Serializable

@Serializable
data class NewUserInfo(
    val id: String,
    val username: String,
    val selfInfo: String,
)