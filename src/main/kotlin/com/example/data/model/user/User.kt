package com.example.data.model.user

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val username: String,
    val password: String,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString(),
)
