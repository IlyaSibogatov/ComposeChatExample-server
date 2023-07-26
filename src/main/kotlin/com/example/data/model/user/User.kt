package com.example.data.model.user

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    val username: String,
    val password: String,
    val selfInfo: String,
    var onlineStatus: Boolean,
    var lastActionTime: Long,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString(),
)