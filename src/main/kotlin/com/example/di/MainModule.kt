package com.example.di

import com.example.data.chat.ChatController
import com.example.data.controllers.RoomController
import com.example.data.source.*
import com.example.data.controllers.AuthController
import com.example.data.controllers.UserController
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase("chat_db")
    }
    single<AuthDataSource> {
        AuthDataSourceImpl(get())
    }
    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }
    single<ChatDataSource> {
        ChatDataSourceImpl(get())
    }
    single<UserDataSource> {
        UserDataSourceImpl(get())
    }
    single {
        AuthController(get())
    }
    single {
        RoomController(get())
    }
    single {
        ChatController(get())
    }
    single {
        UserController(get())
    }
}