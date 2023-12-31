package com.example.data.model.chat

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Chat(
    val name: String,
    val password: String,
    var owner: String,
    val ownerId: String,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString(),
)