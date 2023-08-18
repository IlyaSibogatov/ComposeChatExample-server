package com.example.data.controllers

import com.example.data.model.user.UserDTO
import com.example.data.source.AuthDataSource

class AuthController(
    private val authDataSource: AuthDataSource,
) {

    suspend fun regUser(userCredentials: UserDTO): String? =
        authDataSource.regUser(userCredentials)

    suspend fun login(userCredentials: UserDTO): String? =
        authDataSource.login(userCredentials)

    suspend fun logout(uid: String): Boolean =
        authDataSource.logout(uid)
}