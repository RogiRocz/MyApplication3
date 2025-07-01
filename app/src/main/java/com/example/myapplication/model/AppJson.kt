package com.example.myapplication.utils

import kotlinx.serialization.json.Json

val AppJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
}