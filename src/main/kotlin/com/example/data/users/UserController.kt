package com.example.data.users

import com.example.data.model.user.UserDTO
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

    suspend fun getUserById(uid: String) =
        userDataSource.getUserById(uid)
}