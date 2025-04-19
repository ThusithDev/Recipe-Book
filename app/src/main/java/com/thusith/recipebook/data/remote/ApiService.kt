package com.thusith.recipebook.data.remote

import com.thusith.recipebook.model.LoginRequest
import com.thusith.recipebook.model.RegisterRequest
import com.thusith.recipebook.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<UserResponse>
}
