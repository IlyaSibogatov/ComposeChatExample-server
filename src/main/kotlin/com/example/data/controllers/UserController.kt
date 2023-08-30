package com.example.data.controllers

import com.example.data.model.user.Friend
import com.example.data.model.user.NewUserInfo
import com.example.data.model.user.UserFromId
import com.example.data.source.UserDataSource

class UserController(
    private val userDataSource: UserDataSource,
) {
    suspend fun getUserById(uid: String): UserFromId? =
        userDataSource.getUserById(uid)

    suspend fun addToFriends(selfId: String, userId: String): Boolean =
        userDataSource.friendshipRequest(selfId, userId)

    suspend fun friendshipAccept(selfId: String, userId: String, accept: Boolean) =
        userDataSource.acceptFriendship(selfId, userId, accept)

    suspend fun removeFromFriends(selfId: String, userId: String, selfRemoving: Boolean) =
        userDataSource.removeFromFriends(selfId, userId, selfRemoving)

    suspend fun getFollowerFriends(uid: String, type: String): List<Friend> =
        userDataSource.getFollowerFriends(uid, type)

    suspend fun updateUser(newInfo: NewUserInfo) =
        userDataSource.updateUser(newInfo)

}