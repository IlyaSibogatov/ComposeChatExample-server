package com.example.data.response

import kotlinx.serialization.Serializable

@Serializable
data class DefaultResponse(
    val msg: String,
    val status: Int,
)