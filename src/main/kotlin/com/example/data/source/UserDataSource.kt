package com.example.data.source

import com.example.data.model.user.UserDTO

interface UserDataSource {

    suspend fun regUser(userCredentials: UserDTO): Boolean

    suspend fun login(userCredentials: UserDTO): Boolean
}