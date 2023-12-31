package com.example.data.model.chat

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class MessageHistory(
    val name: String,
    var users: List<String>,
    var messages: List<Message>,
    @BsonId
    val id: String = ObjectId().toString()
)

@Serializable
data class Message(
    var message: String,
    val userId: String,
    val timestamp: Long,
    var wasEdit: Boolean,
    @BsonId
    val id: String = ObjectId().toString()
)