package com.example.data.source

import com.example.data.model.user.Friend
import com.example.data.model.user.NewUserInfo
import com.example.data.model.user.UserFromId

interface UserDataSource {
    suspend fun getUserById(uid: String): UserFromId?

    suspend fun friendshipRequest(selfId: String, userId: String): Boolean

    suspend fun acceptFriendship(selfId: String, userId: String, accept: Boolean)

    suspend fun removeFromFriends(selfId: String, userId: String, selfRemoving: Boolean)

    suspend fun getFollowerFriends(uid: String, type: String): List<Friend>

    suspend fun updateUser(newInfo: NewUserInfo): Boolean
}