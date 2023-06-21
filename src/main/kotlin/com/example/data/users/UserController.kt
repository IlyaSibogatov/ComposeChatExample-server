package com.example.data.users

import com.example.data.model.user.UserDTO
import com.example.data.source.UserDataSource

class UserController(
    private val userDataSource: UserDataSource
) {

    suspend fun regUser(userCredentials: UserDTO): Boolean =
        userDataSource.regUser(userCredentials)

    suspend fun login(userCredentials: UserDTO): Boolean =
        userDataSource.login(userCredentials)
}