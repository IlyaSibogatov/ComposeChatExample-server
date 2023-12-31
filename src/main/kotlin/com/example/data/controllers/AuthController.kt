package com.example.data.controllers

import com.example.data.model.user.UserDTO
import com.example.data.source.AuthDataSource

class AuthController(
    private val authDataSource: AuthDataSource,
) {

    suspend fun regUser(userCredentials: UserDTO): String =
        authDataSource.regUser(userCredentials)

    suspend fun login(userCredentials: UserDTO): String? =
        authDataSource.login(userCredentials)

    suspend fun logout(uid: String): Boolean =
        authDataSource.logout(uid)

    suspend fun changePass(current: String, new: String, uuid: String): String =
        authDataSource.changePass(current, new, uuid)

    suspend fun deleteAcc(uuid: String) =
        authDataSource.deleteAcc(uuid)
}