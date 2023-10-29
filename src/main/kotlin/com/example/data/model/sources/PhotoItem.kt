package com.example.data.model.sources

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class PhotoItem(
    @BsonId
    val id: String = ObjectId().toString(),
    val description: String,
)
