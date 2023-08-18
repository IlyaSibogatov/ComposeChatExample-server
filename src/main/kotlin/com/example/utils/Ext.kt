package com.example.utils

import io.ktor.http.content.*
import java.io.File

object Ext {
    fun PartData.FileItem.save(path: String, uid: String): String {
        val fileBytes = this.streamProvider().readBytes()
        val fileExtension = originalFileName?.takeLastWhile { it != '.' }
        val fileName = "$uid.$fileExtension"
        val folder = File(path)
        folder.parentFile?.exists()?.let {
            folder.mkdir()
        } ?: {
            folder.parentFile.mkdir()
            folder.mkdir()
        }
        println("Path = $path$fileName")
        File("$path$fileName").writeBytes(fileBytes)
        return fileName
    }
}