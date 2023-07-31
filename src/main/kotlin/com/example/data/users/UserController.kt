package com.example.data.users

import com.example.data.model.user.Friend
import com.example.data.model.user.UserDTO
import com.example.data.model.user.UserFromId
import com.example.data.source.UserDataSource

class UserController(
    private val userDataSource: UserDataSource
) {

    suspend fun regUser(userCredentials: UserDTO): String? =
        userDataSource.regUser(userCredentials)

    suspend fun login(userCredentials: UserDTO): String? =
        userDataSource.login(userCredentials)

    suspend fun logout(uid: String): Boolean =
        userDataSource.logout(uid)

    suspend fun getUserById(uid: String): UserFromId? =
        userDataSource.getUserById(uid)

    suspend fun addToFriends(selfId: String, userId: String): Boolean =
        userDataSource.friendshipRequest(selfId, userId)

    suspend fun friendshipAccept(selfId: String, userId: String, accept: Boolean) =
        userDataSource.acceptFriendship(selfId, userId, accept)

    suspend fun getFollowerFriends(uid: String, type: String): List<Friend> =
        userDataSource.getFollowerFriends(uid, type)
}