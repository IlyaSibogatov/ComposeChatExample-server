package com.example.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Chat(
    val name: String,
    val password: String,
    val owner: String,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString(),
)