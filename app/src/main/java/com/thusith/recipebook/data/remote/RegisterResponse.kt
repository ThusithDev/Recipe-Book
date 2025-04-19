package com.thusith.recipebook.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    @SerialName("id") val id: Int,
    @SerialName("token") val token: String
)
