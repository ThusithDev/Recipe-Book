package com.thusith.recipebook.data.remote

import com.thusith.recipebook.data.local.UserDao
import com.thusith.recipebook.data.local.UserEntity
import com.thusith.recipebook.model.RegisterRequest
import com.thusith.recipebook.model.LoginRequest
import com.thusith.recipebook.model.UserResponse
import com.thusith.recipebook.utils.Resource

class AuthRepository(private val api: ApiService, private val userDao: UserDao) {

    suspend fun register(name: String, email: String, request: RegisterRequest): Resource<UserResponse> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    // Save user with all auth data
                    val user = UserEntity(
                        name = name,
                        email = email,
                        token = apiResponse.token
                    )
                    userDao.insertUser(user)
                    Resource.Success(apiResponse)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Registration failed: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    suspend fun login(request: LoginRequest): Resource<UserResponse> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->

                    val currentUser = userDao.getUser()
                    val user = UserEntity(
                        id = currentUser?.id ?: 0,
                        name = currentUser?.name ?: request.email.split("@").first(),
                        email = request.email,
                        token = apiResponse.token
                    )
                    userDao.insertUser(user)
                    Resource.Success(apiResponse)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Login failed: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }
    suspend fun logout() {
        userDao.clearUsers()
    }
}
